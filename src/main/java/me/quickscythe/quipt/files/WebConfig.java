package me.quickscythe.quipt.files;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.Config;
import me.quickscythe.quipt.api.config.ConfigTemplate;
import me.quickscythe.quipt.api.config.ConfigValue;
import me.quickscythe.quipt.files.web.HealthReportNestedConfig;

import java.io.File;

@ConfigTemplate(name = "web")
public class WebConfig extends Config {

    @ConfigValue
    public boolean enable = true;

    @ConfigValue
    public int port = 5050;

    @ConfigValue
    public String host = "127.0.0.1";

    @ConfigValue
    public String webRoot = "web";

    @ConfigValue
    public HealthReportNestedConfig<WebConfig> healthreport = null;

    public WebConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }

    /**
     * @param file The file to save to
     * @param name The name of the config
     */
}
