package com.p1nero.wukong.epicfight.skill;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;

public class EntitySpeedData {
    private static final Map<LivingEntity, Double> entitySpeedMap = new HashMap<>();

    public static double getOriginalSpeed(LivingEntity entity) {
        return entitySpeedMap.getOrDefault(entity, entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
    }

    public static void saveOriginalSpeed(LivingEntity entity) {
        if (!entitySpeedMap.containsKey(entity)) {
            double originalSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            entitySpeedMap.put(entity, originalSpeed);
        }
    }

    public static void restoreOriginalSpeed(LivingEntity entity) {
        Double originalSpeed = entitySpeedMap.get(entity);
        if (originalSpeed != null) {
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(originalSpeed);
            entitySpeedMap.remove(entity);
        }
    }
}
