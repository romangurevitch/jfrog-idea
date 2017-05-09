package org.jfrog.idea.xray.maven;

import com.intellij.openapi.project.Project;
import org.jfrog.idea.xray.ScanTreeNode;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jfrog.idea.xray.ScanManager;
import org.jfrog.idea.xray.persistency.XrayArtifact;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.HashSet;
import java.util.Set;

import static org.jfrog.idea.xray.utils.FileUtils.calculateSha256;

/**
 * Created by romang on 3/2/17.
 */
public class MavenScanManager extends ScanManager {

    public MavenScanManager(Project project) {
        super(project);
        MavenProjectsManager.getInstance(project).addManagerListener(new MavenProjectsListene());
    }

    @Override
    protected Set<String> collectArtifactsToScan() {
        Set<String> checksums = new HashSet<>();
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            for (MavenArtifactNode mavenArtifactNode : mavenProject.getDependencyTree()) {
                checksums.add(getArtifactChecksum(mavenArtifactNode.getArtifact()));
                for (MavenArtifactNode artifactNode : mavenArtifactNode.getDependencies()) {
                    checksums.add(getArtifactChecksum(artifactNode.getArtifact()));
                }
            }
        }
        return checksums;
    }

    @Override
    protected TreeModel updateResultsTree(TreeModel currentScanResults) {
        ScanTreeNode rootNode = new ScanTreeNode("All components");
        TreeModel issuesTree = new DefaultTreeModel(rootNode, false);
        for (MavenProject mavenProject : MavenProjectsManager.getInstance(project).getProjects()) {
            for (MavenArtifactNode dependencyTree : mavenProject.getDependencyTree()) {
                updateChildrenNodes(rootNode, dependencyTree);
            }
        }
        return issuesTree;
    }

    private void updateChildrenNodes(ScanTreeNode parentNode, MavenArtifactNode mavenArtifactNode) {
        ScanTreeNode currentNode = createArtifactNode(mavenArtifactNode.getArtifact());
        for (MavenArtifactNode childrenArifactNode : mavenArtifactNode.getDependencies()) {
            updateChildrenNodes(currentNode, childrenArifactNode);
        }
        parentNode.add(currentNode);
    }

    private ScanTreeNode createArtifactNode(MavenArtifact artifact) {
        ScanTreeNode scanTreeNode = new ScanTreeNode(artifact);
        XrayArtifact scanArtifact = getArtifactSummary(getArtifactChecksum(artifact));
        if (scanArtifact != null) {
            scanTreeNode.setIssues(scanArtifact.issues);
            scanTreeNode.setLicenses(scanArtifact.licenses);
            scanTreeNode.setGeneralInfo(scanArtifact.general);
        }
        return scanTreeNode;
    }

    private String getArtifactChecksum(MavenArtifact artifact) {
        String sha256String = "";
        try {
            sha256String = calculateSha256(artifact.getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha256String;
    }

    /**
     * Maven project listener for scanning artifacts on dependencies changes.
     */
    private class MavenProjectsListene implements MavenProjectsManager.Listener {

        @Override
        public void activated() {
        }

        @Override
        public void projectsScheduled() {
        }

        @Override
        public void importAndResolveScheduled() {
            asyncScanAndUpdateResults(true);
        }
    }

}