package io.specto.hoverfly.ruletest;

import com.google.common.collect.ImmutableList;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.created;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.noContent;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;

public class StatefulCaptureTest {

    @ClassRule
    public static HoverflyRule hoverflyRuleForSimulation = HoverflyRule.inSimulationMode(dsl(
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


    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("stateful-capture.json", HoverflyConfig.localConfigs().proxyLocalHost().enableStatefulCapture());

    private final RestTemplate restTemplate = new RestTemplate();
    private String todoApi;

    @Before
    public void setup(){
        todoApi = "http://localhost:" + hoverflyRuleForSimulation.getProxyPort() + "/items";
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
