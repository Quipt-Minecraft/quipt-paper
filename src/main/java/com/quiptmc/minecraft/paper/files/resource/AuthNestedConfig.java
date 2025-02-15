package com.quiptmc.minecraft.paper.files.resource;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;
import com.quiptmc.core.config.NestedConfig;

@ConfigTemplate(name = "auth")
public class AuthNestedConfig<T extends Config> extends NestedConfig<T> {

    @ConfigValue
    public boolean isPrivate = false;

    @ConfigValue
    public String username = "username";

    @ConfigValue
    public String password = "password";

    public AuthNestedConfig(T parent, String name, QuiptIntegration integration) {
        super(parent, name, integration);
    }


}
