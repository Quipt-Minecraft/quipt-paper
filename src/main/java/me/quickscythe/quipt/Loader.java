/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import me.quickscythe.quipt.utils.CoreUtils;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Loader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver mavenCentral = new MavenLibraryResolver();
        mavenCentral.addDependency(new Dependency(new DefaultArtifact("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r"), null));
        mavenCentral.addDependency(new Dependency(new DefaultArtifact("org.json:json:20231013"), null));
        mavenCentral.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:4.0.3"), null));
        mavenCentral.addRepository(new RemoteRepository.Builder(
                "central", "default", "https://repo1.maven.org/maven2/"
        ).build());

        MavenLibraryResolver quipt = new MavenLibraryResolver();
        quipt.addDependency(new Dependency(new DefaultArtifact("me.quickscythe:quipt-bot:1.0"), null));
        quipt.addDependency(new Dependency(new DefaultArtifact("me.quickscythe:quipt-core:0.0.3-1.21-alpha-B1"), null));
        quipt.addRepository(new RemoteRepository.Builder(
                "quipt", "default", "https://repo.vanillaflux.com/repository/quipt/"
        ).build());
        classpathBuilder.addLibrary(quipt);
        classpathBuilder.addLibrary(mavenCentral);
        File dependenciesFolder = new File("plugins/dependencies");
        if(!dependenciesFolder.exists()) CoreUtils.logger().log("Loader", "Creating dependencies folder: " + dependenciesFolder.mkdir());
        for(File file : Objects.requireNonNull(dependenciesFolder.listFiles())) {
            if (file.getName().endsWith(".jar")) {
                classpathBuilder.addLibrary(new JarLibrary(file.toPath()));
            }
        }
    }
}
