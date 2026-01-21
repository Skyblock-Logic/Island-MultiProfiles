package com.nano.islandMultiProfiles.identity.matcher;

import java.util.OptionalInt;
import java.util.UUID;

import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;

public final class FakeIslandUUIDMatcher {
	private FakeIslandUUIDMatcher() {}

	public static OptionalInt matchSlotId(UUID fakePlayerUUID, UUID islandUUID, int maxSlotSize){
		for (int slot = 1; slot <= maxSlotSize; slot++) {
			UUID expected = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
			if (expected.equals(islandUUID)) {
				return OptionalInt.of(slot);
			}
		}
		return OptionalInt.empty();
	}
}
