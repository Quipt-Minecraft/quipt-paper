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
import me.quickscythe.quipt.utils.chat.placeholder.PlaceholderUtils;
import me.quickscythe.quipt.utils.sessions.SessionManager;
import me.quickscythe.quipt.utils.teleportation.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PaperTests {

    private QuiptPaperIntegration paperIntegration;
    private ServerMock server;

    @BeforeEach
    public void setUp() {

        server = MockBukkit.mock(new BukkitServerMockTest());
        BukkitConverterIntegration initializer = MockBukkit.load(BukkitConverterIntegration.class);
        paperIntegration = new QuiptPaperIntegration(initializer);
        CoreUtils.init(paperIntegration);
    }

    @Test
    void testConfigs() {
        assertNotNull(ConfigManager.getConfig(paperIntegration, ResourceConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, DiscordConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, JenkinsConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, SessionConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, TeleportationConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, WebConfig.class));
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
        player1.disconnect();
        player2.disconnect();

    }

    @Test
    void testSessions() throws InterruptedException {
        PlayerMock player1 = server.addPlayer();
        PlayerMock player2 = server.addPlayer();
        player1.setJumping(true);
        player1.simulateBlockPlace(Material.REDSTONE_BLOCK, player1.getLocation());
        player2.setJumping(true);
        player1.disconnect();
        player2.disconnect();
        player1.reconnect();
        player1.simulatePlayerMove(player1.getLocation().add(0, 0, 20));
        player1.disconnect();
        assertFalse(SessionManager.getConfig().sessions.isEmpty(), "Sessions shouldn't be empty");
    }

    @Test
    void testPlaceholders() {
        PlaceholderUtils.registerPlaceholder("test", (test)-> "test-" + (test.map(Player::getName).orElse("Unknown")));
        assertEquals("test-Unknown", PlaceholderUtils.replace(null, "%test%"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        paperIntegration.destroy();
        MockBukkit.unmock();
    }
}
