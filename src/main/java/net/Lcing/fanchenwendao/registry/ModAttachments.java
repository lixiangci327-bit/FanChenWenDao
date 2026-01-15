package net.Lcing.fanchenwendao.registry;

import net.Lcing.fanchenwendao.FanChenWenDao;
import net.Lcing.fanchenwendao.fashu.FashuData;
import net.Lcing.fanchenwendao.jingjie.JingJieData;
import net.Lcing.fanchenwendao.lingqisystem.LingQiChunkData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

//数据附件
public class ModAttachments {
    // 创建 DeferredRegister，绑定到 NeoForgeRegistries.ATTACHMENT_TYPES
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, FanChenWenDao.MODID);



    //法术数据
    public static final Supplier<AttachmentType<FashuData>> FASHU_DATA = ATTACHMENT_TYPES.register(
            "fashu_data",
            () -> AttachmentType.<FashuData>builder(() -> new FashuData())
                    .serialize(FashuData.CODEC)
                    .copyOnDeath()
                    .build()
    );

    //境界数据
    public static final Supplier<AttachmentType<JingJieData>> JINGJIE_DATA = ATTACHMENT_TYPES.register(
            "jingjie_data",
            () -> AttachmentType.<JingJieData>builder(() -> new JingJieData())
                    .serialize(JingJieData.CODEC)//告诉游戏怎么保存数据Codec
                    .copyOnDeath()//死亡后复制
                    .build()
    );

    //灵气区块数据
    public static final Supplier<AttachmentType<LingQiChunkData>> LINGQI_CHUNK_DATA = ATTACHMENT_TYPES.register(
            "lingqi_chunk_data",
            () -> AttachmentType.<LingQiChunkData>builder(() -> new LingQiChunkData())
                    .serialize(LingQiChunkData.CODEC)
                    .build()
    );

    // 注册方法，在主类调用
    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
