/*
 * Copyright (c) 2024-2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt.files;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.Config;
import me.quickscythe.quipt.api.config.ConfigTemplate;
import me.quickscythe.quipt.api.config.ConfigValue;
import org.json.JSONObject;

import java.io.File;

@ConfigTemplate(name = "teleport_points")
public class TeleportationConfig extends Config {

    @ConfigValue
    public JSONObject locations = new JSONObject();

    /**
     * @param file The file to save to
     * @param name The name of the config
     */
    public TeleportationConfig(File file, String name, QuiptIntegration plugin) {
        super(file, name, plugin);
    }
}
