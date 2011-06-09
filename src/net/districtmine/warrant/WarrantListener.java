// Copyright (C) 2011 Antony Derham <admin@districtmine.net>

package net.districtmine.warrant;

//import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;

public class WarrantListener extends PlayerListener {
	//private final Warrant plugin;

	public WarrantListener(Warrant instance) {
	//	plugin = instance;
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		//Player player = event.getPlayer();
		/*try {
			plugin.warrantPlayer(player);
		} catch (InterruptedException e) {
            plugin.consoleError("Warrant screwed up big time! Oopsy! Warrant wasn't able to do it's job at all :(");
            plugin.consoleError(e.toString());
    		e.printStackTrace();
		}*/
	}
}