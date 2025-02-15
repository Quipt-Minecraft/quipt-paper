package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptCat extends Cat implements QuiptEntity<Cat> {
    public QuiptCat(Location location) {
        super(EntityType.CAT, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}