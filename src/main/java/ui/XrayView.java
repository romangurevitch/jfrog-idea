package ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.vcs.log.ui.VcsLogActionPlaces;
import com.intellij.vcs.log.ui.actions.IntelliSortChooserPopupAction;
import com.intellij.vcs.log.util.BekUtil;
import com.jfrog.xray.client.services.summary.Issue;
import dependencies.IssuesTreeNode;
import messages.XrayIssuesTreeListener;
import messages.XrayLicensesListener;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by romang on 3/7/17.
 */
public class XrayView implements Disposable {

    private final Project project;

    private ContentManager contentManager;
    private SimpleTree issuesTree;
    private SimpleTree licensesTree;

    private JBList issuesList;

    XrayView(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void dispose() {

    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        // Create panels
        contentManager = toolWindow.getContentManager();

        //Adding issues and licenses tabs
        contentManager.addContent(getIssuesContent());
        contentManager.addContent(getLicensesContent());
    }

    private Content getIssuesContent() {
        issuesTree = getMavenTree();
        issuesList = new JBList<>();
        issuesList.setCellRenderer(new IssueCellRenderer());
        issuesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        issuesList.setExpandableItemsEnabled(true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayIssuesTreeListener.XRAY_ISSUES_TREE_LISTENER_TOPIC, model
                -> ApplicationManager.getApplication().invokeLater(() -> {
            issuesTree.setModel(model);
            issuesTree.updateUI();
        }));

        issuesTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                try {
                    Set<Issue> issuesSet = new HashSet<>();
                    for (TreePath path : issuesTree.getSelectionPaths()) {
                        IssuesTreeNode issueNode = (IssuesTreeNode) path.getLastPathComponent();
                        issuesSet.addAll(issueNode.getAllIssues());
                    }

                    DefaultListModel model = new DefaultListModel();
                    for (Issue issue : issuesSet) {
                        model.addElement(issue);
                    }

                    issuesList.setModel(model);
                    issuesList.updateUI();
                } catch (NullPointerException e1) {
                    // Do nothing
                }
            }
        });

        JScrollPane issuesPane = ScrollPaneFactory.createScrollPane(issuesTree, SideBorder.TOP);

        JComponent toolbars = new JPanel(new BorderLayout());
        toolbars.add(createActionsToolbar(), BorderLayout.NORTH);
        JComponent issuesToolbarsAndTable = new JPanel(new BorderLayout());

        issuesToolbarsAndTable.add(toolbars, BorderLayout.NORTH);
        issuesToolbarsAndTable.add(issuesPane, BorderLayout.CENTER);

        Content issuesContent = contentFactory.createContent(null, "Issues", false);
        issuesContent.setCloseable(false);
        OnePixelSplitter issuesOnePixelSplitter = new OnePixelSplitter(false, "jfrog.xray.scan.splitter", 0.7f);
        issuesOnePixelSplitter.setFirstComponent(issuesToolbarsAndTable);
        JScrollPane listScroller = ScrollPaneFactory.createScrollPane(issuesList, SideBorder.RIGHT);
        listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        issuesOnePixelSplitter.setSecondComponent(listScroller);

        return contentFactory.createContent(issuesOnePixelSplitter, "Issues", false);
    }

    private Content getLicensesContent() {
        licensesTree = getMavenTree();
        licensesTree.setRootVisible(false);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayLicensesListener.XRAY_LICENSES_LISTENER_TOPIC, model
                -> ApplicationManager.getApplication().invokeLater(() -> {
            licensesTree.setModel(model);
            licensesTree.updateUI();
        }));

        JScrollPane licensesPane = ScrollPaneFactory.createScrollPane(licensesTree, SideBorder.TOP);

        JComponent toolbars = new JPanel(new BorderLayout());
        toolbars.add(createActionsToolbar(), BorderLayout.NORTH);
        JComponent licensesToolbarsAndTable = new JPanel(new BorderLayout());

        licensesToolbarsAndTable.add(toolbars, BorderLayout.NORTH);
        licensesToolbarsAndTable.add(licensesPane, BorderLayout.CENTER);

        Content licensesContent = contentFactory.createContent(null, "Licenses", false);
        licensesContent.setCloseable(false);
        OnePixelSplitter licensesOnePixelSplitter = new OnePixelSplitter(false, "jfrog.xray.scan.splitter", 0.7f);
        licensesOnePixelSplitter.setFirstComponent(licensesToolbarsAndTable);
        licensesOnePixelSplitter.setSecondComponent(new XrayPanel(project, licensesContent));

        return contentFactory.createContent(licensesOnePixelSplitter, "Licenses", false);
    }

    private JComponent createActionsToolbar() {
        DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(ActionManager.getInstance().getAction(VcsLogActionPlaces.TOOLBAR_ACTION_GROUP));

        DefaultActionGroup mainGroup = new DefaultActionGroup();
        mainGroup.add(new Separator());
        mainGroup.addSeparator();
        mainGroup.addAction(ActionManager.getInstance().getAction("Xray.Refresh"));
        if (BekUtil.isBekEnabled()) {
            if (BekUtil.isLinearBekEnabled()) {
                mainGroup.add(new IntelliSortChooserPopupAction());
                // can not register both of the actions in xml file, choosing to register an action for the "outer world"
                // I can of course if linear bek is enabled replace the action on start but why bother
            }
        }
        mainGroup.add(toolbarGroup);
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.CHANGES_VIEW_TOOLBAR, mainGroup, true);

        JPanel panel = new JPanel(new MigLayout("ins 0, fill", "[left]0[left, fill]push[right]", "center"));
        panel.add(toolbar.getComponent());
        return panel;
    }

    private SimpleTree getMavenTree() {
        return new SimpleTree() {
            private final JLabel myLabel = new JLabel("blabla");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (MavenProjectsManager.getInstance(project).hasProjects()) return;

                myLabel.setFont(getFont());
                myLabel.setBackground(getBackground());
                myLabel.setForeground(getForeground());
                Rectangle bounds = getBounds();
                Dimension size = myLabel.getPreferredSize();
                myLabel.setBounds(0, 0, size.width, size.height);

                int x = (bounds.width - size.width) / 2;
                Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
                try {
                    myLabel.paint(g2);
                } finally {
                    g2.dispose();
                }
            }
        };
    }
}
