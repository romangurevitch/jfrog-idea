package dependencies;

import com.jfrog.xray.client.services.summary.Issue;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by romang on 3/9/17.
 */
public class IssuesTreeNode extends DefaultMutableTreeNode {

    private List<Issue> issues = new ArrayList<>();

    public IssuesTreeNode(Object userObject) {
        super(userObject);
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    /**
     * @return current node's issues
     */
    public List<Issue> getIssues() {
        return issues;
    }

    /**
     * @return all issues of the current node and it's ancestors
     */
    public List<Issue> getAllIssues() {
        List<Issue> issues = getIssues();
        Enumeration c = children();
        while (c.hasMoreElements()) {
            IssuesTreeNode node = (IssuesTreeNode) c.nextElement();
            issues.addAll(node.getAllIssues());
        }
        return issues;
    }

}
