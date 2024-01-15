package dev.tylerm.khs.configuration;

import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Localization {

	public static final Map<String,LocalizationString> LOCAL = new HashMap<>();
	public static final Map<String,LocalizationString> DEFAULT_LOCAL = new HashMap<>();

	private static final Map<String,String[][]> CHANGES = new HashMap<String,String[][]>() {{
		put("en-US", new String[][]{
				{"WORLDBORDER_DECREASING"},
				{"START","TAUNTED"},
				{"GAME_SETUP", "SETUP_GAME", "SETUP_LOBBY", "SETUP_SEEKER_LOBBY", "SETUP_EXIT", "SETUP_SAVEMAP", "SETUP_BOUNDS"},
                {"GAME_PLAYER_FOUND", "GAME_PLAYER_FOUND_BY"}
		});
		put("de-DE", new String[][]{
				{},
				{"TAUNTED"},
				{"GAME_SETUP", "SETUP_GAME", "SETUP_LOBBY", "SETUP_SEEKER_LOBBY", "SETUP_EXIT", "SETUP_SAVEMAP", "SETUP_BOUNDS"},
                {"GAME_PLAYER_FOUND", "GAME_PLAYER_FOUND_BY"}
		});
	}};

	public static void loadLocalization() {

		ConfigManager manager = ConfigManager.create("localization.yml", "lang/localization_"+Config.locale +".yml");

		int PLUGIN_VERSION = manager.getDefaultInt("version");
		int VERSION = manager.getInt("version");
		if (VERSION < PLUGIN_VERSION) {
			for(int i = VERSION; i < PLUGIN_VERSION; i++) {
				if (i < 1) continue;
				String[] changeList = CHANGES.get(Config.locale)[i-1];
				for(String change : changeList)
					manager.reset("Localization." + change);
			}
			manager.reset("version");
		}

		String SELECTED_LOCAL = manager.getString("type");
		if (SELECTED_LOCAL == null) {
			manager.reset("type");
		} else if (!SELECTED_LOCAL.equals(Config.locale)) {
			manager.resetFile("lang"+File.separator+"localization_"+Config.locale +".yml");
		}

		manager.saveConfig();

		for(String key : manager.getConfigurationSection("Localization").getKeys(false)) {
			LOCAL.put(
					key,
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', manager.getString("Localization."+key) ) )
			);
		}

		for(String key : manager.getDefaultConfigurationSection("Localization").getKeys(false)) {
			DEFAULT_LOCAL.put(
					key,
					new LocalizationString( ChatColor.translateAlternateColorCodes('&', manager.getString("Localization."+key) ) )
			);
		}
	}
	
	public static LocalizationString message(String key) {
		LocalizationString message = LOCAL.get(key);
		if (message == null) {
			LocalizationString defaultMessage = DEFAULT_LOCAL.get(key);
			if(defaultMessage == null) {
				return new LocalizationString(ChatColor.RED + "" + ChatColor.ITALIC + key + " is not found in localization.yml. This is a plugin issue, please report it.");
			}
			return new LocalizationString(defaultMessage.toString());
		}
		return new LocalizationString(message.toString());
	}
}
