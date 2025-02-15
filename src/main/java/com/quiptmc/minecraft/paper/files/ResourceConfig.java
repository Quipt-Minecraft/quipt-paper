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
import com.quiptmc.minecraft.paper.files.resource.AuthNestedConfig;
import com.quiptmc.minecraft.paper.files.resource.HashesNestedConfig;

import java.io.File;

@ConfigTemplate(name = "resources",ext = ConfigTemplate.Extension.QPT)
public class ResourceConfig extends Config {

    @ConfigValue
    public String repo_url = "";

    @ConfigValue
    public String repo_branch = "main";

    @ConfigValue
    public AuthNestedConfig<ResourceConfig> auth = null;

    @ConfigValue
    public HashesNestedConfig<ResourceConfig> hashes = null;


    public ResourceConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }
}
