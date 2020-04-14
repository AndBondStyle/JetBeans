package integration;

import com.intellij.ide.actions.CreateFileAction;
import org.jetbrains.annotations.Nullable;

public class CustomNewFileAction extends CreateFileAction {
    CustomNewFileAction() {
        super("JetBeans Component", "Create new JetBeans component", CustomFileType.ICON);
    }

    @Nullable
    @Override
    protected String getDefaultExtension() {
        return CustomFileType.INSTANCE.getDefaultExtension();
    }
}
