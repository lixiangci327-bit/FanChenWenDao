package net.Lcing.fanchenwendao.fashu.logic;


import net.Lcing.fanchenwendao.client.fx.VisualConfig;
import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.Lcing.fanchenwendao.network.packet.SpawnInstantFXPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class InstantSpellLogic implements IFaShuLogic{

    @Override
    public void cast(LivingEntity caster, Level level, FaShuDefine define) {
        //服务端处理
        if (level.isClientSide()) return;

        //获取施法参数
        float range = define.getParamFloat("range", 5.0f);
        float damage = define.getDamage();

        //这里简单使用施法者的视线向量
        Vec3 look = caster.getLookAngle();
        Vec3 origin = caster.getEyePosition();
        Vec3 targetPos = origin.add(look.x * range, look.y * range, look.z * range);

        //System.out.println("DEBUG: InstantSpell target=" + targetPos + ", caster=" + caster.position());


        //TODO:添加AOE伤害，效果

        //视觉效果处理
        //hit阶段
        VisualConfig hitVisual = define.getVisual("hit");

        if (hitVisual.fxID().isEmpty()) {
            //System.err.println("DEBUG: 'hit' visual config NOT FOUND or FX ID empty for spell: " + define.getId());
        }


        //若配置了fxID
        hitVisual.fxID().ifPresent(fxId -> {
            //System.out.println("DEBUG: Sending instant FX packet: " + fxId);

            SpawnInstantFXPayload payload = new SpawnInstantFXPayload(
                    targetPos.x, targetPos.y, targetPos.z,
                    fxId
            );

            //发送给周围玩家
            if (caster instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, payload);
            } else {
                //怪物施法
                PacketDistributor.sendToPlayersTrackingEntity(caster, payload);
            }
        });
    }


}
