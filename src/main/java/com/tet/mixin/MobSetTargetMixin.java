package com.tet.mixin;

import com.tet.TetState;
import com.tet.TetStateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobSetTargetMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void tet$preventTargetWhileThrottling(LivingEntity target, CallbackInfo ci) {
        if (!(target instanceof ServerPlayer player)) return;

        TetState state = TetStateManager.get(player.getUUID());
        if (state.netForce() > 0) {
            // 油门激活中：直接取消，不让生物把玩家设为目标
            ci.cancel();
        }
    }
}