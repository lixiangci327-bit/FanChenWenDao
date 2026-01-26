package net.Lcing.fanchenwendao.fashu.logic;

import net.Lcing.fanchenwendao.fashu.FaShuDefine;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IFaShuLogic {
    void cast(LivingEntity caster, Level level, FaShuDefine define);
}
