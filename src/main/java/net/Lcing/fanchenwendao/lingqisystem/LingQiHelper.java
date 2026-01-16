package net.Lcing.fanchenwendao.lingqisystem;


import net.Lcing.fanchenwendao.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

//灵气辅助类
public class LingQiHelper {

    //灵气噪声缩放系数
    public static final double BASE_SCALE = 0.01;
    public static final double RARE_SCALE = 0.05;

    //根据群系计算基础灵气值
    public static float getBaseLingQi(Holder<Biome> biome) {

        //1.高山/深海——300.0
        if (biome.is(Biomes.JAGGED_PEAKS) || biome.is(Biomes.DEEP_OCEAN)) {
            return 300.0f;
        }

        //2.蘑菇岛/原始松木林——600.0
        if (biome.is(Biomes.MUSHROOM_FIELDS) || biome.is(Biomes.OLD_GROWTH_PINE_TAIGA)) {
            return 600.0f;
        }

        //3.荒地/沙漠/地狱——20.0
        if (biome.is(Biomes.DESERT) || biome.is(Biomes.BADLANDS) || biome.is(Biomes.NETHER_WASTES)) {
            return 20.0f;
        }

        //其余默认
        return 100.0f;  //TODO；对群系base进行更细分
    }




    //计算上限maxLingQi：群系基础 * 噪声系数
    public static float calculatemaxLingQi(Level level, BlockPos pos) {
        //获取当前群系
        Holder<Biome> biome = level.getBiome(pos);
        float baseLingQi = getBaseLingQi(biome);

        //使用柏林噪声
        long seed = 0;
        if (level instanceof ServerLevel serverLevel) {
            seed = serverLevel.getSeed();
        } else {
            seed = level.dimension().location().hashCode();
        }

        //基础底噪
        double basenoise = SimplePerlinNoise.noise(pos.getX() * BASE_SCALE, pos.getZ() * BASE_SCALE, seed);
        double basefactor = 1.0 + (basenoise * 0.2);    //[0.8-1.2]

        //机缘噪点
        double rarenoise = SimplePerlinNoise.noise(pos.getX() * RARE_SCALE, pos.getZ() * RARE_SCALE, seed + 12345);

        //锐化：稀少区域出现机缘噪点
        double rarefactor = 0.0;
        if (rarenoise > 0.6) {
            rarefactor = (rarenoise - 0.6) * 10.0;   //灵气增加0-4倍
        }

        //最终分布
        double finalfactor = basefactor + rarefactor;

        //噪声修正
        return (float) (baseLingQi * finalfactor);
    }



    //3*3chunk 吸收灵气
    public static float baseAbsorb(ServerPlayer player, float totalNeed) {
        Level level = player.level();
        ChunkPos center = player.chunkPosition();

        //获取总量
        float totalamount = 0.0f;
        List<LevelChunk> validChunks = new ArrayList<>();   //使用LevelChunk，后面才能使用setUnsaved来标记区块

        //遍历周围chunk
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                //必须检查chunk是否加载，不能强制加载
                if (level.hasChunk(center.x + x, center.z + z)) {
                    LevelChunk chunk = level.getChunk(center.x + x, center.z + z);
                    LingQiChunkData data = chunk.getData(ModAttachments.LINGQI_CHUNK_DATA);

                    //记录这个加载出来的有效区块
                    validChunks.add(chunk);
                    //加入总量
                    totalamount += data.getCurrentLingQi();
                }
            }
        }

        //消耗逻辑
        if (totalamount <= 0 || totalamount < totalNeed) return 0.0f;

        //总库存充足，计算比例
        float ratio = Math.min(1.0f, totalNeed / totalamount);

        //扣除灵气
        float actualAbsorb = 0.0f;
        for (LevelChunk chunk : validChunks) {

            LingQiChunkData data = chunk.getData(ModAttachments.LINGQI_CHUNK_DATA);
            //遍历范围内所有有效区块
            float consume = data.getCurrentLingQi() * ratio;
            //扣除
            float result = data.consumeLingQi(consume);

            //如果扣除了灵气，则标记区块
            if (result > 0.0f) {
                actualAbsorb += result;
                chunk.setUnsaved(true);
            }
        }

        return actualAbsorb;
    }

}
