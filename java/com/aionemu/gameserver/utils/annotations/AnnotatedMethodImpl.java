package com.aionemu.gameserver.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class AnnotatedMethodImpl
        implements AnnotatedMethod {
    private final AnnotatedClass annotatedClass;
    private final Method method;
    private Map<Class<?>, Annotation> classToAnnotationMap = null;
    private Annotation[] annotations = null;

    AnnotatedMethodImpl(AnnotatedClass annotatedClass, Method method) {
        this.annotatedClass = annotatedClass;
        this.method = method;
    }

    private Map<Class<?>, Annotation> getAllAnnotationMap() {
        if (this.classToAnnotationMap == null) {
            this.classToAnnotationMap = this.getAllAnnotationMapCalculated();
        }
        return this.classToAnnotationMap;
    }

    private Map<Class<?>, Annotation> getAllAnnotationMapCalculated() {
        HashMap result = new HashMap();
        Class<?> superClass = this.getAnnotatedClass().getTheClass().getSuperclass();
        if (superClass != null) {
            this.fillAnnotationsForOneMethod(result, AnnotationManager.getAnnotatedClass(superClass).getAnnotatedMethod(this.getMethod().getName(), this.getMethod().getParameterTypes()));
        }
        for (Class<?> c : this.getAnnotatedClass().getTheClass().getInterfaces()) {
            this.fillAnnotationsForOneMethod(result, AnnotationManager.getAnnotatedClass(c).getAnnotatedMethod(this.getMethod().getName(), this.getMethod().getParameterTypes()));
        }
        for (Annotation annotation : this.getMethod().getDeclaredAnnotations()) {
            result.put(annotation.getClass().getInterfaces()[0], annotation);
        }
        return result;
    }

    private void fillAnnotationsForOneMethod(HashMap<Class<?>, Annotation> result, AnnotatedMethod annotatedMethod) {
        if (annotatedMethod == null) {
            return;
        }
        this.addAnnotations(result, annotatedMethod.getAllAnnotations());
    }

    private void addAnnotations(HashMap<Class<?>, Annotation> result, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation == null) continue;
            result.put(annotation.getClass().getInterfaces()[0], annotation);
        }
    }

    @Override
    public Annotation[] getAllAnnotations() {
        if (this.annotations == null) {
            this.annotations = this.getAllAnnotationsCalculated();
        }
        return this.annotations;
    }

    private Annotation[] getAllAnnotationsCalculated() {
        Collection<Annotation> values = this.getAllAnnotationMap().values();
        return values.toArray(new Annotation[0]);
    }

    @Override
    public AnnotatedClass getAnnotatedClass() {
        return this.annotatedClass;
    }

    @Override
    public Annotation getAnnotation(Class<?> annotationClass) {
        return this.getAllAnnotationMap().get(annotationClass);
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}

