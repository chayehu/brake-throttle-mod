package com.tet.mixin;

import com.tet.TetState;
import com.tet.TetStateManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public abstract class WardenTargetMixin {

    // 使用完整方法描述符，避免因参数类型匹配失败而注入无效
    @Inject(method = "canTargetEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void tet$preventWardenTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof ServerPlayer player)) return;

        TetState state = TetStateManager.get(player.getUUID());
        if (state.netForce() > 0) {
            cir.setReturnValue(false);
        }
    }
}