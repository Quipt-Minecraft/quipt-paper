package me.quickscythe.quipt.files.web;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.Config;
import me.quickscythe.quipt.api.config.ConfigTemplate;
import me.quickscythe.quipt.api.config.ConfigValue;
import me.quickscythe.quipt.api.config.NestedConfig;

@ConfigTemplate(name = "health-report")
public class HealthReportNestedConfig<T extends Config> extends NestedConfig<T> {

    @ConfigValue
    public boolean enable = true;
    public String update = "1h";
    public String root = "/healthreport";


    public HealthReportNestedConfig(T parent, String name, QuiptIntegration integration) {
        super(parent, name, integration);
    }
}
