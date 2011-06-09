// Copyright (C) 2011 Antony Derham <admin@districtmine.net>

package net.districtmine.warrant;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;

public class Warrant extends JavaPlugin {
	public WarrantSql mysql;

	// Plugin
	private static String name;
	private static String version;
	private static String directory;
	private static WarrantYml config;
	private static PluginDescriptionFile pd;
	private static PluginManager pm;
	
	// Config
	private static String forum;
	private static Boolean multitable;
	private static String sqlhost;
	private static String sqlport;
	private static String sqldb;
	private static String sqluser;
	private static String sqlpass;
	
	private final WarrantListener playerListener = new WarrantListener();

	@Override
	public void onEnable() {
		pd = getDescription();
		pm = getServer().getPluginManager();

		name = pd.getName();
		version = pd.getVersion();
		directory = "plugins" + File.separator + name;

		WarrantLogger.initialize(Logger.getLogger("Minecraft"));

		File configFile = new File(directory + File.separator + "config.yml");

		new File(directory).mkdir();

		if (!configFile.exists()) {
			try {
				// TODO: Copy over default from JAR
				// configFile.createNewFile();
				WarrantLogger
						.error("Couldn't find default config.yml! Disabling...");
				this.getPluginLoader().disablePlugin(this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			config = new WarrantYml(configFile);
			config.load();
			forum = config.readString("forum.type");
			setMultitable(config.readBoolean("forum.multitable"));
			setSqlhost(config.readString("database.host"));
			setSqlport(config.readString("database.port"));
			setSqldb(config.readString("database.db"));
			setSqluser(config.readString("database.user"));
			setSqlpass(config.readString("database.pass"));
		}

		WarrantPermissionsHandler.initialize(this);

		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener,
				Priority.Low, this);

		WarrantLogger.info("Enabled");
		WarrantLogger.info("Using forum engine '" + forum + "'");
	}

	@Override
	public void onDisable() {
		WarrantLogger.info("Disabled");
	}

	public static String getName() {
		return name;
	}

	public static String getVersion() {
		return version;
	}

	public static WarrantYml getConfig() {
		return config;
	}

	public static void setMultitable(Boolean multitable) {
		Warrant.multitable = multitable;
	}

	public static Boolean getMultitable() {
		return multitable;
	}

	public static void setSqlhost(String sqlhost) {
		Warrant.sqlhost = sqlhost;
	}

	public static String getSqlhost() {
		return sqlhost;
	}

	public static void setSqlport(String sqlport) {
		Warrant.sqlport = sqlport;
	}

	public static String getSqlport() {
		return sqlport;
	}

	public static void setSqldb(String sqldb) {
		Warrant.sqldb = sqldb;
	}

	public static String getSqldb() {
		return sqldb;
	}

	public static void setSqluser(String sqluser) {
		Warrant.sqluser = sqluser;
	}

	public static String getSqluser() {
		return sqluser;
	}

	public static void setSqlpass(String sqlpass) {
		Warrant.sqlpass = sqlpass;
	}

	public static String getSqlpass() {
		return sqlpass;
	}
}
