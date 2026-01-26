package net.Lcing.fanchenwendao.fashu;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.Serializable;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

//存储法术数据
public class FashuData implements INBTSerializable<CompoundTag> {

    private static final ResourceLocation EMPTY_FASHU = ResourceLocation.parse("fanchenwendao:empty");
    private ResourceLocation currentFaShuId = EMPTY_FASHU;

    public FashuData() {}

    public ResourceLocation getCurrentFaShuId() {
        return currentFaShuId;
    }

    public void setCurrentFaShuId(ResourceLocation currentFaShuId) {
        this.currentFaShuId = currentFaShuId;
    }

    //循环切换法术
    public void cycleNext() {
        //获取全服法术列表
        List<ResourceLocation> allIds = new ArrayList<>(FaShuManager.getAll().keySet());
        //若无法术
        if (allIds.isEmpty()) return;

        //状态修正：玩家当前法术为空 / 存档内法术ID在当前服务器列表找不到
        if (currentFaShuId.equals(EMPTY_FASHU) || !allIds.contains(currentFaShuId)) {
            this.currentFaShuId = allIds.get(0);    //自动设置为第一个
            return;
        }

        //正常循环
        int currentIndex = allIds.indexOf(currentFaShuId);
        int nextIndex = (currentIndex + 1) % allIds.size();
        this.currentFaShuId = allIds.get(nextIndex);
    }


    //数据持久化
    //保存数据，游戏保存/玩家下线时调用
    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {

        CompoundTag tag = new CompoundTag();
        //存入数据，key = "currentFashu", value = 0
        tag.putString("currentFaShuId", currentFaShuId.toString());
        return tag;

    }
    //读取数据，玩家上线/数据同步时调用
    //从NBT读出int，变回Type
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("currentFaShuId")) {
            this.currentFaShuId = ResourceLocation.parse(tag.getString("currentFaShuId"));
        }
    }
}
