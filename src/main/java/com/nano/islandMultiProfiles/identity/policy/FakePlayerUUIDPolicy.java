package com.nano.islandMultiProfiles.identity.policy;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class FakePlayerUUIDPolicy {
	private FakePlayerUUIDPolicy() {}

	public static UUID issue(UUID playerUUID, int groupId){
		String source = "playerUUID:" + playerUUID.toString() + ",group:" + groupId;
		return UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
	}
}
