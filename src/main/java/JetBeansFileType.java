import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.*;
import javax.swing.*;

public class JetBeansFileType extends XmlLikeFileType implements UIBasedFileType {
    public static final JetBeansFileType INSTANCE = new JetBeansFileType();
    private static final Icon ICON = IconLoader.getIcon("jar-gray.png");

    private JetBeansFileType() {
        super(JetBeansLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Bean";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "JetBeans component";
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
