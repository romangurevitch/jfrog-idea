package xray.maven;

import com.intellij.openapi.project.Project;
import dependencies.ScanTreeNode;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.build.api.util.FileChecksumCalculator;
import xray.ScanManager;
import xray.persistency.XrayArtifact;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by romang on 3/2/17.
 */
public class MavenScanManager extends ScanManager {

    public MavenScanManager(Project project) {
        super(project);

    }

    @Override
    protected TreeModel scanAndUpdateResults(TreeModel currentScanResults) {
        TreeModel issuesTree = new DefaultTreeModel(new ScanTreeNode("All components"), false);
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            ScanTreeNode rootNode = (ScanTreeNode) issuesTree.getRoot();
            for (MavenArtifactNode dependencyTree : mavenProject.getDependencyTree()) {
                updateChildrenNodes(rootNode, dependencyTree);
            }
        }
        return issuesTree;
    }

    private void updateChildrenNodes(ScanTreeNode parentNode, MavenArtifactNode mavenArtifactNode) {
        ScanTreeNode currentNode = scanAndCreateNode(mavenArtifactNode.getArtifact());
        for (MavenArtifactNode childrenArifactNode : mavenArtifactNode.getDependencies()) {
            updateChildrenNodes(currentNode, childrenArifactNode);
        }
        parentNode.add(currentNode);
    }

    private ScanTreeNode scanAndCreateNode(MavenArtifact artifact) {
        ScanTreeNode scanTreeNode = new ScanTreeNode(artifact);

        XrayArtifact scanArtifact = scanArtifact(getArtifactSha1(artifact));
        if (scanArtifact != null) {
            scanTreeNode.setIssues(scanArtifact.issues);
            scanTreeNode.setLicenses(scanArtifact.licenses);
            scanTreeNode.setGeneralInfo(scanArtifact.general);
        }
        return scanTreeNode;
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