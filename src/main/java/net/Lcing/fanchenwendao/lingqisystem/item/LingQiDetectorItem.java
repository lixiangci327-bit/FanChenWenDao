package net.Lcing.fanchenwendao.lingqisystem.item;

import net.Lcing.fanchenwendao.lingqisystem.LingQiChunkData;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

//灵气探测器
public class LingQiDetectorItem extends Item {

    public LingQiDetectorItem() {
        //设置物品属性，最大堆叠1
        super(new Item.Properties().stacksTo(1));
    }


    //右键使用
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        //只在服务端处理
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            //获取玩家pos的区块
            LevelChunk chunk = serverLevel.getChunkAt(player.blockPosition());

            //读取灵气数据
            LingQiChunkData data = chunk.getData(ModAttachments.LINGQI_CHUNK_DATA);

            //组装信息
            String msg = String.format("§b[灵气探测]§r 当前: §a%.1f§r / 上限: §6%.1f",
                    data.getCurrentLingQi(), data.getMaxLingQi());

            //发送给玩家
            player.displayClientMessage(Component.literal(msg), true);
        }

        //返回SUCCESS表示成功
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

}
