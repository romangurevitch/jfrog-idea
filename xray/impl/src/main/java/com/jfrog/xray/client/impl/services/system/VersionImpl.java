package com.jfrog.xray.client.impl.services.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jfrog.xray.client.services.system.Version;

/**
 * Created by romang on 2/2/17.
 */
public class VersionImpl implements Version {
    @JsonProperty("xray_version")
    private String version;
    @JsonProperty("xray_revision")
    private String revision;

    public VersionImpl() {
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getRevision() {
        return revision;
    }
}
