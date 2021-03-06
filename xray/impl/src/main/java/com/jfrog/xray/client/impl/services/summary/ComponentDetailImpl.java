package com.jfrog.xray.client.impl.services.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jfrog.xray.client.services.summary.ComponentDetail;

/**
 * Created by romang on 6/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentDetailImpl implements ComponentDetail {

    @JsonProperty("component_id")
    private String componentId;
    private String sha1;

    public ComponentDetailImpl(String componentId, String sha1) {
        this.componentId = componentId;
        this.sha1 = sha1;
    }

    @JsonProperty("component_id")
    public String getComponentId() {
        return componentId;
    }

    @Override
    public String getSha1() {
        return sha1;
    }
}
