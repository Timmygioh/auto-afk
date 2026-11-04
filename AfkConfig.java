package de.example.autoafk.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class AfkConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("autoafk.json");

	public static AfkConfig INSTANCE = new AfkConfig();

	public boolean autoStartOnJoin = true;
	public int intervalMinutes = 10;

	public static void load() {
		try {
			if (Files.exists(FILE)) {
				AfkConfig loaded = GSON.fromJson(Files.readString(FILE), AfkConfig.class);
				if (loaded != null) INSTANCE = loaded;
				if (INSTANCE.intervalMinutes < 1) INSTANCE.intervalMinutes = 10;
			} else {
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			Files.writeString(FILE, GSON.toJson(INSTANCE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
