package com.quiptmc.minecraft.paper.utils.entity.entities;

import com.quiptmc.minecraft.paper.utils.entity.QuiptEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class QuiptAxolotl extends Axolotl implements QuiptEntity<Axolotl> {
    public QuiptAxolotl(Location location) {
        super(EntityType.AXOLOTL, ((CraftWorld) location.getWorld()).getHandle());
        spawn(location,this);

    }
}
