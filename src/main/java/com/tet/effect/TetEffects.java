package com.tet.effect;

import com.tet.TetMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class TetEffects {
    public static final Holder<MobEffect> SEALED = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            Identifier.fromNamespaceAndPath(TetMod.MOD_ID, "sealed"),
            new SealedEffect()
    );

    public static void initialize() {
        // 触发静态字段初始化
    }
}