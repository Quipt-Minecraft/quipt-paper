package com.quiptmc.minecraft.paper.utils;


import com.quiptmc.minecraft.paper.QuiptPaperIntegration;
import com.sun.management.OperatingSystemMXBean;
import com.quiptmc.core.data.registries.Registries;
import com.quiptmc.core.data.registries.Registry;
import com.quiptmc.minecraft.paper.utils.entity.QuiptEntityType;
import com.quiptmc.minecraft.paper.utils.entity.entities.*;
import com.quiptmc.minecraft.paper.web.ResourcePackHandler;

import java.lang.management.ManagementFactory;

public class CoreUtils {

    private static QuiptPaperIntegration integration;


    public static void preInit() {
        //DO NOT USE BUKKIT CODE HERE. ONLY REGISTER DATA UNRELATED TO PAPER AND QUIPT INTEGRATIONS
    }

    public static void init(QuiptPaperIntegration integration) {

//        QueueTask task = new QueueTask() {
//            @Override
//            public void run() {
//
//            }
//
//            @Override
//            public double progress() {
//                return 0;
//            }
//
//        };
//
//
//        QueueManager.queueTask("init", task);

        Registries.KEYS.register("entities").ifPresent(key -> {
            Registries.REGISTRAR.add(key, new Registry<>(QuiptEntityType.class));
            Registries.REGISTRAR.get(key, QuiptEntityType.class).ifPresent(registry -> {
                registry.register("allay", new QuiptEntityType<>(QuiptAlly.class));
                registry.register("armadillo", new QuiptEntityType<>(QuiptArmadillo.class));
                registry.register("axolotl", new QuiptEntityType<>(QuiptAxolotl.class));
                registry.register("bat", new QuiptEntityType<>(QuiptBat.class));
                registry.register("bee", new QuiptEntityType<>(QuiptBee.class));
                registry.register("blaze", new QuiptEntityType<>(QuiptBlaze.class));
                registry.register("bogged", new QuiptEntityType<>(QuiptBogged.class));
                registry.register("breeze", new QuiptEntityType<>(QuiptBreeze.class));
                registry.register("camel", new QuiptEntityType<>(QuiptCamel.class));
                registry.register("cat", new QuiptEntityType<>(QuiptCat.class));
                registry.register("cave_spider", new QuiptEntityType<>(QuiptCaveSpider.class));
                registry.register("chicken", new QuiptEntityType<>(QuiptChicken.class));
                registry.register("cod", new QuiptEntityType<>(QuiptCod.class));
                registry.register("cow", new QuiptEntityType<>(QuiptCow.class));
                registry.register("creeper", new QuiptEntityType<>(QuiptCreeper.class));
                registry.register("dolphin", new QuiptEntityType<>(QuiptDolphin.class));

            });
        });

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
