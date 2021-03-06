package core.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareToggleAction;
import gui.inspector.TreeSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ViewSettingsAction extends DefaultActionGroup implements DumbAware {
    private TreeSettings settings;

    public ViewSettingsAction(TreeSettings settings) {
        this.settings = settings;
        this.getTemplatePresentation().setIcon(AllIcons.Actions.Show);
        this.getTemplatePresentation().setText("Display Options");
        this.setPopup(true);

        this.addSeparator("Group By");
        this.add(this.makeToggleAction(
                null, "Class",
                () -> this.settings.groupMode == TreeSettings.GROUP_BY_CLASS,
                (state) -> { if (state) this.settings.setGroupMode(TreeSettings.GROUP_BY_CLASS); }
        ));
        this.add(this.makeToggleAction(
                null, "None",
                () -> this.settings.groupMode == TreeSettings.GROUP_BY_NONE,
                (state) -> { if (state) this.settings.setGroupMode(TreeSettings.GROUP_BY_NONE); }
        ));

        this.addSeparator("Sort By");
        this.add(this.makeToggleAction(
                null, "Name",
                () -> this.settings.sortMode == TreeSettings.SORT_BY_NAME,
                (state) -> { if (state) this.settings.setSortMode(TreeSettings.SORT_BY_NAME); }
        ));
        this.add(this.makeToggleAction(
                null, "Kind",
                () -> this.settings.sortMode == TreeSettings.SORT_BY_KIND,
                (state) -> { if (state) this.settings.setSortMode(TreeSettings.SORT_BY_KIND); }
        ));

        this.addSeparator("Filter");
        this.add(this.makeToggleAction(
                null, "Readonly",
                () -> (this.settings.filter & TreeSettings.SHOW_READONLY) != 0,
                (state) -> this.settings.toggleFilter(TreeSettings.SHOW_READONLY, state)
        ));
        this.add(this.makeToggleAction(
                null, "Non-bound",
                () -> (this.settings.filter & TreeSettings.SHOW_NON_BOUND) != 0,
                (state) -> this.settings.toggleFilter(TreeSettings.SHOW_NON_BOUND, state)
        ));
        this.add(this.makeToggleAction(
                null, "Hidden",
                () -> (this.settings.filter & TreeSettings.SHOW_HIDDEN) != 0,
                (state) -> this.settings.toggleFilter(TreeSettings.SHOW_HIDDEN, state)
        ));
        this.add(this.makeToggleAction(
                null, "Expert",
                () -> (this.settings.filter & TreeSettings.SHOW_EXPERT) != 0,
                (state) -> this.settings.toggleFilter(TreeSettings.SHOW_EXPERT, state)
        ));
    }

    private ToggleAction makeToggleAction(Icon icon, String text, Supplier<Boolean> check, Consumer<Boolean> update) {
        ToggleAction action = new DumbAwareToggleAction() {
            public boolean isSelected(@NotNull AnActionEvent e) { return check.get(); }
            public void setSelected(@NotNull AnActionEvent e, boolean state) { update.accept(state); }
        };
        if (icon != null) action.getTemplatePresentation().setIcon(icon);
        action.getTemplatePresentation().setText(text);
        return action;
    }
}
