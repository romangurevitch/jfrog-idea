package xray;

import com.jfrog.xray.client.services.summary.SummaryResponse;

/**
 * Created by romang on 3/2/17.
 */
public class ArtifactsSummary {

    private SummaryResponse summary;

    public ArtifactsSummary(SummaryResponse summary) {
        this.summary = summary;
    }

    public SummaryResponse getSummary() {
        return summary;
    }
}
