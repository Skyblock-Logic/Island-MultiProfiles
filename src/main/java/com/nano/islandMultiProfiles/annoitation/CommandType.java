package com.nano.islandMultiProfiles.annoitation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandType {
	String message() default "플레이어만 사용 가능합니다.";
	boolean player();
	boolean console();
}
