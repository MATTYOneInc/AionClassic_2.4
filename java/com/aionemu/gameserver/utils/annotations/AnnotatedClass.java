package com.aionemu.gameserver.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotatedClass {
    public Class<?> getTheClass();

    public Annotation[] getAllAnnotations();

    public Annotation getAnnotation(Class<?> var1);

    public AnnotatedMethod[] getAnnotatedMethods();

    public AnnotatedMethod getAnnotatedMethod(String var1, Class<?>[] var2);

    public AnnotatedMethod getAnnotatedMethod(Method var1);
}

