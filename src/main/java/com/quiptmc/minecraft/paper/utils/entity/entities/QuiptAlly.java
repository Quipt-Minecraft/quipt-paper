package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptAlly extends Allay implements QuiptEntity<Allay> {
    public QuiptAlly(Location location) {
        super(EntityType.ALLAY, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}
