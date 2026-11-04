package de.example.autoafk.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AfkScreen extends Screen {
	private Button infoButton;

	public AfkScreen() {
		super(Component.literal("Auto AFK"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int y = this.height / 2 - 40;

		infoButton = addRenderableWidget(Button.builder(infoText(), b -> {})
				.bounds(cx - 120, y - 30, 240, 20).build());
		infoButton.active = false;

		addRenderableWidget(Button.builder(timerText(), b -> {
			AutoAfkClient.running = !AutoAfkClient.running;
			b.setMessage(timerText());
		}).bounds(cx - 120, y, 240, 20).build());

		addRenderableWidget(Button.builder(autoStartText(), b -> {
			AfkConfig.INSTANCE.autoStartOnJoin = !AfkConfig.INSTANCE.autoStartOnJoin;
			AfkConfig.save();
			b.setMessage(autoStartText());
		}).bounds(cx - 120, y + 26, 240, 20).build());

		addRenderableWidget(Button.builder(Component.literal("Schließen"), b -> onClose())
				.bounds(cx - 120, y + 62, 240, 20).build());
	}

	@Override
	public void tick() {
		super.tick();
		if (infoButton != null) {
			infoButton.setMessage(infoText());
		}
	}

	private Component infoText() {
		int secondsLeft = Math.max(0, (AutoAfkClient.intervalTicks() - AutoAfkClient.ticks) / 20);
		return Component.literal(String.format("Nächstes /afk in %d:%02d", secondsLeft / 60, secondsLeft % 60));
	}

	private Component timerText() {
		return Component.literal("Timer: " + (AutoAfkClient.running ? "LÄUFT (klicken = pausieren)" : "PAUSIERT (klicken = starten)"));
	}

	private Component autoStartText() {
		return Component.literal("Beim Rejoin automatisch starten: " + (AfkConfig.INSTANCE.autoStartOnJoin ? "AN" : "AUS"));
	}
}
