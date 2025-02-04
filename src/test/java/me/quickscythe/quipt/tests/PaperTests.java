package me.quickscythe.quipt.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.quickscythe.quipt.QuiptPaperIntegration;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.*;
import me.quickscythe.quipt.tests.objects.BukkitConverterIntegration;
import me.quickscythe.quipt.tests.objects.BukkitServerMockTest;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaperTests {

    private BukkitConverterIntegration initializer;

    private QuiptPaperIntegration paperIntegration;

    private ServerMock server;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new BukkitServerMockTest());
        initializer = MockBukkit.load(BukkitConverterIntegration.class);
        paperIntegration = new QuiptPaperIntegration(initializer);
        CoreUtils.init(paperIntegration);
    }

    @Test
    void testConfigs() {
        assertNotNull(ConfigManager.getConfig(paperIntegration, ResourceConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, DiscordConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, HashesConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, JenkinsConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, SessionConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, TeleportationConfig.class));
        paperIntegration.log("test", ConfigManager.getConfig(paperIntegration, ResourceConfig.class).file().getAbsolutePath());
    }

    @Test
    void testTeleportation() {
        server.setPlayers(2);
        PlayerMock player1 = server.getPlayer(0);
        PlayerMock player2 = server.getPlayer(1);

        player1.teleport(new Location(player1.getWorld(), 50, 0, 50));
        player2.teleport(new Location(player2.getWorld(), -100, 0, -100));

        LocationUtils.TeleportRequest request = LocationUtils.request(player1, player2);
        request.send();
        request.accept();

        assertEquals(player1.getLocation(), player2.getLocation(), "Players should be at the same location");

    }

    @AfterEach
    public void tearDown() throws IOException {
//        paperIntegration.destroy();
        MockBukkit.unmock();
    }
}
