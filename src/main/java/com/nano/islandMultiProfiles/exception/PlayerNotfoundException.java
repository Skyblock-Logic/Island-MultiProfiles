package com.nano.islandMultiProfiles.exception;

public class PlayerNotfoundException extends RuntimeException {
	public PlayerNotfoundException(String message) {
		super(message);
	}
	public PlayerNotfoundException() {
		super("플레이어를 찾지 못했습니다. #PlayerNotFoundException");
	}
}
