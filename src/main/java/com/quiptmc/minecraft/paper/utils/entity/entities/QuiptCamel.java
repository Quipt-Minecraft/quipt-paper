package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.camel.Camel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptCamel extends Camel implements QuiptEntity<Camel> {
    public QuiptCamel(Location location) {
        super(EntityType.CAMEL, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}