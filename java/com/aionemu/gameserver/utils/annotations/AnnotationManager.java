package com.aionemu.gameserver.utils.annotations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationManager {
    private static Map<Class<?>, AnnotatedClass> classToAnnotatedMap = new ConcurrentHashMap();

    public static AnnotatedClass getAnnotatedClass(Class<?> theClass) {
        AnnotatedClass annotatedClass = classToAnnotatedMap.get(theClass);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClassImpl(theClass);
            classToAnnotatedMap.put(theClass, annotatedClass);
        }
        return annotatedClass;
    }

    public static boolean containsClass(Class<?> theClass) {
        return classToAnnotatedMap.get(theClass) != null;
    }
}

