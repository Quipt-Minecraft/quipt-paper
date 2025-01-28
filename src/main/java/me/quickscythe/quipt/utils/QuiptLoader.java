package me.quickscythe.quipt.utils;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import me.quickscythe.quipt.json.SimpleJsonObject;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public abstract class QuiptLoader implements PluginLoader {

    public abstract String name();

    public abstract void registerDependencies(PluginClasspathBuilder classpathBuilder);
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        File dependenciesFolder = new File("libraries");
        if (!dependenciesFolder.exists()) dependenciesFolder.mkdirs();
        for (File file : Objects.requireNonNull(dependenciesFolder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                classpathBuilder.addLibrary(new JarLibrary(file.toPath()));
            }
        }

        File dependenciesFile = new File("dependencies-" + name() + ".json");
        SimpleJsonObject dependencies;
        if (!dependenciesFile.exists()) {
            try {
                dependencies = defaultDependencies(dependenciesFile);
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

        registerDependencies(classpathBuilder);

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

    public abstract SimpleJsonObject defaultDependencies(File dependenciesFile) throws IOException;
}
