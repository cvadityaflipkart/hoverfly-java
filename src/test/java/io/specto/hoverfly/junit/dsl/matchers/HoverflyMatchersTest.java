package io.specto.hoverfly.junit.dsl.matchers;


import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import io.specto.hoverfly.junit.dsl.HoverflyDslException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HoverflyMatchersTest {


    @Test
    public void matchesShouldCreateGlobMatcher() {

        RequestFieldMatcher matcher = HoverflyMatchers.matches("fo*o");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.GLOB);
        assertThat(matcher.getValue()).isEqualTo("fo*o");
    }

    @Test
    public void goRegexMatchesShouldCreateRegexMatcher() {
        RequestFieldMatcher matcher = HoverflyMatchers.matchesGoRegex("r([a-z]+)go");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.REGEX);
        assertThat(matcher.getValue()).isEqualTo("r([a-z]+)go");
    }

    @Test
    public void startsWithShouldCreateRegexMatcher() {
        RequestFieldMatcher matcher = HoverflyMatchers.startsWith("foo");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.REGEX);
        assertThat(matcher.getValue()).isEqualTo("^foo.*");
    }


    @Test
    public void endsWithShouldCreateRegexMatcher() {
        RequestFieldMatcher matcher = HoverflyMatchers.endsWith("foo");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.REGEX);
        assertThat(matcher.getValue()).isEqualTo(".*foo$");
    }

    @Test
    public void containsShouldCreateRegexMatcher() {
        RequestFieldMatcher matcher = HoverflyMatchers.contains("foo");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.REGEX);
        assertThat(matcher.getValue()).isEqualTo(".*foo.*");
    }

    @Test
    public void anyShouldCreateRegexMatcher() {
        RequestFieldMatcher matcher = HoverflyMatchers.any();

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.REGEX);
        assertThat(matcher.getValue()).isEqualTo(".*");
    }

    @Test
    public void shouldCreateJsonMatcherFromString() {
        RequestFieldMatcher matcher = HoverflyMatchers.equalsToJson("{\"flightId\":\"1\",\"class\":\"PREMIUM\"}");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.JSON);
        assertThat(matcher.getValue()).isEqualTo("{\"flightId\":\"1\",\"class\":\"PREMIUM\"}");
    }

    @Test
    public void shouldThrowExceptionIfInputStringIsInvalidJsonFormat() {

        assertThatThrownBy(() -> HoverflyMatchers.equalsToJson("{\"flightId\":\"1\",\"class\":\"PREMIUM\""))
                .isInstanceOf(HoverflyDslException.class)
                .hasMessageContaining("Fail to create JSON matcher from invalid JSON string");

    }

    @Test
    public void shouldCreateJsonMatcherFromJSONObject() throws Exception {

        JSONObject object = new JSONObject().put("id", 1);

        RequestFieldMatcher matcher = HoverflyMatchers.equalsToJson(object.toString());

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.JSON);
        assertThat(matcher.getValue()).isEqualTo("{\"id\":1}");

    }

    @Test
    public void shouldCreateXmlMatcherFromString() {
        RequestFieldMatcher matcher = HoverflyMatchers.equalsToXml("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <flightId>1</flightId> <class>PREMIUM</class>");

        assertThat(matcher.getMatcher()).isEqualTo(RequestFieldMatcher.MatcherType.XML);
        assertThat(matcher.getValue()).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <flightId>1</flightId> <class>PREMIUM</class>");
    }

    @Test
    public void shouldThrowExceptionIfInputStringIsInvalidXmlFormat() {

        assertThatThrownBy(() -> HoverflyMatchers.equalsToXml("{\"flightId\":\"1\",\"class\":\"PREMIUM\""))
                .isInstanceOf(HoverflyDslException.class)
                .hasMessageContaining("Fail to create XML matcher from invalid XML string");

    }
}