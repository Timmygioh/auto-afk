package de.example.autoafk.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class AutoAfkClient implements ClientModInitializer {
	public static boolean running = false;
	public static int ticks = 0;
	private static int joinDelay = -1;

	private static KeyMapping menuKey;

	public static int intervalTicks() {
		return AfkConfig.INSTANCE.intervalMinutes * 60 * 20;
	}

	@Override
	public void onInitializeClient() {
		AfkConfig.load();

		menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.autoafk.menu",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				KeyMapping.Category.MISC
		));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			ticks = 0;
			if (AfkConfig.INSTANCE.autoStartOnJoin) {
				joinDelay = 100;
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			running = false;
			ticks = 0;
			joinDelay = -1;
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				client.gui.setScreen(new AfkScreen());
			}

			if (client.player == null || client.getConnection() == null) {
				return;
			}

			if (joinDelay > 0 && --joinDelay == 0) {
				joinDelay = -1;
				running = true;
				ticks = 0;
				client.getConnection().sendCommand("afk");
			}

			if (running && ++ticks >= intervalTicks()) {
				ticks = 0;
				client.getConnection().sendCommand("afk");
			}
		});
	}
}
