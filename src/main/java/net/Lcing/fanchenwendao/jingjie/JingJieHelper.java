package net.Lcing.fanchenwendao.jingjie;


import net.Lcing.fanchenwendao.network.packet.SyncJingJiePayload;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
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

        //检查是否满足升级条件 （简单逻辑，后续引入突破概率、必须条件、隐藏彩蛋）
        if (data.getExperience() >= data.getMaxExperience()) {
            levelup(player);
        } else {
            syncToClient(player);//未升级也要同步修为
        }
    }

    //提升境界
    public static int levelup(ServerPlayer player) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        //执行升级逻辑
        data.levelUp();

        //同步
        syncToClient(player);
        //TODO 添加音效、粒子等

        return data.getLevel();
    }

    //负责发包的私有方法
    public static void syncToClient(ServerPlayer player) {
        JingJieData data = player.getData(ModAttachments.JINGJIE_DATA);

        //创建包
        SyncJingJiePayload payload = new SyncJingJiePayload(player.getId(), data.getLevel(), data.getExperience());

        //发送数据包
        PacketDistributor.sendToPlayer(player, payload);
    }
}
