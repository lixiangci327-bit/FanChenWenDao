package net.Lcing.fanchenwendao.jingjie;


import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

//境界系统的核心辅助类
//境界的修改、查询、同步操作都通过该类进行。不是操作Data对象
public class JingJieHelper {

    //获取当前玩家境界
    public static int getLevel(ServerPlayer player) {
        return player.getData(ModAttachments.JINGJIE_DATA).getLevel();
    }

    //获取修为
    public static float getExperience(ServerPlayer player) {
        return player.getData(ModAttachments.JINGJIE_DATA).getExperience();
    }

    //获取灵力
    public static float getLingli(ServerPlayer player) {
        return player.getData(ModAttachments.JINGJIE_DATA).getLingli();
    }

    //是否在修炼
    public static boolean isXiulian(ServerPlayer player) {
        return player.getData(ModAttachments.JINGJIE_DATA).isXiulian();
    }

    //获取当前功法
    public static ResourceLocation getMainGongFaID(ServerPlayer player) {
        return player.getData(ModAttachments.JINGJIE_DATA).getMainGongFaID();
    }





    //境界名称本地化
    public static Component getJingJieName(int level) {
        String key = "fanchenwendao.jingjie." + level;
        return Component.translatable(key);
    }

    //设置玩家的境界，并且自动同步
    public static void setLevel(ServerPlayer player, int level) {
        //获取数据附件
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        //修改数据
        data.setLevel(level);

        //执行同步
        syncToClient(player);
    }

    //增加经验值并检查是否升级
    public static void addExperience(ServerPlayer player, float amount) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
        data.addExperience(amount);

        //检查是否满足升级条件 （简单逻辑，TODO:后续引入突破概率、必须条件、隐藏彩蛋）
        if (data.getExperience() >= getMaxExperience(player)) {
            Levelup(player);
        } else {
            syncToClient(player);//未升级也要同步修为
        }
    }

    //计算升级所需的修为
    public static float getMaxExperience(Player player) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
        int level = data.getLevel();
        if (level == 0) return 100.0f;
        return (float) (100.0 * Math.pow(1.5, level));
    }

    //提升境界
    public static int Levelup(ServerPlayer player) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        //执行升级逻辑
        data.levelUp();

        Component jingjiename = getJingJieName(data.getLevel());
        player.sendSystemMessage(Component.translatable("fanchenwendao.message.levelup", jingjiename));

        //同步
        syncToClient(player);
        //TODO 添加音效、粒子等

        return data.getLevel();
    }

    //设置灵力并同步
    public static void setLingli(ServerPlayer player, float amount) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
        data.setLingli(amount);
        syncToClient(player);
    }

    //设置修炼状态
    public static void setXiulian(ServerPlayer player, boolean xiulian) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);
        //如果状态没变则不发包
        if (data.isXiulian() != xiulian) {
            data.setXiulian(xiulian);
            syncToClient(player);
        }
    }





    //负责发包的私有方法
    public static void syncToClient(ServerPlayer player) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        //创建包
        SyncJingJiePayload payload = new SyncJingJiePayload(
                player.getId(),
                data.serializeNBT(player.registryAccess())  //把所有属性打包为一个CompoundTag
        );

        //发送数据包
        PacketDistributor.sendToPlayer(player, payload);
    }
}
