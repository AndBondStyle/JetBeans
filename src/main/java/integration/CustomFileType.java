package integration;

import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.openapi.fileTypes.UIBasedFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CustomFileType extends XmlLikeFileType implements UIBasedFileType {
    public static final CustomFileType INSTANCE = new CustomFileType();
    private static final Icon ICON = IconLoader.getIcon("jar-gray.png");

    private CustomFileType() {
        super(CustomLanguage.INSTANCE);
    }

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
}
