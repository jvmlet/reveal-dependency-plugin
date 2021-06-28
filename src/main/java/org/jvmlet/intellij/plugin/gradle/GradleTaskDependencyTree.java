package org.jvmlet.intellij.plugin.gradle;

import com.intellij.execution.actions.BaseRunConfigurationAction;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
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

public class GradleTaskDependencyTree extends BaseRunConfigurationAction {


    public GradleTaskDependencyTree() {
        this(null,null);
    }

    public GradleTaskDependencyTree(@Nullable @NlsActions.ActionText String text,@Nullable @NlsActions.ActionText String description) {
        super(()->text, ()->description ,null );
    }


    @Override
    protected void perform(ConfigurationContext context) {



        try {
            File file = getInitScript();

            GradleExecuteTaskAction.runGradle(context.getProject(),
                    null, context.getProject().getBasePath(),
                    String.format("--init-script \"%s\" build taskTree --no-repeat", file.getAbsolutePath()));

        }catch (Exception e){

        }
    }


    @Override
    protected void updatePresentation(Presentation presentation, @NotNull String actionText, ConfigurationContext context) {

    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setIcon(AllIcons.Actions.ShowAsTree);
    }

    private static File getInitScript() throws IOException {
        Path init = Files.createTempFile("init", ".gradle");


        try (InputStream is = GradleTaskDependencyTree.class.getResourceAsStream("/init.gradle")) {
            Files.copy(is,init, StandardCopyOption.REPLACE_EXISTING);
        }
        return init.toFile();
    }


}
