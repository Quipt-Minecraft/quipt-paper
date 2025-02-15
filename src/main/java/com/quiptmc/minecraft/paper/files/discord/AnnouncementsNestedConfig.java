package com.quiptmc.minecraft.paper.files.discord;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigValue;
import com.quiptmc.core.config.NestedConfig;

public class AnnouncementsNestedConfig<T extends Config> extends NestedConfig<T> {

    @ConfigValue
    public boolean join = false;

    @ConfigValue
    public boolean leave = false;

    @ConfigValue
    public boolean death = false;

    @ConfigValue
    public boolean chat = false;

    @ConfigValue
    public boolean server_start = false;



    public AnnouncementsNestedConfig(T parent, String name, QuiptIntegration integration) {
        super(parent, name, integration);
    }


}
