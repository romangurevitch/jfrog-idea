package dependencies;

import com.jfrog.xray.client.services.summary.License;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by romang on 3/9/17.
 */
public class LicensesTreeNode extends DefaultMutableTreeNode {

    private List<License> license;

    public LicensesTreeNode(Object userObject) {
        super(userObject);
    }

    public void setLicenses(List<License> license) {
        this.license = license;
    }

    /**
     * @return current node's licenses
     */
    public List<License> getLicenses() {
        return license;
    }

    /**
     * @return all licenses of the current node and it's ancestors
     */
    public List<License> getAllLicenses() {
        List<License> licenses = getLicenses();
        Enumeration c = children();
        while (c.hasMoreElements()) {
            LicensesTreeNode node = (LicensesTreeNode) c.nextElement();
            licenses.addAll(node.getAllLicenses());
        }
        return licenses;
    }
}
