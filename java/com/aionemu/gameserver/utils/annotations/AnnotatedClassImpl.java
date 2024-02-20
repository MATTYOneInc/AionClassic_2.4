package com.aionemu.gameserver.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class AnnotatedClassImpl
        implements AnnotatedClass {
    private final Class<?> theClass;
    private Map<Class<?>, Annotation> classToAnnotationMap = null;
    private Map<Method, AnnotatedMethod> methodToAnnotatedMap = null;
    private Annotation[] annotations = null;
    private AnnotatedMethod[] annotatedMethods = null;

    AnnotatedClassImpl(Class<?> theClass) {
        this.theClass = theClass;
    }

    private Map<Class<?>, Annotation> getAllAnnotationMap() {
        if (this.classToAnnotationMap == null) {
            this.classToAnnotationMap = this.getAllAnnotationMapCalculated();
        }
        return this.classToAnnotationMap;
    }

    private Map<Class<?>, Annotation> getAllAnnotationMapCalculated() {
        HashMap result = new HashMap();
        Class<?> superClass = this.getTheClass().getSuperclass();
        if (superClass != null) {
            this.fillAnnotationsForOneClass(result, superClass);
        }
        for (Class<?> c : this.getTheClass().getInterfaces()) {
            this.fillAnnotationsForOneClass(result, c);
        }
        for (Annotation annotation : this.getTheClass().getDeclaredAnnotations()) {
            result.put(annotation.getClass().getInterfaces()[0], annotation);
        }
        return result;
    }

    private void fillAnnotationsForOneClass(HashMap<Class<?>, Annotation> result, Class<?> baseClass) {
        this.addAnnotations(result, AnnotationManager.getAnnotatedClass(baseClass).getAllAnnotations());
    }

    private void addAnnotations(HashMap<Class<?>, Annotation> result, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;
            if (result.containsKey(annotation.getClass().getInterfaces()[0])) {
                result.put(annotation.getClass().getInterfaces()[0], null);
                continue;
            }
            result.put(annotation.getClass().getInterfaces()[0], annotation);
        }
    }

    @Override
    public Class<?> getTheClass() {
        return this.theClass;
    }

    @Override
    public Annotation[] getAllAnnotations() {
        if (this.annotations == null) {
            this.annotations = this.getAllAnnotationsCalculated();
        }
        return this.annotations;
    }

    private Annotation[] getAllAnnotationsCalculated() {
        return this.getAllAnnotationMap().values().toArray(new Annotation[0]);
    }

    @Override
    public Annotation getAnnotation(Class<?> annotationClass) {
        return this.getAllAnnotationMap().get(annotationClass);
    }

    private Map<Method, AnnotatedMethod> getMethodMap() {
        if (this.methodToAnnotatedMap == null) {
            this.methodToAnnotatedMap = this.getMethodMapCalculated();
        }
        return this.methodToAnnotatedMap;
    }

    private Map<Method, AnnotatedMethod> getMethodMapCalculated() {
        LinkedHashMap<Method, AnnotatedMethod> result = new LinkedHashMap<Method, AnnotatedMethod>();
        ArrayList<Method> methods = new ArrayList<Method>();
        this.getAllMethods(this.getTheClass(), methods);
        for (Method method : methods) {
            if (method.getAnnotations().length == 0) continue;
            result.put(method, new AnnotatedMethodImpl(this, method));
        }
        return result;
    }

    private void getAllMethods(Class<?> clazz, ArrayList<Method> methods) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        List<Method> declared = Arrays.asList(clazz.getDeclaredMethods());
        methods.addAll(declared);
        if (clazz.getSuperclass() != null) {
            this.getAllMethods(clazz.getSuperclass(), methods);
        }
    }

    @Override
    public AnnotatedMethod getAnnotatedMethod(Method method) {
        return this.getMethodMap().get(method);
    }

    @Override
    public AnnotatedMethod[] getAnnotatedMethods() {
        if (this.annotatedMethods == null) {
            this.annotatedMethods = this.getAnnotatedMethodsCalculated();
        }
        return this.annotatedMethods;
    }

    private AnnotatedMethod[] getAnnotatedMethodsCalculated() {
        Collection<AnnotatedMethod> values = this.getMethodMap().values();
        return values.toArray(new AnnotatedMethod[0]);
    }

    @Override
    public AnnotatedMethod getAnnotatedMethod(String name, Class<?>[] parameterType) {
        try {
            return this.getAnnotatedMethod(this.getTheClass().getMethod(name, parameterType));
        }
        catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }
}

