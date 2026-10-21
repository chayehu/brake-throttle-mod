package com.tet.client;

import com.tet.effect.TetEffects;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class TetModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 客户端拦截聊天发送
		ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
			// 如果玩家拥有“封口”效果，返回 false 阻止消息发送[reference:2]
			if (net.minecraft.client.Minecraft.getInstance().player != null &&
					net.minecraft.client.Minecraft.getInstance().player.hasEffect(TetEffects.SEALED)) {
				// 可选：在聊天栏提示玩家
				net.minecraft.client.Minecraft.getInstance().player.displayClientMessage(
						net.minecraft.network.chat.Component.literal("你的嘴巴被“封口”了，无法说话。"),
						true
				);
				return false;
			}
			return true;
		});
	}
}