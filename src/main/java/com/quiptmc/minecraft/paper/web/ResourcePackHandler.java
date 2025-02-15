package com.quiptmc.minecraft.paper.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.server.QuiptServer;
import com.quiptmc.core.server.QuiptServlet;
import com.quiptmc.minecraft.paper.files.ResourceConfig;
import com.quiptmc.minecraft.paper.files.WebConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static net.kyori.adventure.text.Component.text;

public class ResourcePackHandler extends QuiptServlet {


    private final File pack;
    private final File repo;
    private final ResourceConfig packData;

    private boolean serverStarted = false;
    private byte[] storedHash = new byte[0];

    public ResourcePackHandler(QuiptServer server) {
        super(server);

        packData = ConfigManager.getConfig(server.integration(), ResourceConfig.class);
        pack = new File(server.integration().dataFolder(), "resources/pack.zip");
        repo = new File(server.integration().dataFolder(), "resources/repo/");
        if (!pack.getParentFile().isDirectory())
            server.integration().logger().log(pack.getParentFile().mkdirs() ? "Set up 'pack.zip' parents." : "Couldn't set up 'pack.zip' parents.");
        if (!repo.exists())
            server.integration().logger().log(repo.mkdirs() ? "Set up 'repo' directory." : "Couldn't set up 'repo' directory.");
        if (pack.exists()) {
            sync();
        }
    }

    public void start() {
        try {
            if (!pack.exists()) pack.createNewFile();
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
            sync();
        } catch (IOException | NoSuchAlgorithmException e) {
            server().integration().logger().error("Error starting resource pack server", e);
        }
    }

    public void sync() {
        server().integration().logger().log("Syncing resource pack.");
        if (repo.exists() && new File(repo, ".git").exists()) updateRepo();
        else cloneRepo();
    }

    private void updateRepo() {
        server().integration().logger().log("Updating resource pack.");
        try {
            Git git = Git.open(repo);
            git.pull().call();
            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
            git.close();

            zip(commit);
        } catch (GitAPIException | IOException e) {
            server().integration().logger().error("There was an error updating the repo", e);
        }
    }

    private void cloneRepo() {
        if (!enabled()) return;
        server().integration().logger().log("Cloning resource pack.");
        try {
            Git git = Git.cloneRepository().setURI(packData.repo_url).setDirectory(repo).setBranch(packData.repo_branch).call();

            server().integration().logger().log("Cloned resource pack.");
            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
            git.close();
            zip(commit);
        } catch (GitAPIException e) {
            server().integration().logger().error("Error cloning repository", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void zip(RevCommit commit) {
        server().integration().logger().log("Zipping resource pack.");
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

            if(Arrays.equals(storedHash, newHash)){
                server().integration().logger().log("Resource pack hash matches. Skipping update.");
                updateHashes(newHash, newCommitHash, newEncryptedHash);
                return;
            }

            if (newEncryptedHash.equals(packData.hashes.encrypted_zip_hash)) {
                server().integration().logger().log("Resource pack hash matches. Skipping update.");
                updateHashes(newHash, newCommitHash, newEncryptedHash);
                return;
            }
            if (packData.hashes.commit_hash.equals(newCommitHash)) {
                server().integration().logger().log("Commit hash match. Skipping update.");
                updateHashes(newHash, newCommitHash, newEncryptedHash);
                return;
            }
            updateHashes(newHash, newCommitHash, newEncryptedHash);
            packData.save();
            server().integration().logger().log("Resource pack hash mismatch. Updating pack.");
            updatePack();
        } catch (IOException | NoSuchAlgorithmException e) {
            server().integration().logger().error("Error zipping resource pack", e);
        }
    }

    private void updateHashes(byte[] newHash, String newCommitHash, String newEncryptedHash) {
        storedHash = newHash;
        packData.hashes.commit_hash = newCommitHash;
        packData.hashes.encrypted_zip_hash = newEncryptedHash;
    }

    public boolean enabled() {
        return !packData.repo_url.isEmpty();
    }

    public void setUrl(String url) {
        if (url.isEmpty()) return;
        if (!serverStarted) start();
        String oldUrl = packData.repo_url;
        if (!oldUrl.equals(url)) {
            server().integration().logger().log("Resources", "Resource pack URL changed. Updating pack.");
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
        for (Player player : Bukkit.getOnlinePlayers()) {
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
        WebConfig webConfig = ConfigManager.getConfig(server().integration(), WebConfig.class);
        String url = "http://" + webConfig.host + ":" + webConfig.port + "/resources/pack.zip";
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream in = Files.newInputStream(pack.toPath())) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
        }
        byte[] newHash = digest.digest();
        player.setResourcePack(url, newHash, text("This pack is required for the best experience on this server.", TextColor.color(0x22E3DF)));
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String uri = req.getRequestURI().toLowerCase();
        uri = uri.replaceFirst("/resources/", "");

        if (uri.equalsIgnoreCase("pack.zip")) {
//            resp.setContentType("application/zip");
//            resp.setHeader("Content-Disposition", "attachment; filename=\"" + packServer().pack().getName() + "\"");
            resp.setContentLengthLong(pack().length());

            try (FileInputStream fis = new FileInputStream(pack()); OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
        if (uri.toLowerCase().startsWith("update")) {
            sync();
        }
    }


}