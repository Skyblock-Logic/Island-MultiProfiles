package com.nano.islandMultiProfiles.identity.policy;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class FakeIslandUUIdPolicy {
	private FakeIslandUUIdPolicy() {}

	public static UUID issue(UUID fakePlayerUUID, int slot) {
		String source = "fakePlayerUUID:" + fakePlayerUUID.toString() + ",slot:" + slot;
		return UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
	}
}
