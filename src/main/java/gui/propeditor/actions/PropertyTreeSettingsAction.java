package gui.propeditor.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareToggleAction;
import gui.propeditor.tree.PropertyTree;
import gui.propeditor.tree.PropertyTreeSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyTreeSettingsAction extends DefaultActionGroup implements DumbAware {
    private PropertyTreeSettings settings;

    public PropertyTreeSettingsAction(PropertyTree tree) {
        this.settings = tree.settings;
        this.setPopup(true);
        this.addSeparator("Group By");
        this.add(this.makeToggleAction(
                null, "Class",
                () -> this.settings.groupMode == PropertyTreeSettings.GROUP_BY_CLASS,
                (state) -> this.settings.setGroupMode(state ? PropertyTreeSettings.GROUP_BY_CLASS : PropertyTreeSettings.GROUP_BY_NONE)
        ));
        this.add(this.makeToggleAction(
                null, "None",
                () -> this.settings.groupMode == PropertyTreeSettings.GROUP_BY_NONE,
                (state) -> this.settings.setGroupMode(!state ? PropertyTreeSettings.GROUP_BY_CLASS : PropertyTreeSettings.GROUP_BY_NONE)
        ));

        this.addSeparator("Sort By");
        this.add(this.makeToggleAction(
                null, "Name",
                () -> this.settings.sortMode == PropertyTreeSettings.SORT_BY_NAME,
                (state) -> this.settings.setSortMode(state ? PropertyTreeSettings.SORT_BY_NAME : PropertyTreeSettings.SORT_BY_KIND)
        ));
        this.add(this.makeToggleAction(
                null, "Kind",
                () -> this.settings.sortMode == PropertyTreeSettings.SORT_BY_KIND,
                (state) -> this.settings.setSortMode(!state ? PropertyTreeSettings.SORT_BY_NAME : PropertyTreeSettings.SORT_BY_KIND)
        ));

        this.addSeparator("Filter");
        this.add(this.makeToggleAction(
                null, "Readonly",
                () -> (this.settings.filter & PropertyTreeSettings.SHOW_READONLY) != 0,
                (state) -> this.settings.toggleFilter(PropertyTreeSettings.SHOW_READONLY, state)
        ));
        this.add(this.makeToggleAction(
                null, "Bound",
                () -> (this.settings.filter & PropertyTreeSettings.SHOW_BOUND) != 0,
                (state) -> this.settings.toggleFilter(PropertyTreeSettings.SHOW_BOUND, state)
        ));
        this.add(this.makeToggleAction(
                null, "Hidden",
                () -> (this.settings.filter & PropertyTreeSettings.SHOW_HIDDEN) != 0,
                (state) -> this.settings.toggleFilter(PropertyTreeSettings.SHOW_HIDDEN, state)
        ));
        this.add(this.makeToggleAction(
                null, "Expert",
                () -> (this.settings.filter & PropertyTreeSettings.SHOW_EXPERT) != 0,
                (state) -> this.settings.toggleFilter(PropertyTreeSettings.SHOW_EXPERT, state)
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
