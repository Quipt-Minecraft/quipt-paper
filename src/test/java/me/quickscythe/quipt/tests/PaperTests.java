package me.quickscythe.quipt.tests;

import me.quickscythe.quipt.QuiptPaperIntegration;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.*;
import me.quickscythe.quipt.tests.objects.TestQuiptPaperIntegration;
import me.quickscythe.quipt.utils.CoreUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaperTests {

//    private Initializer initializer;

    private QuiptPaperIntegration paperIntegration;

    @BeforeEach
    public void setUp() {
        paperIntegration = new TestQuiptPaperIntegration();
        CoreUtils.init(paperIntegration);

//        MockitoAnnotations.openMocks(this);
//        try (MockedStatic<Bukkit> mockedBukkit = Mockito.mockStatic(Bukkit.class)) {
//            String expectedVersionMessage = "Mocked Version Message";
//            mockedBukkit.when(Bukkit::getVersionMessage).thenReturn(expectedVersionMessage);
//
////            Server server = mock(Server.class);
////            PluginManager pluginManager = mock(PluginManager.class);
////            mockedBukkit.when(Bukkit::getServer).thenReturn(server);
////            when(server.getUnsafe()).thenReturn(ObjectFactory.getUnsafeValues());
////            mockedBukkit.when(Bukkit::getUnsafe).thenReturn(ObjectFactory.getUnsafeValues());
////            when(server.getPluginManager()).thenReturn(pluginManager);
//
//            initializer = mock(Initializer.class);
////            when(server.getPluginManager().getPlugin("DragonForgeCore")).thenReturn(initializer);
//            CoreUtils.init(initializer, new PaperIntegration());
//            paperIntegration = CoreUtils.integration();
//        }
    }

    @Test
    void testConfigs() throws IOException {
        assertNotNull(ConfigManager.getConfig(paperIntegration, ResourceConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, DiscordConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, HashesConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, JenkinsConfig.class));
        assertNotNull(ConfigManager.getConfig(paperIntegration, SessionConfig.class));

        paperIntegration.destroy();




    }
}
