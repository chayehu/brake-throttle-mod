package com.tet.item;

import com.tet.TetMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class TetItems {

    public static final Item THROTTLE = register("throttle", ThrottleItem::new);
    public static final Item BRAKE = register("brake", BrakeItem::new);
    public static final Item BRAKE_BROKEN = register("brake_broken", BrakeBrokenItem::new);

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM,
                Identifier.fromNamespaceAndPath(TetMod.MOD_ID, name)
        );
        Item.Properties properties = new Item.Properties().stacksTo(1).setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
    }

    public static void initialize() {
        ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(TetMod.MOD_ID, "tet_items")
        );

        CreativeModeTab tab = FabricItemGroup.builder()
                .icon(() -> new ItemStack(THROTTLE))
                .title(Component.translatable("itemGroup.tet"))
                .displayItems((params, output) -> {
                    output.accept(THROTTLE);
                    output.accept(BRAKE);
                    output.accept(BRAKE_BROKEN);
                })
                .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, tab);
    }
}