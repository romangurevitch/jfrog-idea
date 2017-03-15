package ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.vcs.log.ui.VcsLogActionPlaces;
import com.intellij.vcs.log.ui.actions.IntelliSortChooserPopupAction;
import com.intellij.vcs.log.util.BekUtil;
import messages.XrayIssuesListener;
import messages.XrayLicensesListener;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.navigator.MavenProjectsStructure;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romang on 3/7/17.
 */
public class XrayView implements Disposable {

    private final Project project;

    private ContentManager contentManager;
    private XrayPanel libraryPanel;
    private XrayPanel detailsPanel;


    private final List<XrayPanel> myPanels = new ArrayList<>();
    private Content myChangeListXrayContent;
    private SimpleTree issuesTree;
    private SimpleTree licensesTree;
    private List<MavenProjectsStructure.DependencyNode> myChildren = new ArrayList<>();

    XrayView(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void dispose() {

    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        // Create panels
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content libraryContent = contentFactory.createContent(null, "Library list", false);
        libraryContent.setCloseable(false);
        libraryPanel = new XrayPanel(project, libraryContent);
        Disposer.register(this, libraryPanel);

        Content detailsContent = contentFactory.createContent(null, "Details", false);
        detailsContent.setCloseable(false);
        detailsPanel = new XrayPanel(project, detailsContent);
        Disposer.register(this, detailsPanel);

        Content issuesContent = contentFactory.createContent(null, "Issues", false);
        issuesContent.setCloseable(false);
        Content licensesContent = contentFactory.createContent(null, "Licenses", false);
        licensesContent.setCloseable(false);

//        JTabbedPane jTabbedPane = new JTabbedPane();
//        jTabbedPane.addTab("Issues", new XrayPanel(project, issuesContent));
//        jTabbedPane.addTab("Licenses", new XrayPanel(project, licensesContent));

        OnePixelSplitter issuesOnePixelSplitter = new OnePixelSplitter(false, "jfrog.xray.scan.splitter", 0.7f);

        JComponent toolbars = new JPanel(new BorderLayout());
        toolbars.add(createActionsToolbar(), BorderLayout.NORTH);
        JComponent issuesToolbarsAndTable = new JPanel(new BorderLayout());
        issuesToolbarsAndTable.add(toolbars, BorderLayout.NORTH);

        issuesTree = getMavenTree();
        issuesTree.setRootVisible(false);

        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayIssuesListener.XRAY_ISSUES_LISTENER_TOPIC, model
                -> ApplicationManager.getApplication().invokeLater(() -> {
            issuesTree.setModel(model);
            issuesTree.updateUI();
        }));

        JScrollPane issuesPane = ScrollPaneFactory.createScrollPane(issuesTree, SideBorder.TOP);
        issuesToolbarsAndTable.add(issuesPane, BorderLayout.CENTER);

        licensesTree = getMavenTree();
        licensesTree.setRootVisible(false);

        busConnection.subscribe(XrayLicensesListener.XRAY_LICENSES_LISTENER_TOPIC, model
                -> ApplicationManager.getApplication().invokeLater(() -> {
            licensesTree.setModel(model);
            licensesTree.updateUI();
        }));

        issuesOnePixelSplitter.setFirstComponent(issuesToolbarsAndTable);
        issuesOnePixelSplitter.setSecondComponent(new XrayPanel(project, issuesContent));

//        Content toolWindowContent = contentFactory.createContent(onePixelSplitter, "Scan results", false);

        Content issues = contentFactory.createContent(issuesOnePixelSplitter, "Issues", false);
        Content licenses = contentFactory.createContent(issuesOnePixelSplitter, "Licenses", false);

        contentManager = toolWindow.getContentManager();
        contentManager.addContent(getIssuesContent());
        contentManager.addContent(getLicensesContent());
    }


    private Content getIssuesContent() {
        issuesTree = getMavenTree();
        issuesTree.setRootVisible(false);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayIssuesListener.XRAY_ISSUES_LISTENER_TOPIC, model
                -> ApplicationManager.getApplication().invokeLater(() -> {
            issuesTree.setModel(model);
            issuesTree.updateUI();
        }));

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
        issuesOnePixelSplitter.setSecondComponent(new XrayPanel(project, issuesContent));

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
