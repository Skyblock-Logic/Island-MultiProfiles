package com.nano.islandMultiProfiles.entity.player;

import java.util.UUID;

public record GroupPlayer(UUID playerUUID, long groupId, int roleId) { }
