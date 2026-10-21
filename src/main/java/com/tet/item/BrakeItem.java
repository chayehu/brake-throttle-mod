package com.tet.item;

import com.tet.TetState;
import com.tet.TetStateManager;
import com.tet.effect.TetEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrakeItem extends Item {

    public static final int MAX_USE_TICKS = 40;
    public static final int MAX_N = 1600;
    public static final int HOLD_DURATION = 72000;
    public static final int AUTO_STOP_TICKS = 1200;   // 1 分钟自动熄火
    public static final int SEALED_MESSAGE_TICKS = 60; // 封口提示独占 3 秒

    public BrakeItem(Properties properties) {
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
            player.displayClientMessage(Component.literal("刹车蓄力: " + currentN + "N"), true);
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
            state.brakeN += finalN;

            player.displayClientMessage(
                    Component.literal("刹车: +" + finalN + "N  总刹车: " + state.brakeN + "N  净推力: " + state.netForce() + "N"),
                    true
            );

            if (usedTicks >= MAX_USE_TICKS) {
                state.brakeN = 0;
                state.autoStopTicks = AUTO_STOP_TICKS;
                // 封口提示先独占 action bar 3 秒，之后才轮到倒计时
                state.sealedMessageTicks = SEALED_MESSAGE_TICKS;

                player.setItemInHand(player.getUsedItemHand(), new ItemStack(TetItems.BRAKE_BROKEN));
                player.addEffect(new MobEffectInstance(TetEffects.SEALED, AUTO_STOP_TICKS, 0, false, true, true));
                // 这里不再 displayClientMessage，交给 TetTickHandler 统一显示
            }
        }
        return true;
    }
}