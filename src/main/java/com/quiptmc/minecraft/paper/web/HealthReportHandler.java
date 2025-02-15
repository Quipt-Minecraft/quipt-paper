package com.quiptmc.minecraft.paper.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.quiptmc.core.logger.QuiptLogger;
import com.quiptmc.core.server.QuiptServer;
import com.quiptmc.core.server.QuiptServlet;
import com.quiptmc.minecraft.paper.utils.CoreUtils;
import com.quiptmc.minecraft.paper.utils.chat.placeholder.PlaceholderUtils;
import com.quiptmc.minecraft.paper.utils.heartbeat.Flutter;
import com.quiptmc.minecraft.paper.utils.heartbeat.HeartbeatUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class HealthReportHandler extends QuiptServlet {

    private final File folder;
    Map<Long, Double> tps = new HashMap<>();
    private final Map<Long, StatusScreenshot> screenshots = new HashMap<>();

    public HealthReportHandler(QuiptServer server) {
        super(server);
        PlaceholderUtils.registerPlaceholder("web_data", (player) -> {
            JSONObject data = new JSONObject();
            for (Map.Entry<Long, StatusScreenshot> entry : screenshots.entrySet()) {

                data.put(entry.getKey().toString(), entry.getValue().json());
            }
            return data.toString();
        });
        folder = new File(server.integration().dataFolder(), "web");
        if (!folder.exists()) {
            QuiptLogger logger = server.integration().logger();
            logger.log("Web folder does not exist, creating: %s", folder.mkdirs() ? "Success" : "Failed");
            logger.log("Copying web files");

            try {
                copyResources("web", folder, true);
            } catch (IOException e) {
                server.integration().logger().error("Failed to copy web files", e);
            }
        }

        HeartbeatUtils.heartbeat(CoreUtils.integration()).addFlutter(new Flutter() {
            long lastRan = 0;
            long delay = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS);

            @Override
            public boolean run() {
                long now = System.currentTimeMillis();
                if (now - lastRan > delay) {
                    lastRan = now;
                    takeScreenshot();
                }
                return true;
            }
        });


    }

    public void copyResources(String resourcePath, File targetDirectory, boolean isRoot) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File resourceDirectory = new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile());

        if (resourceDirectory.isDirectory()) {
            for (String child : Objects.requireNonNull(resourceDirectory.list())) {
                copyResources(resourcePath + "/" + child, isRoot ? targetDirectory : new File(targetDirectory, resourceDirectory.getName()), false);
            }
        } else {
            if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                throw new IOException("Failed to create target directory: " + targetDirectory);
            }

            try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
                if (resourceStream == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }

                File targetFile = new File(targetDirectory, resourceDirectory.getName());
                Files.copy(resourceStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        updateTPS();
        String finalPath = req.getRequestURI().replaceFirst("/healthreport", "");

        if (!finalPath.endsWith(".html") && !finalPath.endsWith(".css") && !finalPath.endsWith(".js") && !finalPath.endsWith(".png") && !finalPath.endsWith("jpg") && !finalPath.endsWith(".jpng"))
            finalPath += (finalPath.endsWith("/") ? "" : "/") + "index.html";

        String fileExtension = finalPath.substring(finalPath.lastIndexOf(".") + 1);

        resp.setContentType("text/" + fileExtension);

        File htmlFile = new File(folder, finalPath.substring(1));

        if (htmlFile.exists()) {

            try {
                // Read the HTML file into a string
                BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
                StringBuilder htmlBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    htmlBuilder.append(line).append("\n");
                }
                reader.close();

                // Replace the placeholders with their corresponding values
                String htmlContent = htmlBuilder.toString();

                resp.getWriter().write(PlaceholderUtils.replace(null, htmlContent));

            } catch (IOException e) {
                e.printStackTrace();
            }

//            try (InputStream inputStream = new FileInputStream(htmlFile); ServletOutputStream outputStream = resp.getOutputStream()) {
//
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "HTML file not found");
        }


    }

    public void takeScreenshot() {
        screenshots.put(System.currentTimeMillis(), new StatusScreenshot(Bukkit.getTPS()[0], (double) Runtime.getRuntime().freeMemory() /Runtime.getRuntime().maxMemory(), CoreUtils.getCPUUsage(), Bukkit.getOnlinePlayers().size()));
        List<Long> remove = new ArrayList<>();
        for (long time : screenshots.keySet()) {
            if (System.currentTimeMillis() - time > TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)) {
                remove.add(time);
            }
        }
        for (long time : remove) {
            screenshots.remove(time);
        }
    }

    public void updateTPS() {
        tps.put(System.currentTimeMillis(), Bukkit.getTPS()[0]);
        List<Long> remove = new ArrayList<>();
        for (long time : tps.keySet()) {
            if (System.currentTimeMillis() - time > TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)) {
                remove.add(time);
            }
        }
        for (long time : remove) {
            tps.remove(time);
        }
    }

    record StatusScreenshot(double tps, double memory, double cpu, int players) {

        public JSONObject json(){
            JSONObject object = new JSONObject();
            object.put("tps", tps);
            object.put("memory", memory);
            object.put("cpu", cpu);
            object.put("players", players);
            return object;
        }

    }

}
