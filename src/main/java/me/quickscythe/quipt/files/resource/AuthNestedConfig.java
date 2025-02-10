package me.quickscythe.quipt.files.resource;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.Config;
import me.quickscythe.quipt.api.config.ConfigTemplate;
import me.quickscythe.quipt.api.config.ConfigValue;
import me.quickscythe.quipt.api.config.NestedConfig;

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
