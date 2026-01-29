package com.nano.islandMultiProfiles.api;

import com.nano.islandMultiProfiles.api.info.IslandInfoProvider;
import com.nano.islandMultiProfiles.api.info.PlayerProvider;
import com.nano.islandMultiProfiles.cache.TestCache;

public class ProfileProviderCore {
	private static ProfileProviderCore instance;
	private final TestCache cache = new TestCache();
	private final IslandInfoProvider infoProvider = new IslandInfoProvider();
	private final PlayerProvider provider = new PlayerProvider();
	private ProfileProviderCore() {}

	public static ProfileProviderCore getInstance() {
		if (instance == null) {
			instance = new ProfileProviderCore();
		}
		return instance;
	}

	public TestCache getCache(){
		return cache;
	}

	public IslandInfoProvider getInfoProvider(){
		return infoProvider;
	}

	public PlayerProvider getProvider(){
		return provider;
	}

}
