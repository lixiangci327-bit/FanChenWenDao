package net.Lcing.fanchenwendao.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.Lcing.fanchenwendao.client.debug.EntityPoseDebugger;
import net.Lcing.fanchenwendao.entity.ThrownSwordEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Random;

public class ThrownSwordRenderer extends EntityRenderer<ThrownSwordEntity> {

    private final ItemRenderer itemRenderer;
    private final float scale;

    public ThrownSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale= 0.8F;
    }

    //重写纹理方法
    @Override
    public ResourceLocation getTextureLocation(ThrownSwordEntity entity) {
        return null; //由ItemRenderer自己找纹理
    }

    @Override
    public void render(ThrownSwordEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        //先push
        poseStack.pushPose();

        //控制旋转
        //Yaw和Pitch由ProjectileUtil.rotateTowardsMovement计算
        float yRot = entity.yRotO + (entity.getYRot() - entity.yRotO) * partialTick;
        float xRot = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTick;
        Random random = new Random(entity.getId());
        float randomRot = random.nextFloat() * 180.0F;

        if (entity.tickCount < 2) {
            yRot = entity.getYRot();
            xRot = entity.getXRot();
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        //使用调试器
        if (EntityPoseDebugger.enabled) {
            EntityPoseDebugger.apply(poseStack);
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.00F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomRot));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45.00F));
        }

        //调用原版物品渲染器 使用ItemDisplayContext.FIXED模式
        this.itemRenderer.renderStatic(
                entity.getSwordStack(),
                ItemDisplayContext.FIXED,//渲染模式
                packedLight,//光照等级
                OverlayTexture.NO_OVERLAY,//叠加层
                poseStack,//姿态栈
                buffer,//缓冲区
                entity.level(),
                entity.getId()//实体ID
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

    }
}
