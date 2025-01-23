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

@ConfigTemplate(name = "discord")
public class DiscordConfig extends Config {

    @ConfigValue
    public boolean enable_bot = false;

    @ConfigValue
    public String bot_token = "<token_here>";

    @ConfigValue
    public String server_status_channel = "none";

    @ConfigValue
    public String player_status_channel = "none";

    @ConfigValue
    public String chat_message_channel = "none";

    public DiscordConfig(File file, String name, QuiptIntegration plugin) {
        super(file, name, plugin);
    }
}
