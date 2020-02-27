package io.specto.hoverfly.testng;

import com.google.common.collect.ImmutableList;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers;
import io.specto.hoverfly.testng.api.TestNGClassRule;
import io.specto.hoverfly.testng.api.TestNGRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(HoverflyExecutor.class)
public class StatefulCaptureTest {

    @TestNGClassRule
    public static HoverflyTestNG hoverflyTestNGForSimulation = HoverflyTestNG.inSimulationMode(dsl(
            service(HoverflyMatchers.contains("localhost"))
                    .get("/items")
                    .willReturn(success(json(new ToDoList(ImmutableList.of("book flight")))))
                    .post("/items")
                    .body(json(new ToDoList(ImmutableList.of("plan trip"))))
                    .willReturn(created().andSetState("event", "new item added"))
                    .get("/items")
                    .withState("event", "new item added")
                    .willReturn(success(json(new ToDoList(ImmutableList.of("book flight", "plan trip")))))
                    .delete("/items")
                    .willReturn(noContent().andSetState("event", "all items deleted"))
                    .get("/items")
                    .withState("event", "all items deleted")
                    .willReturn(success())
    ), HoverflyConfig.localConfigs().proxyPort(51322).asWebServer()).printSimulationData();


    @TestNGRule
    public HoverflyTestNG hoverflyTestNG = HoverflyTestNG.inCaptureOrSimulationMode("stateful-capture.json", HoverflyConfig.localConfigs().proxyLocalHost().enableStatefulCapture());

    private final RestTemplate restTemplate = new RestTemplate();
    private String todoApi;

    @BeforeMethod
    public void setup() {
        todoApi = "http://localhost:" + hoverflyTestNGForSimulation.getProxyPort() + "/items";
    }

    @Test
    public void shouldBeAbleToSimulateUsingStatefulCaptureData() throws URISyntaxException {
        final ResponseEntity<ToDoList> initialGet = restTemplate.getForEntity(todoApi, ToDoList.class);
        assertThat(initialGet.getBody().getItems()).containsOnly("book flight");

        RequestEntity<ToDoList> postRequest = RequestEntity.post(new URI(todoApi))
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ToDoList(ImmutableList.of("plan trip")));
        ResponseEntity<Void> postItem = restTemplate.exchange(postRequest, Void.class);
        assertThat(postItem.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        final ResponseEntity<ToDoList> secondGet = restTemplate.getForEntity(todoApi, ToDoList.class);
        assertThat(secondGet.getBody().getItems()).containsOnly("book flight", "plan trip");

        restTemplate.delete(todoApi);

        final ResponseEntity<Void> thirdGet = restTemplate.getForEntity(todoApi, Void.class);
        assertThat(thirdGet.getBody()).isNull();
    }


    static class ToDoList {
        private List<String> items;

        public ToDoList() {
        }

        ToDoList(List<String> items) {
            this.items = items;
        }


        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }
}
