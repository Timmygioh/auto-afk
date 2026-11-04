package de.example.autoafk.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AfkScreen extends Screen {
	public AfkScreen() {
		super(Component.literal("Auto AFK"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int y = this.height / 2 - 40;

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

	private Component timerText() {
		return Component.literal("Timer: " + (AutoAfkClient.running ? "LÄUFT (klicken = pausieren)" : "PAUSIERT (klicken = starten)"));
	}

	private Component autoStartText() {
		return Component.literal("Beim Rejoin automatisch starten: " + (AfkConfig.INSTANCE.autoStartOnJoin ? "AN" : "AUS"));
	}

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
		super.render(g, mouseX, mouseY, delta);
		int secondsLeft = Math.max(0, (AutoAfkClient.intervalTicks() - AutoAfkClient.ticks) / 20);
		String info = String.format("Nächstes /afk in %d:%02d", secondsLeft / 60, secondsLeft % 60);
		g.drawCenteredString(this.font, info, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
	}
}
