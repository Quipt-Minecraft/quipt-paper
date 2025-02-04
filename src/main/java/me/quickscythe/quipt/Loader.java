package me.quickscythe.quipt;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class Loader implements PluginLoader {

    Logger logger = LoggerFactory.getLogger("QuiptLoader");

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        File dependenciesFolder = new File("dependencies/quipt-paper");
        if (!dependenciesFolder.exists()) dependenciesFolder.mkdirs();
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/gradle.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : Objects.requireNonNull(dependenciesFolder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                classpathBuilder.addLibrary(new JarLibrary(file.toPath()));
            }

        }
        System.out.println(properties);
        MavenLibraryResolver central = new MavenLibraryResolver();
        central.addRepository(new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build());
        central.addDependency(new Dependency(new DefaultArtifact("org.json:json:" + properties.getProperty("json_version")), null));
        central.addDependency(new Dependency(new DefaultArtifact("org.eclipse.jgit:org.eclipse.jgit:" + properties.getProperty("jgit_version")), null));


        MavenLibraryResolver quipt = new MavenLibraryResolver();
        quipt.addRepository(new RemoteRepository.Builder("quipt", "default", "https://repo.vanillaflux.com/repository/quipt/").build());
        quipt.addDependency(new Dependency(new DefaultArtifact("me.quickscythe:quipt-bot:" + properties.getProperty("quipt_bot_version")), null));
        quipt.addDependency(new Dependency(new DefaultArtifact("me.quickscythe:quipt-core:" + properties.getProperty("quipt_core_version")), null));


        classpathBuilder.addLibrary(central);
        classpathBuilder.addLibrary(quipt);
    }
}
