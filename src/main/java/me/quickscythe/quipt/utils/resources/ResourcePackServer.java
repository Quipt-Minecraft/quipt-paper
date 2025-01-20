/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt.utils.resources;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.HashesConfig;
import me.quickscythe.quipt.files.ResourceConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.chat.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static net.kyori.adventure.text.Component.text;

public class ResourcePackServer {

    private final File pack;
    private final File repo;

    private final HashesConfig hashData;
    private final ResourceConfig packData;

    private boolean serverStarted = false;

    private byte[] storedHash = new byte[0];
//    byte[] hash = new byte[0];

    public ResourcePackServer() {
        hashData = ConfigManager.getConfig(CoreUtils.quiptPlugin(), HashesConfig.class);
        packData = ConfigManager.getConfig(CoreUtils.quiptPlugin(), ResourceConfig.class);
        pack = new File(CoreUtils.dataFolder(), "resources/pack.zip");
        repo = new File(CoreUtils.dataFolder(), "resources/repo/");
        if (!pack.getParentFile().isDirectory())
            CoreUtils.logger().log(Logger.LogLevel.INFO, "Resources",
                    pack.getParentFile().mkdirs()
                            ? "Set up 'pack.zip' parents."
                            : "Couldn't set up 'pack.zip' parents.");
        if (!repo.exists())
            CoreUtils.logger().log(Logger.LogLevel.INFO, "Resources",
                    repo.mkdirs()
                            ? "Set up 'repo' directory."
                            : "Couldn't set up 'repo' directory.");
        if(pack.exists()){
            startServer();
        }
    }

    private void startServer() {
        try {

            //Setup server
            HttpServer server = HttpServer.create(new InetSocketAddress(packData.server_port), 0);
            server.createContext("/resources.zip", new ResourcePackHandler(this));
            server.createContext("/update", new ResourcePackUpdater(this));
            server.setExecutor(null);
            server.start();

            //
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream in = Files.newInputStream(pack.toPath())) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) > 0) {
                    digest.update(buffer, 0, count);
                }
            }
            storedHash = digest.digest();
            serverStarted = true;
            //sync
            sync();
        } catch (IOException | NoSuchAlgorithmException e) {
            CoreUtils.logger().error("ResourcePackServer", e);
        }
    }

    private void sync() {
        CoreUtils.logger().log("Resources", "Syncing resource pack.");
        if (repo.exists() && new File(repo, ".git").exists()) updateRepo();
        else cloneRepo();
    }

    private void updateRepo() {
        CoreUtils.logger().log("Resources", "Updating resource pack.");
        try {
            Git git = Git.open(repo);
            git.pull().call();
            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
            git.close();

            zip(commit);
        } catch (GitAPIException | IOException e) {
            CoreUtils.logger().error("Resources", e);
        }
    }

    private void cloneRepo() {
        if (!enabled()) return;
        CoreUtils.logger().log("Resources", "Cloning resource pack.");
        try {
            Git git = Git.cloneRepository().setURI(packData.repo_url).setDirectory(repo).setBranch(packData.repo_branch).call();

            CoreUtils.logger().log("Resources", "Cloned resource pack.");
            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
            git.close();
            zip(commit);
        } catch (GitAPIException e) {
            CoreUtils.logger().log("Resources", "Error cloning repository: " + e.getMessage());
            CoreUtils.logger().error("Resources", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void zip(RevCommit commit) {
        CoreUtils.logger().log("Resources", "Zipping resource pack.");
        try {
            zipFolder(repo.toPath(), pack.toPath());
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream in = Files.newInputStream(pack.toPath())) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = in.read(buffer)) > 0) {
                    digest.update(buffer, 0, count);
                }
            }
            byte[] newHash = digest.digest();

            String newEncryptedHash = new String(newHash, StandardCharsets.UTF_8);

            String newCommitHash = commit.getId().getName();

            if (newEncryptedHash.equals(hashData.encrypted_zip_hash)) {
                CoreUtils.logger().log("Resources", "Resource pack hash matches. Skipping update.");
                return;
            }
            if (hashData.commit_hash.equals(newCommitHash)) {
                CoreUtils.logger().log("Resources", "Commit hash match. Skipping update.");
                return;
            }
            storedHash = newHash;
            hashData.commit_hash = newCommitHash;
            hashData.encrypted_zip_hash = newEncryptedHash;
            CoreUtils.logger().log("Resources", "Resource pack hash mismatch. Updating pack.");
            updatePack();
        } catch (IOException | NoSuchAlgorithmException e) {
            CoreUtils.logger().log("Resources", "Error zipping resource pack: " + e.getMessage());
            CoreUtils.logger().error("Resources", e);
        }
    }

    public boolean enabled() {
        return !packData.repo_url.isEmpty();
    }

    public void setUrl(String url) {
        if (url.isEmpty()) return;
        if(!serverStarted)
            startServer();
        String oldUrl = packData.repo_url;
        if (!oldUrl.equals(url)) {
            CoreUtils.logger().log("Resources", "Resource pack URL changed. Updating pack.");
            try {
                Files.walk(repo.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                cloneRepo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        packData.repo_url = url;

    }

    public void updatePack() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Component msg = text("Resource pack updated. Click here to reload.").color(TextColor.color(0x49DFFF));
            msg = msg.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack reload"));
            player.sendMessage(msg);
        }
    }

    public File pack() {
        return pack;
    }

    public void setPack(Player player) throws IOException, NoSuchAlgorithmException {
        if (!enabled()) return;
        String url = "http://" + packData.server_ip + ":" + packData.server_port + "/resources.zip";

        player.setResourcePack(url, storedHash, text("This pack is required for the best experience on this server."));
    }

    private void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(sourceFolderPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
                try {
                    zos.putNextEntry(zipEntry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    System.err.println("Error zipping file: " + path + " - " + e.getMessage());
                }
            });
        }
    }


    static class ResourcePackHandler implements HttpHandler {

        private final ResourcePackServer server;

        public ResourcePackHandler(ResourcePackServer server) {
            this.server = server;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = server.pack().getPath(); // Update this path to your file
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // Set the response headers and status code
            exchange.sendResponseHeaders(200, fileBytes.length);

            // Write the file bytes to the response body
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        }
    }

    static class ResourcePackUpdater implements HttpHandler {

        private final ResourcePackServer server;

        public ResourcePackUpdater(ResourcePackServer server) {
            this.server = server;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            server.sync();
        }
    }
}
