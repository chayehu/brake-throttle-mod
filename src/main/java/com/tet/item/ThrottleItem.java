package com.tet.item;

import com.tet.TetState;
import com.tet.TetStateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrottleItem extends Item {

    public static final int MAX_USE_TICKS = 60;    // 3 秒蓄满
    public static final int MAX_N = 2000;
    public static final int HOLD_DURATION = 72000;

    public ThrottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return HOLD_DURATION;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) return;

        int usedTicks = HOLD_DURATION - remainingUseDuration;
        int currentN = usedTicks >= MAX_USE_TICKS
                ? MAX_N
                : (int) ((usedTicks / (double) MAX_USE_TICKS) * MAX_N);

        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal("油门蓄力: " + currentN + "N"), true);
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) return false;

        int usedTicks = HOLD_DURATION - remainingUseDuration;
        int finalN = usedTicks >= MAX_USE_TICKS
                ? MAX_N
                : (int) ((usedTicks / (double) MAX_USE_TICKS) * MAX_N);

        if (!level.isClientSide()) {
            TetState state = TetStateManager.get(player.getUUID());
            // 累加（和刹车一致），封顶 2000
            state.throttleN = Math.min(state.throttleN + finalN, MAX_N);

            // 饱和效果：时长按 N 值比例 0~200 tick（0~10 秒），等级 0（饱和 I）
            if (finalN > 0) {
                int duration = (int) ((finalN / (double) MAX_N) * 200);
                if (duration > 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, 0));
                }
            }

            player.displayClientMessage(
                    Component.literal("油门: +" + finalN + "N  总油门: " + state.throttleN + "N  净推力: " + state.netForce() + "N"),
                    true
            );
        }
        return true;
    }
}