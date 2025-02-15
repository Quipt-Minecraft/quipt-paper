/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.quiptmc.minecraft.paper.files;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;
import com.quiptmc.minecraft.paper.files.discord.AnnouncementsNestedConfig;
import com.quiptmc.minecraft.paper.files.discord.ChannelsNestedConfig;

import java.io.File;

@ConfigTemplate(name = "discord")
public class DiscordConfig extends Config {

    @ConfigValue
    public boolean enable_bot = false;

    @ConfigValue
    public String bot_token = "<token_here>";

    @ConfigValue
    public AnnouncementsNestedConfig<?> announcements = null;

    @ConfigValue
    public ChannelsNestedConfig<?> channels = null;

    public DiscordConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }
}
