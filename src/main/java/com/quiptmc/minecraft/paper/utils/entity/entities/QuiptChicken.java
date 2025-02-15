package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptChicken extends Chicken implements QuiptEntity<Chicken> {
    public QuiptChicken(Location location) {
        super(EntityType.CHICKEN, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}