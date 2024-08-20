package org.jvmlet.intellij.plugin.gradle.reveal;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.nodes.ExternalLibrariesNode;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RevealAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        var component = PlatformDataKeys.CONTEXT_COMPONENT.getData(anActionEvent.getDataContext());
        if (component instanceof SimpleTree tree) {
            AbstractProjectViewPane currentProjectViewPane = ProjectView.getInstance(anActionEvent.getProject())
                    .getCurrentProjectViewPane();
            JTree projectTree = currentProjectViewPane.getTree();
            Object root = projectTree
                    .getModel()
                    .getRoot();

            String mavenCoordinates = tree.getSelectedNode().getName().replace("(*)", "").trim(); // IDEA 2020.1 added " (*)" suffix for omitted dependency

            DefaultMutableTreeNode rootNode = DefaultMutableTreeNode.class.cast(root);

            Optional.ofNullable(TreeUtil.findNode(rootNode, n -> n.getUserObject() instanceof ExternalLibrariesNode))
                    .ifPresent(extLibNode -> {
                        // expand twice to populate expanded children
                        TreeUtil.promiseExpand(projectTree, TreeUtil.getPath(rootNode, extLibNode))
                                .onSuccess(path -> {

                                    TreeUtil.promiseExpand(projectTree, path)
                                            .onSuccess(p -> {

                                                DefaultMutableTreeNode found = TreeUtil.findNode(extLibNode, n -> {
                                                    if (n.getUserObject() instanceof NamedLibraryElementNode) {
                                                        return NamedLibraryElementNode.class.cast(n.getUserObject())
                                                                .getValue()
                                                                .getName()
                                                                .contains(mavenCoordinates);
                                                    }
                                                    return false;
                                                });
                                                Optional.ofNullable(found)
                                                        .ifPresent(n -> TreeUtil.selectInTree(n, true, projectTree));
                                            });

                                });
                    });


        }
    }
}
