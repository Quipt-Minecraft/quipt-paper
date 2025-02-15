package com.quiptmc.minecraft.paper.files.web;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;
import com.quiptmc.core.config.NestedConfig;

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
