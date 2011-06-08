// Copyright (C) 2011 Antony Derham <admin@districtmine.net>

package net.districtmine.warrant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Warrant extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");

	private final String PROP_MYSQL_HOST = "mysql-host";
	private final String PROP_MYSQL_PORT = "mysql-port";
	private final String PROP_MYSQL_USER = "mysql-user";
	private final String PROP_MYSQL_PASS = "mysql-pass";
	private final String PROP_MYSQL_DB = "mysql-db";
	private final String PROP_MYSQL_USERS_TABLE = "users-table";
	private final String PROP_MYSQL_OPTIONS_TABLE = "minecraftname-table";
	private final String PROP_USERS_TABLE_MEMBERID_FIELD = "memberid-field";
	private final String PROP_OPTIONS_TABLE_MEMBERID_FIELD = "minecraftname-table-memberid-field";
	private final String PROP_GROUP_FIELD = "group-field";
	private final String PROP_USERNAME_FIELD = "minecraftname-field";
	
	private final String configurationFile = "warrant.properties";
	private final String groupsFile = "groups.properties";

	// Plugin
	private String version;
	private PluginDescriptionFile pdfFile;

	// Attributes
	private final WarrantListener playerListner = new WarrantListener(this);
	private File warrantFolder;

	// Config
	private final Properties propGroups = new Properties();
	private final Properties propConfig = new Properties();
	HashMap<Integer, String> propGroupsMap;
	
	// GM Vars
	private WorldsHolder groupManagerHandler;
	
	// Permissions Vars
	public PermissionHandler permissionHandler;
	
	// So... which one we gonna use? 1=GM 2=Perms
	private static Integer rankingPlugin;
	
	// Woop threaded!! :D
	private static class AuthoriseWarrant implements Runnable {
		private final Warrant plugin;
		private Player warrantee;
		
		// GM Vars
		private User auxUser;
		
		// Perms Vars

		// Database
		private MySQL sql;
		
		// Properties...
		private String propMysqlUser;
		private String propMysqlPass;
		private String propMysqlHost;
		private String propMysqlPort;
		private String propMysqlDb;
		private String propMysqlUsersTable;
		private String propMysqlOptionsTable;
		private String propUsersTableMemberIdField;
		private String propOptionsTableMemberIdField;
		private String propGroupField;
		private String propUsernameField;

		private AuthoriseWarrant(Warrant instance, Player player, Properties propConfig) {
			plugin = instance;
			warrantee = player;
			
			propMysqlUser = propConfig.getProperty(plugin.PROP_MYSQL_USER);
			propMysqlPass = propConfig.getProperty(plugin.PROP_MYSQL_PASS);
			propMysqlHost = propConfig.getProperty(plugin.PROP_MYSQL_HOST);
			propMysqlPort = propConfig.getProperty(plugin.PROP_MYSQL_PORT);
			propMysqlDb = propConfig.getProperty(plugin.PROP_MYSQL_DB);
			propMysqlUsersTable = propConfig.getProperty(plugin.PROP_MYSQL_USERS_TABLE);
			propMysqlOptionsTable = propConfig.getProperty(plugin.PROP_MYSQL_OPTIONS_TABLE);
			propUsersTableMemberIdField = propConfig.getProperty(plugin.PROP_USERS_TABLE_MEMBERID_FIELD);
			propOptionsTableMemberIdField = propConfig.getProperty(plugin.PROP_OPTIONS_TABLE_MEMBERID_FIELD);
			propGroupField = propConfig.getProperty(plugin.PROP_GROUP_FIELD);
			propUsernameField = propConfig.getProperty(plugin.PROP_USERNAME_FIELD);
		}
		
        public void run() {
        	try {
        		String playerName = warrantee.getName();
    			
    			// Load database configuration and connect...
    			sql = new MySQL(propMysqlUser,propMysqlPass,propMysqlHost,propMysqlPort,propMysqlDb);
    			
    			ResultSet rs;
    			
    			plugin.consoleLog(propMysqlOptionsTable);
    			
    			if (propMysqlOptionsTable.isEmpty()) {
    				rs = sql.trySelect("SELECT " + propMysqlUsersTable + '.' + propGroupField + " FROM " + propMysqlUsersTable + " WHERE " + propUsernameField + " = '" + playerName + "'");
    			} else {
    				rs = sql.trySelect("SELECT " + propMysqlUsersTable + '.' + propGroupField + " FROM " + propMysqlUsersTable + " INNER JOIN " + propMysqlOptionsTable + " ON (" + propMysqlUsersTable + "." + propUsersTableMemberIdField + " = " + propMysqlOptionsTable + "." + propOptionsTableMemberIdField + " AND " + propUsernameField + " = '" + playerName + "')");
    			}
    			
    			if (rs.next() != false) {
    				String userGroup = rs.getString(propMysqlUsersTable + '.' + propGroupField);
    				String userGroupName = plugin.propGroups.getProperty(userGroup);
    				plugin.consoleLog(userGroupName);
            		if (rankingPlugin == 1) {
    	    			OverloadedWorldHolder dataHolder = plugin.groupManagerHandler.getWorldData(warrantee);
    	    			auxUser = dataHolder.getUser(playerName);

        				Group auxGroup = dataHolder.getGroup(userGroupName);
        				auxUser.setGroup(auxGroup);
            		} else if (rankingPlugin == 2) {
            			com.nijiko.permissions.Group permsGroup = plugin.permissionHandler.getGroupObject("world", userGroupName);
            			plugin.permissionHandler.getUserObject("world", playerName).addParent(permsGroup);
            		} else {
            			plugin.consoleError("Cocked up big time... can't find GroupManager or Permissions!");
            			throw new Exception();
            		}
    			} else {
    				plugin.consoleWarning("No warrant granted. Unregistered player belongs to default group.");
    			}
            } catch (Exception e) {
                plugin.consoleError("Warrant not issued! Warrant's authorisation thread failed!");
                plugin.consoleError(e.toString());
        		e.printStackTrace();
            }
        }
    }

	public void onEnable() {
		warrantFolder = this.getDataFolder();
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_LOGIN, playerListner, Priority.Low, this);

		this.pdfFile = this.getDescription();
		this.version = this.pdfFile.getVersion();
		
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		Plugin groupManangerPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");

        if (groupManangerPlugin != null) {
            if (!this.getServer().getPluginManager().isPluginEnabled(groupManangerPlugin)) {
                this.getServer().getPluginManager().enablePlugin(groupManangerPlugin);
                consoleLog("GroupManager detected! Enabling automagically!");
            }
            groupManagerHandler = ((GroupManager) groupManangerPlugin).getWorldsHolder();
            rankingPlugin = 1;
            consoleLog("Using GroupManager!");
        } else if (permissionsPlugin != null) {
            if (!this.getServer().getPluginManager().isPluginEnabled(permissionsPlugin)) {
                this.getServer().getPluginManager().enablePlugin(permissionsPlugin);
                consoleLog("Permissions detected! Enabling automagically!");
            }
		    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            rankingPlugin = 2;
            consoleLog("Using Permissions!");
		} else {
        	consoleError("Couldn't find GroupManager or Permissions! Disabling...");
            this.getPluginLoader().disablePlugin(this);
        }
		
		// Create folders and files
		if (!warrantFolder.exists()) {
			consoleLog("Config folder missing, creating...");
			warrantFolder.mkdir();
			consoleLog("Folder created");
		}

		File fConfig = new File(warrantFolder.getAbsolutePath() + File.separator + configurationFile);
		if (!fConfig.exists()) {
			consoleLog("Config is missing, creating...");
			try {
				fConfig.createNewFile();
				Properties propConfig = new Properties();
				propConfig.setProperty(PROP_MYSQL_HOST, "localhost");
				propConfig.setProperty(PROP_MYSQL_PORT, "3306");
				propConfig.setProperty(PROP_MYSQL_USER, "user");
				propConfig.setProperty(PROP_MYSQL_PASS, "pass");
				propConfig.setProperty(PROP_MYSQL_DB, "forum");
				propConfig.setProperty(PROP_MYSQL_USERS_TABLE, "smf_members");
				propConfig.setProperty(PROP_MYSQL_OPTIONS_TABLE, "");
				propConfig.setProperty(PROP_USERS_TABLE_MEMBERID_FIELD, "id_member");
				propConfig.setProperty(PROP_OPTIONS_TABLE_MEMBERID_FIELD, "");
				propConfig.setProperty(PROP_GROUP_FIELD, "id_group");
				propConfig.setProperty(PROP_USERNAME_FIELD, "member_name");
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fConfig.getAbsolutePath()));
				propConfig.store(stream, "Automatically generated config file");
				consoleLog("Configuration created");
			} catch (IOException ex) {
				consoleWarning("Configuration file creation failure");
			}
		}
		
		File fGroups = new File(warrantFolder.getAbsolutePath() + File.separator + groupsFile);
		if (!fGroups.exists()) {
        	consoleError("Couldn't find groups.properties! You MUST create this yourself! Disabling...");
            this.getPluginLoader().disablePlugin(this);
            return;
		}
		
		loadSettings();

		consoleLog("Enabled version " + version + "!");
	}

	public void onDisable() {
		consoleLog("Goodbye!");
	}

	public boolean loadSettings() {
		consoleLog("Trying to load settings...");
		try {
			// Load warrant.properties
			BufferedInputStream streamConfig = new BufferedInputStream(new FileInputStream(warrantFolder.getAbsolutePath() + File.separator + configurationFile));
			propConfig.load(streamConfig);
			
			BufferedInputStream streamGroups = new BufferedInputStream(new FileInputStream(warrantFolder.getAbsolutePath() + File.separator + groupsFile));
			propGroups.load(streamGroups);
			
			consoleLog("Settings Loaded");
		} catch (Exception ex) {
			consoleWarning("Failed to load settings. Disabling...");
            this.getPluginLoader().disablePlugin(this);
			return false;
		}
		return true;
	}

	public void warrantPlayer(Player player) throws InterruptedException {
		consoleLog("Player login! Attempting to grant warrant...");
		
        long startTime = System.currentTimeMillis();
        long patience = 1000 * 60;
        Thread t = new Thread(new AuthoriseWarrant(this, player, propConfig));
        t.start();
        
        while (t.isAlive()) {
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {
            	consoleWarning("Taking too long to authorise a warrant! Interrupting thread!");
                t.interrupt();
                t.join();
            }
        }
        
		consoleLog("Warrant processed");
	}

	public void consoleLog(String msg) {
		log.info("[Warrant] - " + msg);
	}

	public void consoleWarning(String msg) {
		log.warning("[Warrant] - " + msg);
	}

	public void consoleError(String msg) {
		log.severe("[Warrant] - " + msg);
	}
}
