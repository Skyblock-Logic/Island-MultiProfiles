package com.nano.islandMultiProfiles;

import org.bukkit.plugin.java.JavaPlugin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.nano.islandMultiProfiles.infra.command.IMCmdIslandCreate;
import com.nano.islandMultiProfiles.infra.command.IMCmdTeleport;
import com.nano.islandMultiProfiles.listener.IslandUpdateListener;
import com.nano.islandMultiProfiles.service.IslandService;

public final class IslandMultiProfiles extends JavaPlugin {

    private final IslandService islandService = new IslandService(this);


    @Override
    public void onEnable() {
        onCommand();
        onEvent();
    }

    private void onCommand(){
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandCreate(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdTeleport(islandService));
    }

    private void onEvent(){
        getServer().getPluginManager().registerEvents(new IslandUpdateListener(islandService),this);
    }

    @Override
    public void onDisable() {

    }
}
