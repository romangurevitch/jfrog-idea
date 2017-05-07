package dependencies;

import xray.persistency.XrayGeneral;
import xray.persistency.XrayIssue;
import xray.persistency.XrayLicense;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by romang on 3/9/17.
 */
public class ScanTreeNode extends DefaultMutableTreeNode {

    private Set<XrayIssue> issues = new HashSet<>();
    private Set<XrayLicense> licenses = new HashSet<>();
    private XrayGeneral generalInfo;

    public ScanTreeNode(Object userObject) {
        super(userObject);
    }

    public void setIssues(Set<XrayIssue> issues) {
        this.issues = issues;
    }

    public void setLicenses(Set<XrayLicense> licenses) {
        this.licenses = licenses;
    }

    /**
     * @return current node's issues
     */
    public Set<XrayIssue> getIssues() {
        return issues;
    }

    /**
     * @return current node's licenses
     */
    public Set<XrayLicense> getLicenses() {
        return licenses;
    }

    /**
     * @return all issues of the current node and it's ancestors
     */
    public Set<XrayIssue> getAllIssues() {
        Set<XrayIssue> allIssues = new HashSet<>();
        addIssuesRecursive(allIssues);
        return allIssues;
    }

    private void addIssuesRecursive(Set<XrayIssue> issues) {
        if (!this.issues.isEmpty()) {
            issues.addAll(this.issues);
        }

        Enumeration c = children();
        while (c.hasMoreElements()) {
            ScanTreeNode node = (ScanTreeNode) c.nextElement();
            node.addIssuesRecursive(issues);
        }
    }

    /**
     * @return all licenses of the current node and it's ancestors
     */
    public Set<XrayLicense> getAllLicenses() {
        Set<XrayLicense> allLicenses = new HashSet<>();
        addLicensesRecursive(allLicenses);
        return allLicenses;
    }

    private void addLicensesRecursive(Set<XrayLicense> licenses) {
        if (!this.licenses.isEmpty()) {
            licenses.addAll(this.licenses);
        }

        Enumeration c = children();
        while (c.hasMoreElements()) {
            ScanTreeNode node = (ScanTreeNode) c.nextElement();
            node.addLicensesRecursive(licenses);
        }
    }

    public void setGeneralInfo(XrayGeneral generalInfo) {
        this.generalInfo = generalInfo;
    }

    public XrayGeneral getGeneralInfo() {
        return generalInfo;
    }
}
