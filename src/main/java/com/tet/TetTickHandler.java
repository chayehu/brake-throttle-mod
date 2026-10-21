package com.tet;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TetTickHandler {

    private static final double RAM_SPEED_THRESHOLD = 0.5;
    private static final float RAM_DAMAGE_RATIO = 0.75f;
    private static final int STOP_THRESHOLD = 200;
    private static final double AGGRO_CLEAR_RADIUS = 32.0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                TetState state = TetStateManager.get(player.getUUID());

                // --- 0. 自动停车倒计时 ---
                if (state.autoStopTicks > 0) {
                    state.autoStopTicks--;

                    if (state.autoStopTicks <= 0) {
                        // 倒计时结束：清空状态 + 强制停车
                        TetStateManager.remove(player.getUUID());

                        Vec3 v = player.getDeltaMovement();
                        player.setDeltaMovement(0, v.y, 0);
                        player.hurtMarked = true;

                        var sh = player.getAttribute(Attributes.STEP_HEIGHT);
                        if (sh != null) sh.setBaseValue(0.6);

                        player.displayClientMessage(Component.literal("车辆已自动熄火。"), true);
                        continue;
                    }

                    // 封口提示优先显示 3 秒，独占 action bar，防止与倒计时重叠
                    if (state.sealedMessageTicks > 0) {
                        state.sealedMessageTicks--;
                        player.displayClientMessage(
                                Component.literal("刹车已断裂！1 分钟后自动熄火，期间被“封口”。"),
                                true
                        );
                    } else {
                        // 封口提示结束后，再开始显示倒计时
                        int remainSeconds = (state.autoStopTicks + 19) / 20; // 向上取整
                        player.displayClientMessage(
                                Component.literal("自动刹车倒计时: " + remainSeconds + " 秒"),
                                true
                        );
                    }
                }

                // --- 1. 净推力 <= 200N：清除状态 + 停车（仅在没有断裂倒计时时）---
                if (state.autoStopTicks == 0 && state.throttleN > 0 && state.netForce() <= STOP_THRESHOLD) {
                    TetStateManager.remove(player.getUUID());

                    Vec3 v = player.getDeltaMovement();
                    player.setDeltaMovement(0, v.y, 0);
                    player.hurtMarked = true;

                    var sh = player.getAttribute(Attributes.STEP_HEIGHT);
                    if (sh != null) sh.setBaseValue(0.6);
                    continue;
                }

                int net = state.netForce();

                // --- 2. 无净推力：完全交还原版 ---
                if (net <= 0) {
                    var sh = player.getAttribute(Attributes.STEP_HEIGHT);
                    if (sh != null) sh.setBaseValue(0.6);
                    continue;
                }

                // --- 3. 有推力：提高跨步高度 + 不消耗饱食度 ---
                var stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
                if (stepHeight != null) {
                    stepHeight.setBaseValue(1.0);
                }

                player.getFoodData().addExhaustion(-40.0f);

                // --- 4. 施加定制速度 ---
                Vec3 vel = player.getDeltaMovement();
                double speedPerTick = Math.min(net / 2000.0, 1.0);

                Vec3 look = player.getViewVector(1.0F);
                Vec3 horiz = new Vec3(look.x, 0, look.z);
                if (horiz.lengthSqr() < 0.0001) {
                    horiz = new Vec3(0, 0, 1);
                } else {
                    horiz = horiz.normalize();
                }

                player.setDeltaMovement(horiz.x * speedPerTick, vel.y, horiz.z * speedPerTick);
                player.hurtMarked = true;

                // --- 5. 高速撞击伤害 ---
                if (speedPerTick >= RAM_SPEED_THRESHOLD) {
                    AABB box = player.getBoundingBox().move(horiz.scale(1.5)).inflate(0.5);
                    List<Entity> entities = player.level().getEntities(player, box, e ->
                            e instanceof LivingEntity && e != player && e.isAlive()
                    );

                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity living) {
                            float damage = living.getMaxHealth() * RAM_DAMAGE_RATIO;
                            if (player.level() instanceof ServerLevel serverLevel) {
                                living.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
                            }
                        }
                    }
                }

                // --- 6. 清除周围生物仇恨 ---
                AABB aggroBox = player.getBoundingBox().inflate(AGGRO_CLEAR_RADIUS);
                List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                        LivingEntity.class, aggroBox,
                        e -> e != player && e.isAlive()
                );

                for (LivingEntity entity : nearby) {
                    if (entity instanceof Mob mob) {
                        mob.setTarget(null);
                    }
                    if (entity instanceof WitherBoss wither) {
                        wither.setTarget(null);
                        wither.setAlternativeTarget(1, 0);
                        wither.setAlternativeTarget(2, 0);
                    }
                    if (entity instanceof NeutralMob neutral) {
                        neutral.stopBeingAngry();
                    }
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                TetStateManager.remove(handler.getPlayer().getUUID())
        );
    }
}