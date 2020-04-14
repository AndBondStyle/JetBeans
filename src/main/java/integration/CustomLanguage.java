package integration;

import com.intellij.lang.Language;

public class CustomLanguage extends Language {
    public static final CustomLanguage INSTANCE = new CustomLanguage();

    private CustomLanguage() {
        super("JetBeans");
    }
}
