package com.aionemu.gameserver.ai2.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value= RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface AIListenable {
    public boolean enabled() default true;

    public AIEventType type();
}