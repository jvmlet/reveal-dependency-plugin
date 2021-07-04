package org.jvmlet.intellij.plugin.gradle;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys;
import com.intellij.openapi.externalSystem.model.project.ModuleData;
import com.intellij.openapi.externalSystem.view.ExternalSystemNode;
import com.intellij.openapi.externalSystem.view.ModuleNode;
import com.intellij.openapi.externalSystem.view.ProjectNode;
import com.intellij.openapi.externalSystem.view.TaskNode;
import com.intellij.openapi.util.NlsActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradleTaskDependencyTree extends AnAction {


    public GradleTaskDependencyTree() {
        this(null,null);
    }

    public GradleTaskDependencyTree(@Nullable @NlsActions.ActionText String text,@Nullable @NlsActions.ActionText String description) {
        super(()->text, ()->description ,null );
    }






    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setIcon(AllIcons.Actions.ShowAsTree);
        event.getPresentation().setEnabledAndVisible(
                Optional.ofNullable(event.getData(ExternalSystemDataKeys.SELECTED_NODES))
                .map(l->l.stream().anyMatch(n->n instanceof TaskNode))
                .orElse(false)

        );
        
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final List<ExternalSystemNode> selectedNodes = e.getData(ExternalSystemDataKeys.SELECTED_NODES);


        List<String> tasks = new ArrayList<>();
        selectedNodes.forEach(n->{
            if(n instanceof TaskNode){
                TaskNode  taskNode = (TaskNode) n;

                final ModuleNode moduleDataNode = taskNode.findParent(ModuleNode.class);
                if (moduleDataNode != null) {
                    ModuleData moduleData = moduleDataNode.getData();
                    Optional.ofNullable(moduleData).map(ModuleData::getId)
                            .ifPresent(id->tasks.add(String.format("%s:%s",id,taskNode.getName())));

                }
                else {
                    ProjectNode projectNode = taskNode.findParent(ProjectNode.class);
                    if(null!= projectNode){
                        tasks.add(taskNode.getName());
                    }

                }


            }
        });

        if (tasks.isEmpty()){
            return;
        }

        try {

            tasks.add("taskTree");
            tasks.add("--init-script");
            tasks.add(String.format("\"%s\"", getInitScript().getAbsolutePath()));
            final String command = String.join(" ",tasks);

            GradleExecuteTaskAction.runGradle(e.getProject(),null,e.getProject().getBasePath(),command);

        }catch (Exception exception){
            com.intellij.openapi.diagnostic.Logger.getInstance(getClass()).error(exception);
        }
    }

    private static File getInitScript() throws IOException {


        Path init =PathManager.getConfigDir().resolve(".taskTree.gradle");
        if(!init.toFile().exists()) {
            try (InputStream is = GradleTaskDependencyTree.class.getResourceAsStream("/init.gradle")) {
                Files.copy(is, init, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return init.toFile();
    }


}
