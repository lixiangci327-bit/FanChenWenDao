package com.p1nero.wukong.mixin.animation_types_mixin;

import com.p1nero.wukong.client.particle.WuKongEffect;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class, remap = false)
public abstract class AnimationPlayerMixin {
    @Shadow
    protected float elapsedTime;
    @Shadow
    protected float prevElapsedTime;
    @Shadow
    protected boolean reversed;
    @Shadow
    public DynamicAnimation getAnimation() {
        return null;
    }
    @Shadow
    protected boolean isEnd;
    @Shadow
    protected DynamicAnimation play;
    @Shadow
    public boolean isReversed() {
        return this.reversed;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
//
//        if (entitypatch.getOriginal().hasEffect(WuKongEffect.DING.get())) {
//            // ci.cancel();
//        }
//

    }
}
