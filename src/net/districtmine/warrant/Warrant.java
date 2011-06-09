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

        if(!configFile.exists()){
            try {
                // TODO: Copy over default from JAR
            	//configFile.createNewFile();
            	WarrantLogger.error("Couldn't find default config.yml! Disabling...");
            	this.getPluginLoader().disablePlugin(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
    	    config = new WarrantYml(configFile);
    	    config.load();
        }
        
		WarrantPermissionsHandler.initialize(this);
		
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Priority.Low, this);
		
    	WarrantLogger.info("Enabled");
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
}
