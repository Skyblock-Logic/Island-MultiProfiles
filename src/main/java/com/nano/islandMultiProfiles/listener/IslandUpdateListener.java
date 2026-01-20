package com.nano.islandMultiProfiles.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.events.IslandDisableFlagEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandEnableFlagEvent;
import com.nano.islandMultiProfiles.service.IslandService;

public class IslandUpdateListener implements Listener {

	private final IslandService islandService;

	public IslandUpdateListener(IslandService islandService) {
		this.islandService = islandService;
	}

	/**
	 *
	 */
	@EventHandler
	public void enable(IslandEnableFlagEvent event){
		islandService.update(event.getIsland());
	}

	@EventHandler
	public void disable(IslandDisableFlagEvent event){
		islandService.update(event.getIsland());
	}
}
