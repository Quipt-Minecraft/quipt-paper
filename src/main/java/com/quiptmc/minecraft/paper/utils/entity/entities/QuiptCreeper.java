package com.quiptmc.minecraft.paper.utils.entity.entities;


import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptCreeper extends Creeper implements QuiptEntity<Creeper> {


    public QuiptCreeper(Location location) {
        super(EntityType.CREEPER, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);
    }
}
