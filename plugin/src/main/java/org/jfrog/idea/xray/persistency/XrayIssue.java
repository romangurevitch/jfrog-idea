package org.jfrog.idea.xray.persistency;

import com.jfrog.xray.client.services.summary.Issue;
import org.jetbrains.annotations.NotNull;

/**
 * Created by romang on 4/12/17.
 */

public class XrayIssue implements Comparable<XrayIssue> {

    public String created;
    public String description;
    public String issueType;
    public String provider;
    public String sevirity;
    public String summary;

    public XrayIssue() {
    }

    public XrayIssue(Issue issue) {
        created = issue.getCreated();
        description = issue.getDescription();
        issueType = issue.getIssueType();
        provider = issue.getProvider();
        sevirity = issue.getSeverity();
        summary = issue.getSummary();
    }

    public Severity getSeverity() {
        return Severity.valueOf(sevirity.toLowerCase());
    }

    @Override
    public int compareTo(@NotNull XrayIssue o) {
        return Integer.compare(getSeverity().getValue(), o.getSeverity().getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XrayIssue xrayIssue = (XrayIssue) o;

        if (!description.equals(xrayIssue.description)) return false;
        return summary.equals(xrayIssue.summary);
    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + summary.hashCode();
        return result;
    }
}
