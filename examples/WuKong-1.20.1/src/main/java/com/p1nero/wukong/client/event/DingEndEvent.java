package com.p1nero.wukong.client.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DingEndEvent {
    public static void execute(Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.GLOW, entity.getX(), entity.getY() + 1, entity.getX(), 50, 0, 0, 0, 10);
        if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getX(), 50, 0, 0, 0, 10);
        entity.aiStep();
        entity.tick();
    }
}
