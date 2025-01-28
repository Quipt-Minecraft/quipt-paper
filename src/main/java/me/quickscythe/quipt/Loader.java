package me.quickscythe.quipt;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import me.quickscythe.quipt.json.SimpleJsonObject;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.QuiptLoader;

import java.io.File;
import java.io.IOException;

public class Loader extends QuiptLoader {


    @Override
    public String name() {
        return "quipt-paper";
    }

    @Override
    public void registerDependencies(PluginClasspathBuilder classpathBuilder) {
    }

    @Override
    public SimpleJsonObject defaultDependencies(File dependenciesFile) throws IOException {
        if (!dependenciesFile.exists()) dependenciesFile.createNewFile();

        SimpleJsonObject dependencies = new SimpleJsonObject();
        SimpleJsonObject central = new SimpleJsonObject();
        central.put("type", "default");
        central.put("url", "https://repo1.maven.org/maven2/");
        SimpleJsonObject jgit = new SimpleJsonObject();
        jgit.put("group", "org.eclipse.jgit");
        jgit.put("artifact", "org.eclipse.jgit");
        jgit.put("version", "6.6.1.202309021850-r");

        SimpleJsonObject json = new SimpleJsonObject();
        json.put("group", "org.json");
        json.put("artifact", "json");
        json.put("version", "20250107");

        central.put("dependencies", new SimpleJsonObject().put("jgit", jgit).put("json", json));

        SimpleJsonObject quipt = new SimpleJsonObject();
        quipt.put("type", "default");
        quipt.put("url", "https://repo.vanillaflux.com/repository/quipt/");
        SimpleJsonObject bot = new SimpleJsonObject();
        bot.put("group", "me.quickscythe");
        bot.put("artifact", "quipt-bot");
        bot.put("version", "1.0.1-alpha-B4");

        SimpleJsonObject core = new SimpleJsonObject();
        core.put("group", "me.quickscythe");
        core.put("artifact", "quipt-core");
        core.put("version", "0.0.7-alpha-B2");

        quipt.put("dependencies", new SimpleJsonObject().put("bot", bot).put("core", core));

        dependencies.put("repositories", new SimpleJsonObject().put("central", central).put("quipt", quipt));


        return dependencies;
    }

}
