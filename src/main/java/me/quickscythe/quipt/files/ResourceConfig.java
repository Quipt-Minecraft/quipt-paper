/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
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

import java.io.File;

@ConfigTemplate(name = "resources")
public class ResourceConfig extends Config {

    @ConfigValue
    public String repo_url = "";

    @ConfigValue
    public String repo_branch = "main";

    @ConfigValue
    public boolean is_private = false;

    @ConfigValue
    public String repo_username = "user";

    @ConfigValue
    public String repo_password = "pass";

    @ConfigValue
    public int server_port = 9009;

    @ConfigValue
    public String server_ip = "127.0.0.1";

    public ResourceConfig(File file, String name, QuiptIntegration plugin) {
        super(file, name, plugin);
    }
}
