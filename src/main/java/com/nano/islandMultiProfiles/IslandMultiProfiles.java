package com.nano.islandMultiProfiles;

import org.bukkit.plugin.java.JavaPlugin;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.nano.islandMultiProfiles.infra.command.coop.IMCmdCoopInvite;
import com.nano.islandMultiProfiles.infra.command.coop.IMCmdCoopKick;
import com.nano.islandMultiProfiles.infra.command.coop.IMCmdCoopLeave;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdIslandDisband;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdIslandInvite;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdIslandSetting;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdIslandUpgrade;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdPlayerKick;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdRename;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdSetSpawn;
import com.nano.islandMultiProfiles.infra.command.island.common.IMCmdIslandCreate;
import com.nano.islandMultiProfiles.infra.command.island.common.IMCmdTeleport;
import com.nano.islandMultiProfiles.infra.command.island.member.IMCmdIslandAccept;
import com.nano.islandMultiProfiles.listener.IslandUpdateListener;
import com.nano.islandMultiProfiles.service.IslandCoopService;
import com.nano.islandMultiProfiles.service.IslandService;

public final class IslandMultiProfiles extends JavaPlugin {

    private final IslandService islandService = new IslandService(this);
    private final IslandCoopService islandCoopService = new IslandCoopService(this);


    @Override
    public void onEnable() {
        onCommand();
        onEvent();
    }

    private void onCommand(){
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandCreate(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdTeleport(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdRename(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandInvite(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandAccept(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdPlayerKick(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandDisband(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdSetSpawn(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandSetting(islandService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdIslandUpgrade(islandService));

        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdCoopInvite(islandCoopService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdCoopKick(islandCoopService));
        SuperiorSkyblockAPI.getCommands().registerCommand(new IMCmdCoopLeave(islandCoopService));
    }

    private void onEvent(){
        getServer().getPluginManager().registerEvents(new IslandUpdateListener(islandService),this);
    }
}
