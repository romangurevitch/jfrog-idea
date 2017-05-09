package com.jfrog.xray.client.impl.test;

import com.jfrog.xray.client.services.summary.Error;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by romang on 2/27/17.
 */
public class SummaryTests extends XrayTestsBase {

    @Test
    public void testArtifactSummaryNonExistingSha() throws IOException {
        List<String> checksums = new ArrayList<>();
        checksums.add("nonExistingSha");
        SummaryResponse summary = xray.summary().artifactSummary(checksums, null);
        for (Error err : summary.getErrors()) {
            assertEquals(err.getError(), "Artifact doesn't exist or not indexed/cached in Xray");
        }
    }

    @Test
    public void testArtifactSummaryNonExistingPath() throws IOException {
        List<String> paths = new ArrayList<>();
        paths.add("non/existing/path");
        SummaryResponse summary = xray.summary().artifactSummary(null, paths);
        for (Error err : summary.getErrors()) {
            assertEquals(err.getError(), "Artifact doesn't exist or not indexed/cached in Xray");
        }
    }

    @Test
    public void testArtifactSummary() throws IOException {
        SummaryResponse summary = xray.summary().artifactSummary(null, null);
        assertNull(summary.getArtifacts());
        assertNull(summary.getErrors());
    }
}
