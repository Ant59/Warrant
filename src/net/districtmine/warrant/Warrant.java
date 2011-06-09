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

import com.alta189.sqlLibrary.MySQL.mysqlCore;

public class Warrant extends JavaPlugin {
	public static final Logger log = Logger.getLogger("Minecraft");
	public mysqlCore mysql;

	public void consoleLog(String msg) {
		log.info("[Warrant] - " + msg);
	}

	public void consoleWarning(String msg) {
		log.warning("[Warrant] - " + msg);
	}

	public void consoleError(String msg) {
		log.severe("[Warrant] - " + msg);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}
}
