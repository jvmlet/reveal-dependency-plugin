package org.jvmlet.intellij.plugin.gradle.reveal;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys;
import com.intellij.ui.treeStructure.SimpleTree;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GradleTreeGetter implements DataSink {
    private SimpleTree simpleTree;

    public SimpleTree getSimpleTree() {
        return simpleTree;
    }

    @Override
    public <T> void set(@NotNull DataKey<T> dataKey, @Nullable T t) {
        if (dataKey.equals(ExternalSystemDataKeys.PROJECTS_TREE)){
            simpleTree = (SimpleTree) t;
        }

    }

    @Override
    public <T> void setNull(@NotNull DataKey<T> dataKey) {

    }

    @Override
    public <T> void lazy(@NotNull DataKey<T> dataKey, @NotNull Function0<? extends T> function0) {

    }

    @Override
    public void uiDataSnapshot(@NotNull UiDataProvider uiDataProvider) {

    }

    @Override
    public void dataSnapshot(@NotNull DataSnapshotProvider dataSnapshotProvider) {

    }

    @Override
    public void uiDataSnapshot(@NotNull DataProvider dataProvider) {

    }
}
