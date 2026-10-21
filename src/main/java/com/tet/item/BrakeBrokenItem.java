package com.tet.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BrakeBrokenItem extends Item {

    public BrakeBrokenItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        // 断裂刹车不能使用，直接返回 PASS
        return InteractionResult.PASS;
    }
}