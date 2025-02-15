package com.quiptmc.minecraft.paper.utils.entity;

import com.quiptmc.minecraft.paper.utils.CoreUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class QuiptEntityType<T extends QuiptEntity> {

    Class<T> quiptEntityType;

    public QuiptEntityType(Class<T> quiptEntityType) {
        this.quiptEntityType = quiptEntityType;
    }

    public Optional<T> spawn(Class<?>[] constructorClasses, Object... constructorArguments) {
        T entity = null;
        try {
            entity = quiptEntityType.getConstructor(constructorClasses).newInstance(constructorArguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            CoreUtils.integration().logger().error("Failed to spawn entity of type " + quiptEntityType.getSimpleName(), e);
        }
        return Optional.ofNullable(entity);
    }
}
