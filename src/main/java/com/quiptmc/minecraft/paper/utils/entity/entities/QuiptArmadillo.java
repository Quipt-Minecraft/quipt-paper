package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptArmadillo extends Armadillo implements QuiptEntity<Armadillo> {
    public QuiptArmadillo(Location location) {
        super(EntityType.ARMADILLO, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}
