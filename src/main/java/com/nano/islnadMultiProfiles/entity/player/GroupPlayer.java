package com.nano.islnadMultiProfiles.entity.player;

import java.util.UUID;

public record GroupPlayer(UUID playerUUID, long groupId, int roleId) { }
