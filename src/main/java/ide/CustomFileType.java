package ide;

import com.intellij.openapi.fileTypes.UIBasedFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CustomFileType implements UIBasedFileType {
    public static final CustomFileType INSTANCE = new CustomFileType();
    public static final Icon ICON = IconLoader.getIcon("../jar-gray.png");

    @NotNull
    @Override
    public String getName() {
        return "JetBeans Component";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "JetBeans Component";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "bb";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ICON;
    }

    @Nullable
    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        return null;
    }

    @Override
    public boolean isBinary() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
