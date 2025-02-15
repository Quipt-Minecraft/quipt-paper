package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Bogged;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptBogged extends Bogged implements QuiptEntity<Bogged> {
    public QuiptBogged(Location location) {
        super(EntityType.BOGGED, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}