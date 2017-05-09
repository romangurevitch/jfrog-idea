package org.jfrog.idea.xray.persistency;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jfrog.xray.client.services.summary.Artifact;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romang on 4/10/17.
 */

@State(name = "XrayScanCache", storages = {@Storage(file = "XrayScanCache.xml")})
public final class ScanCache implements PersistentStateComponent<ScanCache> {

    public Map<String, XrayArtifact> artifacts = new HashMap<>();
    public Map<String, String> lastUpdated = new HashMap<>();

    public static ScanCache getInstance(Project project) {
        return ServiceManager.getService(project, ScanCache.class);
    }

    @Nullable
    @Override
    public ScanCache getState() {
        return this;
    }

    @Override
    public void loadState(ScanCache state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public LocalDateTime getLastUpdateTime(String checksum) {
        if (lastUpdated.get(checksum) == null) {
            return null;
        }
        return LocalDateTime.parse(lastUpdated.get(checksum));
    }

    public XrayArtifact getArtifact(String checksum) {
        return artifacts.get(checksum);
    }

    public void updateArtifact(String checksum, Artifact artifact) {
        artifacts.put(checksum, new XrayArtifact(artifact));
    }

    public void setLastUpdated(String checksum) {
        lastUpdated.put(checksum, LocalDateTime.now().toString());
    }
}
