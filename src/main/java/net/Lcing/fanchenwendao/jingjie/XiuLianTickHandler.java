package net.Lcing.fanchenwendao.jingjie;


import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.client.animation.FCAnimations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(modid = FanChenWenDao.MODID)
public class XiuLianTickHandler {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        //只在服务端运行
        if (event.getEntity() instanceof ServerPlayer player) {

            //每秒执行一次
            if (player.tickCount % 20 == 0) {
                int currentlevel = JingJieHelper.getLevel(player);

                ServerPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
                if (patch == null) return;

                //检查动作
                Animator animator = patch.getAnimator();
                var currentAnim = animator.getPlayerFor(null).getAnimation();

                if (currentAnim != null) {

                    //null检查
                    if (currentAnim.registryName() == null) return;

                    if (FCAnimations.SITDOWN.registryName().equals(currentAnim.registryName())) {

                        //debug:后面将修炼状态检测从动画 -> 灵气或其他状态，动画并不稳定

                        //凡人突破
                        if (currentlevel == 0) {
                            JingJieHelper.addExperience(player, 5);
                        } else {
                            if (currentlevel < 15) {
                                JingJieHelper.addExperience(player, 5);
                                player.sendSystemMessage(Component.literal("§a修为 +5..."), true);
                            }
                        }
                    }
                } else {
                    player.sendSystemMessage(Component.literal("无动画"), false);
                }
            }
        }
    }
}
