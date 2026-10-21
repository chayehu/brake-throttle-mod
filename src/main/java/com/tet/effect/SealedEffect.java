package com.tet.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SealedEffect extends MobEffect {
    public SealedEffect() {
        // 效果分类：有害。颜色：暗红色 (0x8B0000)
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }
}