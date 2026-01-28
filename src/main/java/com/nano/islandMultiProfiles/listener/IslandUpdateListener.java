package com.nano.islandMultiProfiles.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.events.IslandCoopPlayerEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisableFlagEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandEnableFlagEvent;
import com.nano.islandMultiProfiles.service.IslandService;

public class IslandUpdateListener implements Listener {

	private final IslandService islandService;

	public IslandUpdateListener(IslandService islandService) {
		this.islandService = islandService;
	}

	@EventHandler
	public void enable(IslandEnableFlagEvent event){
		islandService.syncFlagsToSlots(event.getIsland());
	}

	@EventHandler
	public void disable(IslandDisableFlagEvent event){
		islandService.syncFlagsToSlots(event.getIsland());
	}
	@EventHandler
	public void coop(IslandCoopPlayerEvent event){
		islandService.syncCoopToSlots(event.getIsland());
	}
}
