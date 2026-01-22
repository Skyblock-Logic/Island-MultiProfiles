package com.nano.islandMultiProfiles.identity.policy;

public final class IslandNamePolicy {

	private static final String SUFFIX = "-sub-";

	private IslandNamePolicy() {}

	public static String encode(String baseName, int slot) {
		return slot == 1 ? baseName : baseName + SUFFIX + slot;
	}

	public static String decode(String fullName) {
		int idx = fullName.lastIndexOf(SUFFIX);
		if (idx == -1) return fullName;
		return fullName.substring(0, idx);
	}
}
