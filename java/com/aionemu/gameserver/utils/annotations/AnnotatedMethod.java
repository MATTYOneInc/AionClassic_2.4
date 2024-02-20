package com.aionemu.gameserver.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotatedMethod {
    public AnnotatedClass getAnnotatedClass();

    public Method getMethod();

    public Annotation[] getAllAnnotations();

    public Annotation getAnnotation(Class<?> var1);
}

