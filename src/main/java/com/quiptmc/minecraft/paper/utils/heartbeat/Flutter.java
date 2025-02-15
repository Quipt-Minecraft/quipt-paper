package com.quiptmc.minecraft.paper.utils.heartbeat;

import java.net.UnknownHostException;

@FunctionalInterface
public interface Flutter {

    boolean run() throws UnknownHostException;
}