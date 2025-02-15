package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptBlaze extends Blaze implements QuiptEntity<Blaze> {
    public QuiptBlaze(Location location) {
        super(EntityType.BLAZE, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}