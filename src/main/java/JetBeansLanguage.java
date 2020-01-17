import com.intellij.lang.Language;

public class JetBeansLanguage extends Language {
    public static final JetBeansLanguage INSTANCE = new JetBeansLanguage();

    private JetBeansLanguage() {
        super("JetBeans");
    }
}
