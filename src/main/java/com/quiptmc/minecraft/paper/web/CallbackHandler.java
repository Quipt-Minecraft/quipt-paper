package com.quiptmc.minecraft.paper.web;

import com.quiptmc.core.server.QuiptServer;
import com.quiptmc.core.server.QuiptServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CallbackHandler extends QuiptServlet {

    public CallbackHandler(QuiptServer server) {
        super(server);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        String action = req.getParameter("action");
        if (action == null) {
            resp.setStatus(400);
            return;
        }

        if(action.equalsIgnoreCase("send_message")){
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            resp.setStatus(200);
            resp.getWriter().write("{\"status\":\"success\"}");
            JSONObject json = new JSONObject(requestBody.toString());
            UUID uuid = UUID.fromString(json.getJSONObject("action").getString("to"));
            String message = json.getJSONObject("action").getString("message");
            if(Bukkit.getPlayer(uuid) != null){
                Bukkit.getPlayer(uuid).sendMessage(message);
            }
            System.out.println(requestBody.toString());
        }

        if (req.getRequestURI().equalsIgnoreCase("/api/action/")) {

        }
    }
}
