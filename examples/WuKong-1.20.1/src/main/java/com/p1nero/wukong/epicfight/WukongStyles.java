package com.p1nero.wukong.epicfight;

import yesman.epicfight.world.capabilities.item.Style;

public enum WukongStyles implements Style {
    SMASH(false),
    THRUST(false),
    PILLAR(false),
    GREATSAGE(false);

    private final boolean canUseOffhand;
    private final int id;

    WukongStyles(boolean canUseOffhand) {
        this.id = ordinal();  // Or use ENUM_MANAGER if necessary
        this.canUseOffhand = canUseOffhand;
    }

    @Override
    public int universalOrdinal() {
        return this.id;
    }

    @Override
    public boolean canUseOffhand() {
        return this.canUseOffhand;
    }
}

