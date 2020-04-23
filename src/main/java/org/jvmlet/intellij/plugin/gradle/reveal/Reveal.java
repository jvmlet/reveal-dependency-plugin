package org.jvmlet.intellij.plugin.gradle.reveal;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys;
import com.intellij.openapi.externalSystem.model.Key;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.view.ExternalProjectsView;
import com.intellij.openapi.externalSystem.view.ExternalProjectsViewImpl;
import com.intellij.openapi.externalSystem.view.ExternalSystemNode;
import com.intellij.openapi.externalSystem.view.ExternalSystemViewContributor;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Reveal extends ExternalSystemViewContributor {


    private static final Key<?>[] KEYS = new Key[]{GradleSourceSetData.KEY};



    @NotNull
    @Override
    public ProjectSystemId getSystemId() {
        return GradleConstants.SYSTEM_ID;
    }

    @NotNull
    @Override
    public List<Key<?>> getKeys() {
        return Arrays.asList(KEYS);
    }

    @NotNull
    @Override
    public List<ExternalSystemNode<?>> createNodes(ExternalProjectsView externalProjectsView, MultiMap<Key<?>, DataNode<?>> dataNodes) {

        if(externalProjectsView instanceof ExternalProjectsViewImpl){
            SimpleTree tree = (SimpleTree) ExternalProjectsViewImpl.class.cast(externalProjectsView)
                    .getData(ExternalSystemDataKeys.PROJECTS_TREE.getName());

            tree.addMouseListener(new PopupHandler() {
                @Override
                public void invokePopup(Component comp, int x, int y) {

                    AbstractProjectViewPane currentProjectViewPane = ProjectView.getInstance(externalProjectsView.getProject())
                            .getCurrentProjectViewPane();

                    Object root = currentProjectViewPane.getTree()
                            .getModel()
                            .getRoot();

                    if(DefaultMutableTreeNode.class.isInstance(root)){
                        List<ExternalSystemNode> selectedNodes = externalProjectsView.getStructure().getSelectedNodes(tree, ExternalSystemNode.class);
                        String mavenCoordinates  = selectedNodes.get(0).getName().replace("(*)","").trim(); // IDEA 2020.1 added " (*)" suffix for omitted dependency
                        DefaultMutableTreeNode found = TreeUtil.findNode(DefaultMutableTreeNode.class.cast(root), n -> {
                            if (n.getUserObject() instanceof NamedLibraryElementNode) {
                                return NamedLibraryElementNode.class.cast(n.getUserObject())
                                        .getValue()
                                        .getName()
                                        .contains(mavenCoordinates);
                            }
                            return false;
                        });
                        Optional.ofNullable(found)
                                .ifPresent(n->TreeUtil.selectInTree(n,true,currentProjectViewPane.getTree()));


                    }

                }
            });
        }
        return Collections.emptyList();
    }
}
