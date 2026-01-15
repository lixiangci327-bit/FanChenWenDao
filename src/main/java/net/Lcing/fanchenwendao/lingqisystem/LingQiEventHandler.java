package net.Lcing.fanchenwendao.lingqisystem;




import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

//灵气系统事件处理器
//灵气的加载，回复更新，玩家交互

@EventBusSubscriber(modid = FanChenWenDao.MODID)
public class LingQiEventHandler {

    //监听区块加载事件：任何区块被加载后都触发
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        //服务端处理灵气逻辑，客户端不参与
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        //获取当前加载的区块
        if (!(event.getChunk() instanceof LevelChunk chunk)) {
            return; //LevelChunk才是完整的游戏区块
        }

        //从区块上获得lingqichunkdata
        LingQiChunkData data = chunk.getData(ModAttachments.LINGQI_CHUNK_DATA);

        //检查初始化
        if (data.getMaxLingQi() == 0) {
            //获取区块中心坐标
            BlockPos centerPos = chunk.getPos().getWorldPosition().offset(8, 64, 8);

            //调用辅助类计算max
            float maxLingQi = LingQiHelper.calculatemaxLingQi(level, centerPos);

            //填入数据
            data.setMaxLingQi(maxLingQi);
            data.setCurrentLingQi(maxLingQi);   //初始化后填满

            //标记区块：数据变了，游戏存盘时需要把该区块保存下来
            chunk.setUnsaved(true);

            //DEBUG; TODO:删除日志
            FanChenWenDao.LOGGER.info("初始化灵气：Chunk [{}, {}] -> max:{}", chunk.getPos().x, chunk.getPos().z, maxLingQi);
        }


    }
}
