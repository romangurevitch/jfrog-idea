package dependencies.maven;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.services.summary.Artifact;
import com.jfrog.xray.client.services.summary.Issue;
import com.jfrog.xray.client.services.summary.License;
import com.jfrog.xray.client.services.summary.SummaryResponse;
import dependencies.IssuesTreeNode;
import dependencies.LicensesTreeNode;
import messages.XrayIssuesListener;
import messages.XrayLicensesListener;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.build.api.util.FileChecksumCalculator;
import xray.ClientUtils;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by romang on 3/2/17.
 */
public class MavenArtifactsManager {

    public static List<String> getMavenArtifactsChecksums(Project project) {
        List<String> artifactsToScan = new ArrayList<>();
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            for (MavenArtifactNode dependencyTree : mavenProject.getDependencyTree()) {
                try {
                    Map<String, String> sha1Map = FileChecksumCalculator.calculateChecksums(dependencyTree.getArtifact().getFile(), "Sha1");
                    for (String shaVal : sha1Map.values()) {
                        artifactsToScan.add(shaVal);
                    }
                } catch (NoSuchAlgorithmException e) {
                    // Do nothing
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }
        return artifactsToScan;
    }


    public static void asyncUpdate(Project project) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> updateMavenTreeModel(project));
    }

    private static void updateMavenTreeModel(Project project) {
        TreeModel issuesTree = new DefaultTreeModel(new IssuesTreeNode(null), false);
        TreeModel licensesTree = new DefaultTreeModel(new LicensesTreeNode(null), false);
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                MessageBus messageBus = project.getMessageBus();
                IssuesTreeNode issuesRootNode = (IssuesTreeNode) issuesTree.getRoot();
                LicensesTreeNode licensesRootNode = (LicensesTreeNode) licensesTree.getRoot();
                for (MavenArtifactNode dependencyTree : mavenProject.getDependencyTree()) {
                    updateChildrenNodes(issuesRootNode, licensesRootNode, dependencyTree);

                    messageBus.syncPublisher(XrayIssuesListener.XRAY_ISSUES_LISTENER_TOPIC).update(issuesTree);
                    messageBus.syncPublisher(XrayLicensesListener.XRAY_LICENSES_LISTENER_TOPIC).update(licensesTree);
                }
            });
        }
    }

    private static void updateChildrenNodes(IssuesTreeNode parentIssuesNode, LicensesTreeNode parentLicensesNode, MavenArtifactNode mavenArtifactNode) {
        LicensesTreeNode currentLicensesNode = new LicensesTreeNode(mavenArtifactNode.getArtifact());
        IssuesTreeNode currentIssuesNode = new IssuesTreeNode(mavenArtifactNode.getArtifact());
        scanAndCreateNode(currentIssuesNode, currentLicensesNode, mavenArtifactNode.getArtifact());

        for (MavenArtifactNode childrenArifactNode : mavenArtifactNode.getDependencies()) {
            updateChildrenNodes(currentIssuesNode, currentLicensesNode, childrenArifactNode);
        }

        addIfHasIssues(parentIssuesNode, currentIssuesNode);
        addIfHasLicenses(parentLicensesNode, currentLicensesNode);
    }

    private static void addIfHasIssues(IssuesTreeNode parent, IssuesTreeNode child) {
        if (!child.getAllIssues().isEmpty()) {
            parent.add(child);
        }
    }

    private static void addIfHasLicenses(LicensesTreeNode parent, LicensesTreeNode child) {
        if (!child.getAllLicenses().isEmpty()) {
            parent.add(child);
        }
    }


    private static void scanAndCreateNode(IssuesTreeNode issuesTreeNode, LicensesTreeNode licensesTreeNode, MavenArtifact artifact) {
        List<Issue> issues = new ArrayList<>();
        List<License> licenses = new ArrayList<>();

        Xray xray = ClientUtils.createClient();
        List<String> checksumList = new ArrayList<>();
        checksumList.add(getArtifactSha1(artifact));
        try {
            SummaryResponse summary = xray.summary().artifactSummary(checksumList, null);
            for (Artifact summaryArtifact : summary.getArtifacts()) {
                issues.addAll(summaryArtifact.getIssues());
                licenses.addAll(summaryArtifact.getLicenses());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        issuesTreeNode.setIssues(issues);
        licensesTreeNode.setLicenses(licenses);
    }

    private static String getArtifactSha1(MavenArtifact artifact) {
        try {
            Map<String, String> sha1Map = FileChecksumCalculator.calculateChecksums(artifact.getFile(), "Sha1");
            for (String shaVal : sha1Map.values()) {
                return shaVal;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}