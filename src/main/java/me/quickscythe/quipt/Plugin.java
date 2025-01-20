/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.quipt;

import me.quickscythe.quipt.api.QuiptPlugin;
import me.quickscythe.quipt.utils.CoreUtils;

import java.io.File;

public class Plugin implements QuiptPlugin {

    private final String name = "Quipt";
    private final File dataFolder;


    public Plugin() {
        dataFolder = new File("config");
        if(!dataFolder.exists()) dataFolder.mkdir();

    }
    @Override
    public void log(String tag, String message) {
        CoreUtils.logger().log(tag, message);
    }

    @Override
    public File dataFolder() {
        return dataFolder;
    }

    @Override
    public String name() {
        return name;
    }
}
