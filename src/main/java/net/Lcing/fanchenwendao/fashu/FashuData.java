package net.Lcing.fanchenwendao.fashu;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.Serializable;
import java.security.Provider;

//存储法术数据
public class FashuData implements INBTSerializable<CompoundTag> {

    //存储具体的枚举对象
    private FashuType currentFashu = FashuType.FIREBALL_SHOOT;


    //定义Codec
    public static final Codec<FashuData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            //定义要保存的字段
            Codec.INT.fieldOf("CurrentFashu").forGetter(d -> d.currentFashu.getId())
    ).apply(instance, (id) -> {
        //定义如何从读取到的数据（id）还原出FashuData对象
        FashuData data = new FashuData();
        data.setCurrentFashu(FashuType.getById(id));
        return data;
    }));


    //无参的构造函数，实例化注册器
    public FashuData() {
    }

    public FashuType getCurrentFashu() {
        return currentFashu;
    }

    public void setCurrentFashu(FashuType Fashu) {
        this.currentFashu = Fashu;
    }


    //循环法术切换
    public void cycleNext() {
        int nextid = (currentFashu.getId() + 1) % FashuType.values().length;
        this.currentFashu = FashuType.getById(nextid);
    }



    //数据持久化
    //将FashuType转换成int存进NBT
    //保存数据，游戏保存/玩家下线时调用
    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {

        CompoundTag tag = new CompoundTag();
        //存入数据，key = "CurrentFashu", value = 0
        tag.putInt("CurrentFashu", currentFashu.getId());
        return tag;

    }
    //读取数据，玩家上线/数据同步时调用
    //从NBT读出int，变回Type
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("CurrentFashu")) {
            this.currentFashu = FashuType.getById(tag.getInt("CurrentFashu"));
        }
    }
}
