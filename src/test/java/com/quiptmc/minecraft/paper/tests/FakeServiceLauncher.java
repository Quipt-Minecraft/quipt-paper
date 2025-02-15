package com.quiptmc.minecraft.paper.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.quiptmc.minecraft.paper.QuiptPaperIntegration;
import com.quiptmc.minecraft.paper.tests.objects.BukkitConverterIntegration;
import com.quiptmc.minecraft.paper.tests.objects.BukkitServerMockTest;
import com.quiptmc.minecraft.paper.utils.CoreUtils;

public class FakeServiceLauncher {

    private static QuiptPaperIntegration paperIntegration;
    private static ServerMock server;

    public static void main(String[] args) {
        System.out.println("Hello, world!");
        server = MockBukkit.mock(new BukkitServerMockTest());
        BukkitConverterIntegration initializer = MockBukkit.load(BukkitConverterIntegration.class);
        paperIntegration = new QuiptPaperIntegration(initializer);
        CoreUtils.init(paperIntegration);
    }
}
