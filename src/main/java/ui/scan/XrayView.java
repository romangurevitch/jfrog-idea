package ui.scan;

import actions.FilterAction;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import dependencies.ScanTreeNode;
import messages.XrayScanComponentsChange;
import messages.XrayScanIssuesChange;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import xray.ScanManagerFactory;
import xray.persistency.XrayIssue;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ui.utils.ComponentUtils.createDisabledTextLabel;


/**
 * Created by romang on 3/7/17.
 */
public class XrayView implements Disposable {

    private final Project project;

    private ContentManager contentManager;
    private Tree componentsTree;
    private JBTable issuesTable;
    private JBPanel detailsPanel;
    private JScrollPane detailsScroll;

    XrayView(@NotNull Project project) {
        this.project = project;
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        detailsPanel = new JBPanel(new BorderLayout());
        detailsPanel.add(createDisabledTextLabel("Select component or issue for more details"), BorderLayout.CENTER);

        contentManager = toolWindow.getContentManager();
        contentManager.addContent(createComponents());
        registerListeners();
    }

    private void registerListeners() {
        // Component tree change listener
        MessageBusConnection busConnection = project.getMessageBus().connect(project);
        busConnection.subscribe(XrayScanComponentsChange.XRAY_SCAN_COMPONENTS_CHANGE_TOPIC, ()
                -> ApplicationManager.getApplication().invokeLater(() -> {
            TreeModel model = ScanManagerFactory.getScanManager(project).getFilteredScanTreeModel();
            componentsTree.setModel(model);
            componentsTree.updateUI();
        }));

        // Component selection listener
        componentsTree.addTreeSelectionListener(e -> {
            updateIssuesTable();
            if (e == null || e.getNewLeadSelectionPath() == null) {
                return;
            }
            DetailsViewFactory.createDetailsView(detailsPanel, (ScanTreeNode) e.getNewLeadSelectionPath().getLastPathComponent());
            SwingUtilities.invokeLater(() -> detailsScroll.getViewport().setViewPosition(new Point(0, 0)));
        });

        // Issue selection listener
        issuesTable.getSelectionModel().addListSelectionListener(e -> {
            if (issuesTable.getSelectedRowCount() != 0) {
                XrayIssue issue = (XrayIssue) issuesTable.getValueAt(issuesTable.getSelectedRow(), issuesTable.getSelectedColumn());
                DetailsViewFactory.createDetailsView(detailsPanel, issue);
                SwingUtilities.invokeLater(() -> detailsScroll.getViewport().setViewPosition(new Point(0, 0)));
            }
        });

        // Issues update listener
        busConnection.subscribe(XrayScanIssuesChange.XRAY_SCAN_ISSUES_CHANGE_TOPIC, ()
                -> ApplicationManager.getApplication().invokeLater(this::updateIssuesTable));

    }

    private Content createComponents() {
        OnePixelSplitter centralVerticalSplit = new OnePixelSplitter(false, 0.3f);
        JBSplitter rightHorizontalSplit = new JBSplitter(true, 0.7f);

        rightHorizontalSplit.setFirstComponent(createIssuesPanel());
        detailsScroll = ScrollPaneFactory.createScrollPane(detailsPanel, SideBorder.TOP);
        detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightHorizontalSplit.setSecondComponent(detailsScroll);

        centralVerticalSplit.setFirstComponent(createComponentsPanel());
        centralVerticalSplit.setSecondComponent(rightHorizontalSplit);

        JComponent xrayPanel = new JBPanel(new BorderLayout());
        xrayPanel.add(createActionsToolbar(), BorderLayout.NORTH);
        xrayPanel.add(centralVerticalSplit, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        return contentFactory.createContent(xrayPanel, "Issues", false);
    }

    private JComponent createComponentsPanel() {
        componentsTree = getMavenTree();
        JScrollPane componentsLeftPanel = ScrollPaneFactory.createScrollPane(componentsTree, SideBorder.TOP);
        return componentsLeftPanel;
    }

    private JComponent createIssuesPanel() {
        issuesTable = new JBTable();
        issuesTable.setDefaultRenderer(Object.class, new IssueTableCellRenderer());
        issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        issuesTable.setTableHeader(null);
        issuesTable.setShowGrid(false);

        JScrollPane tableScroll = ScrollPaneFactory.createScrollPane(issuesTable, SideBorder.TOP);
        tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return tableScroll;
    }

    private void updateIssuesTable() {
        ScanTreeNode selectedNode = (ScanTreeNode) componentsTree.getModel().getRoot();
        if (componentsTree.getSelectionPaths() != null && componentsTree.getSelectionPaths().length == 1) {
            selectedNode = (ScanTreeNode) componentsTree.getSelectionPaths()[0].getLastPathComponent();
        }

        TableModel model = ScanManagerFactory.getScanManager(project).getFilteredScanIssues(selectedNode);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        issuesTable.setRowSorter(sorter);
        issuesTable.setModel(model);
        issuesTable.updateUI();

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private JComponent createActionsToolbar() {
        DefaultActionGroup mainGroup = new DefaultActionGroup();

        mainGroup.addAction(ActionManager.getInstance().getAction("Xray.Refresh"));
        mainGroup.addSeparator();
        mainGroup.add(new FilterAction(new IssueFilterMenu()));
        mainGroup.add(new FilterAction(new LicenseFilterMenu(project)));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.CHANGES_VIEW_TOOLBAR, mainGroup, true);
        JPanel panel = new JPanel(new MigLayout("ins 0, fill", "[left]0[left, fill]push[right]", "center"));
        panel.add(toolbar.getComponent());
        return panel;
    }

    private Tree getMavenTree() {
        return new Tree((javax.swing.tree.TreeModel) null) {
            private final JLabel myLabel = new JLabel("meetsCriteria");

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

    @Override
    public void dispose() {

    }
}
