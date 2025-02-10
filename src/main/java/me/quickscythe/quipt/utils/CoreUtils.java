package me.quickscythe.quipt.utils;


import com.sun.management.OperatingSystemMXBean;
import me.quickscythe.quipt.QuiptPaperIntegration;
import me.quickscythe.quipt.web.handlers.ResourcePackHandler;

import java.lang.management.ManagementFactory;

public class CoreUtils {

    private static QuiptPaperIntegration integration;


    public static void preInit() {
        //DO NOT USE BUKKIT CODE HERE. ONLY REGISTER DATA UNRELATED TO PAPER AND QUIPT INTEGRATIONS
    }

    public static void init(QuiptPaperIntegration integration) {
        CoreUtils.integration = integration;

        integration.enable();


    }


    public static QuiptPaperIntegration integration() {
        return integration;
    }

    public static double getCPUUsage() {
        return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad() * 100;
    }

    public static ResourcePackHandler packHandler() {
        return integration().packHandler();
    }
}
