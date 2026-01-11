package net.Lcing.fanchenwendao.client.animation;


import net.Lcing.fanchenwendao.FanChenWenDao;
import net.neoforged.bus.api.SubscribeEvent;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;

public class FCAnimations {

    //定义动画访问器变量
    public static AnimationAccessor<StaticAnimation>SITDOWN;

    //监听注册事件
    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(FanChenWenDao.MODID, FCAnimations::build);
    }

    //构建动画类型
    private static void build(AnimationManager.AnimationBuilder builder) {

        //打坐动画
        SITDOWN = builder.nextAccessor("biped/living/sitdown",
                (accessor) -> new StaticAnimation(true, accessor, Armatures.BIPED));
    }
}
