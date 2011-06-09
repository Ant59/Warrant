// Copyright (C) 2011 Antony Derham <admin@districtmine.net>

package net.districtmine.warrant;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class WarrantListener extends PlayerListener {

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Thread process = new Thread(new WarrantProcess(player));
		process.start();
	}
}