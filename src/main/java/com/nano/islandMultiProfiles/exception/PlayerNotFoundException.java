package com.nano.islandMultiProfiles.exception;

public class PlayerNotFoundException extends RuntimeException {
	public PlayerNotFoundException(String message) {
		super(message);
	}
	public PlayerNotFoundException() {
		super("플레이어를 찾지 못했습니다. #PlayerNotFoundException");
	}
}
