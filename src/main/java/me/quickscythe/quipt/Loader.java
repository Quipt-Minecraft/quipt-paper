package me.quickscythe.quipt;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import me.quickscythe.quipt.json.SimpleJsonObject;
import me.quickscythe.quipt.utils.CoreUtils;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Loader implements PluginLoader {
    private static SimpleJsonObject loadDefaultDependencies(File dependenciesFile) throws IOException {
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

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        File dependenciesFolder = new File("libraries");
        if (!dependenciesFolder.exists()) dependenciesFolder.mkdirs();
        for (File file : Objects.requireNonNull(dependenciesFolder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                classpathBuilder.addLibrary(new JarLibrary(file.toPath()));
            }
        }

        File dependenciesFile = new File("dependencies.json");
        SimpleJsonObject dependencies;
        if (!dependenciesFile.exists()) {
            try {
                dependencies = loadDefaultDependencies(dependenciesFile);
                try (FileWriter writer = new FileWriter(dependenciesFile)) {
                    writer.write(dependencies.toString(2));
                } catch (IOException e) {
                    CoreUtils.logger().error("Loader", e);
                    return;
                }
            } catch (IOException e) {
                CoreUtils.logger().error("Loader", e);
                return;
            }
        } else {
            try {
                Scanner scanner = new Scanner(dependenciesFile);
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }
                dependencies = new SimpleJsonObject(builder.toString());
            } catch (IOException e) {
                CoreUtils.logger().error("Loader", e);
                return;
            }
        }

        for (String key : dependencies.getJSONObject("repositories").keySet()) {
            SimpleJsonObject repository = dependencies.getJSONObject("repositories").getJSONObject(key);
            MavenLibraryResolver resolver = new MavenLibraryResolver();
            resolver.addRepository(new RemoteRepository.Builder(
                    key, repository.getString("type"), repository.getString("url")
            ).build());
            for (String depKey : repository.getJSONObject("dependencies").keySet()) {
                SimpleJsonObject dep = repository.getJSONObject("dependencies").getJSONObject(depKey);
                resolver.addDependency(new Dependency(
                        new DefaultArtifact(dep.getString("group") + ":" + dep.getString("artifact") + ":" + dep.getString("version")),
                        null
                ));
            }
            classpathBuilder.addLibrary(resolver);
        }
    }
}
