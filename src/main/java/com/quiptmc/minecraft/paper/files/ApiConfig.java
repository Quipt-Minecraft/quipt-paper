package com.quiptmc.minecraft.paper.files;


import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;

import java.io.File;

@ConfigTemplate(name = "api")
public class ApiConfig extends Config {

    @ConfigValue
    public String endpoint = "https://quipt-api.azurewebsites.net/api";

//    @ConfigValue
//    public String endpoint = "https://api.quiptmc.com";

    @ConfigValue
    public String secret = "You Should Change This To Literally Anything Else";

    public ApiConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }
}
