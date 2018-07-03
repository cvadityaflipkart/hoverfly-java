package io.specto.hoverfly.junit.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModeArguments {

    private List<String> headersWhitelist;
    private String matchingStrategy;
    private boolean stateful;

    public ModeArguments() {
    }

    public ModeArguments(List<String> headersWhitelist) {
        this.headersWhitelist = headersWhitelist;
    }

    public ModeArguments(List<String> headersWhitelist, boolean stateful) {
        this.headersWhitelist = headersWhitelist;
        this.stateful = stateful;
    }

    public List<String> getHeadersWhitelist() {
        return headersWhitelist;
    }

    public void setHeadersWhitelist(List<String> headersWhitelist) {
        this.headersWhitelist = headersWhitelist;
    }


    public String getMatchingStrategy() {
        return matchingStrategy;
    }

    public void setMatchingStrategy(String matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }

    public boolean isStateful() {
        return stateful;
    }

    public void setStateful(boolean stateful) {
        this.stateful = stateful;
    }
}
