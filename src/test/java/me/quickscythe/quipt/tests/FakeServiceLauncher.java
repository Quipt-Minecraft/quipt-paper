package me.quickscythe.quipt.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.quickscythe.quipt.QuiptPaperIntegration;
import me.quickscythe.quipt.tests.objects.BukkitConverterIntegration;
import me.quickscythe.quipt.tests.objects.BukkitServerMockTest;
import me.quickscythe.quipt.utils.CoreUtils;

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
