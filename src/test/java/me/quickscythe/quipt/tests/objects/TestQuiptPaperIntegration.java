package me.quickscythe.quipt.tests.objects;

import me.quickscythe.quipt.QuiptPaperIntegration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class TestQuiptPaperIntegration extends QuiptPaperIntegration {
    public TestQuiptPaperIntegration() {
        super(null);
        if(!dataFolder().exists()) dataFolder().mkdir();
    }

    @Override
    public File dataFolder() {
        return new File("test_data/plugins/QuiptTest");
    }
}
