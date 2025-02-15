package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptCow extends Cow implements QuiptEntity<Cow> {
    public QuiptCow(Location location) {
        super(EntityType.COW, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}