package net.Lcing.fanchenwendao.jingjie;


import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.gongfa.GongFaLevel;
import net.Lcing.fanchenwendao.lingqisystem.LingQiChunkData;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


@EventBusSubscriber(modid = FanChenWenDao.MODID)
public class XiuLianTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        //只在服务端运行
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        //频率控制——每秒执行一次
        if (player.tickCount % 20 != 0) {
            return;
        }
        //检查是否在修炼：若没修炼直接直接结束(默认为false)
        if (!JingJieHelper.isXiulian(player)) {
            return;
        }
        //安全检测——必须有禁锢.TODO:飞行检测。史诗战斗强制移动玩家——还是得强行禁止玩家任何移动
        MobEffectInstance slowEffect = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
        if (slowEffect == null || slowEffect.getAmplifier() != 255) {
            JingJieHelper.setXiulian(player, false);
            return;
        }


        //获取环境数据
        ServerLevel level = (ServerLevel) player.level();
        LevelChunk chunk = level.getChunkAt(player.blockPosition());
        LingQiChunkData chunkData = chunk.getData(ModAttachments.LINGQI_CHUNK_DATA);

        //确定当前功法.TODO:现在默认使用黄阶，之后使用get方法获取
        GongFaLevel currentGongFa = GongFaLevel.HUANG;

        //计算理论吸收速度
        float gongfaSpeed = currentGongFa.getBaserate();    //功法基础速度
        int playerLevel = JingJieHelper.getLevel(player);
        float bodyLimit = (playerLevel >= 16) ? 25.0f : 5.0f;

        //取实际量（不能超过身体极限）
        float demand = Math.min(gongfaSpeed, bodyLimit);

        //扣除灵气
        float consume = chunkData.consumeLingQi(demand);

        //修为转化逻辑
        if (consume > 0.0f) {
            //成功吸收
            float gainedExp = consume * currentGongFa.getEfficiency();
            JingJieHelper.addExperience(player, gainedExp);

            //标记区块数据脏了
            chunk.setUnsaved(true);

            //HUD
            String msg = String.format("§a修炼中... 灵气 -%.1f / 修为 +%.1f", consume, gainedExp);
            player.displayClientMessage(Component.literal(msg), true);
        } else {
            //吸收失败（环境灵气不足）
            player.displayClientMessage(Component.literal("§c周围灵气稀薄，无法满足功法运转..."), true);
        }
    }
}
