package com.quiptmc.minecraft.paper.files.discord;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigValue;
import com.quiptmc.core.config.NestedConfig;

public class ChannelsNestedConfig<T extends Config> extends NestedConfig<T> {

    @ConfigValue
    public String player_status = "";

    @ConfigValue
    public String server_status = "";

    public ChannelsNestedConfig(T parent, String name, QuiptIntegration integration) {
        super(parent, name, integration);
    }
}
