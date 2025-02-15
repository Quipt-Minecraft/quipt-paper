package com.quiptmc.minecraft.paper.utils.entity;


import net.minecraft.world.entity.Entity;
import org.bukkit.Location;

public interface QuiptEntity<T extends Entity> {

    default void spawn(Location location, Entity entity) {
        try {
            entity.level().addFreshEntity(entity);
            entity.setPos(location.x(), location.y(), location.z());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}