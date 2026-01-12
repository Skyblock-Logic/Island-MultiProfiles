package com.nano.islnadMultiProfiles.entity.island;

import java.util.UUID;

public class IslandProfile {
    private final UUID islandUUID;
    private UUID fakePlayerUUID;
    private long groupId;

    private int slotIndex;
    private String islandName;

    public IslandProfile(UUID islandUUID, UUID fakePlayerUUID, long groupId, int slotIndex, String islandName) {
        this.islandUUID = islandUUID;
        this.fakePlayerUUID = fakePlayerUUID;
        this.groupId = groupId;
        this.slotIndex = slotIndex;
        this.islandName = islandName;
    }

    public UUID getIslandUUID() {
        return islandUUID;
    }

    public UUID getFakePlayerUUID() {
        return fakePlayerUUID;
    }

    public void setFakePlayerUUID(UUID fakePlayerUUID) {
        this.fakePlayerUUID = fakePlayerUUID;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public String getIslandName() {
        return islandName;
    }

    public void setIslandName(String islandName) {
        this.islandName = islandName;
    }
}
