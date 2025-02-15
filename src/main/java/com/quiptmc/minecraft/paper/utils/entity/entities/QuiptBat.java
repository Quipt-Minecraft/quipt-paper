package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptBat extends Bat implements QuiptEntity<Bat> {
    public QuiptBat(Location location) {
        super(EntityType.BAT, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}
