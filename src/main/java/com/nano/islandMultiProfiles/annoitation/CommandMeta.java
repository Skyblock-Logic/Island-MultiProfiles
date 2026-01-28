package com.nano.islandMultiProfiles.annoitation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMeta {
	String[] aliases();
	String permission() default "";
	String usage() default "";
	String description() default "";
	int minArgs() default 0;
	int maxArgs() default Integer.MAX_VALUE;
	boolean console() default true;
	boolean display() default true;
}