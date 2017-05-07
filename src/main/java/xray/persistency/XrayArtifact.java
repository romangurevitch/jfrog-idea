package xray.persistency;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.jfrog.xray.client.services.summary.Artifact;
import com.jfrog.xray.client.services.summary.Issue;
import com.jfrog.xray.client.services.summary.License;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by romang on 4/11/17.
 */
public class XrayArtifact implements Serializable {

    public XrayGeneral general = new XrayGeneral();
    public Set<XrayIssue> issues = new HashSet<>();
    public Set<XrayLicense> licenses = new HashSet<>();

    public XrayArtifact() {
    }

    public XrayArtifact(Artifact artifact) {
        this.general = new XrayGeneral(artifact.getGeneral());
        issues.addAll(Lists.transform(artifact.getIssues(), new Function<Issue, XrayIssue>() {
            @Nullable
            @Override
            public XrayIssue apply(@Nullable Issue issue) {
                return new XrayIssue(issue);
            }
        }));

        licenses.addAll(Lists.transform(artifact.getLicenses(), new Function<License, XrayLicense>() {
            @Nullable
            @Override
            public XrayLicense apply(@Nullable License license) {
                return new XrayLicense(license);
            }
        }));
    }
}


