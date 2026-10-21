package com.tet.component;

import com.mojang.serialization.Codec;
import com.tet.TetMod;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class TetComponents {

    public static final DataComponentType<Integer> BRAKE_FORCE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(TetMod.MOD_ID, "brake_force"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    public static void initialize() {
        // 触发静态字段初始化
    }
}