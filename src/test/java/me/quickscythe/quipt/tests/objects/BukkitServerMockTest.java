package me.quickscythe.quipt.tests.objects;

import be.seeseemelk.mockbukkit.ServerMock;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BukkitServerMockTest extends ServerMock {

    @Override
    public @NotNull File getPluginsFolder() {
        File file = new File("test_data/plugins");
        if (!file.exists()) file.mkdirs();
        return file;
    }
}
