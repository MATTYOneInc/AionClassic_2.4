package com.aionemu.gameserver.events;

import com.aionemu.gameserver.utils.annotations.AnnotatedClass;
import com.aionemu.gameserver.utils.annotations.AnnotatedMethod;
import com.aionemu.gameserver.utils.annotations.AnnotationManager;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import javolution.util.FastTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventSource<T extends AbstractEvent<?>> {
    private static Logger log = LoggerFactory.getLogger(AbstractEventSource.class);
    private Collection<EventListener<T>> listeners = new FastTable(0).shared();
    protected boolean isFirstMethodFill;

    public AbstractEventSource() {
        AnnotatedMethod[] annotated;
        Class<?> theClass = this.getClass();
        if (AnnotationManager.containsClass(theClass)) {
            return;
        }
        this.isFirstMethodFill = true;
        AnnotatedClass annotatedClass = AnnotationManager.getAnnotatedClass(this.getClass());
        for (AnnotatedMethod method : annotated = annotatedClass.getAnnotatedMethods()) {
            if (!this.addListenable(method)) continue;
            log.debug("Added method {}", (Object)method.getMethod());
        }
    }

    protected abstract boolean addListenable(AnnotatedMethod var1);

    public void addEventListener(EventListener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeEventListener(EventListener<T> listener) {
        this.listeners.remove(listener);
    }

    public boolean hasSubscribers() {
        return this.listeners.size() > 0;
    }

    protected abstract boolean canHaveEventNotifications(T var1);

    protected boolean fireBeforeEvent(T event) {
        return this.fireBeforeEvent(event, null);
    }

    protected boolean fireBeforeEvent(T event, Object[] callingArguments) {
        if (!this.canHaveEventNotifications(event)) {
            return false;
        }
        ((AbstractEvent)event).callingArguments = callingArguments;
        Iterator<EventListener<T>> i = this.listeners.iterator();
        while (i.hasNext()) {
            i.next().onBeforeEvent(event);
        }
        return true;
    }

    protected boolean fireAfterEvent(T event) {
        return this.fireAfterEvent(event, null);
    }

    protected boolean fireAfterEvent(T event, Object[] callingArguments) {
        if (!this.canHaveEventNotifications(event) || !((AbstractEvent)event).isHandled()) {
            return false;
        }
        ((AbstractEvent)event).callingArguments = callingArguments;
        Iterator<EventListener<T>> i = this.listeners.iterator();
        while (i.hasNext()) {
            i.next().onAfterEvent(event);
        }
        return true;
    }

    public static final Method getCurrentMethod(Object o) {
        String s = AbstractEventSource.getCallerClass(2).getName();
        Method cm = null;
        for (Method m : o.getClass().getMethods()) {
            if (!m.getName().equals(s)) continue;
            cm = m;
            break;
        }
        return cm;
    }

    public static Class<?> getCallerClass(int i) {
        Class<?>[] classContext = new SecurityManager(){

            @Override
            public Class<?>[] getClassContext() {
                return super.getClassContext();
            }
        }.getClassContext();
        if (classContext != null) {
            for (int j = 0; j < classContext.length; ++j) {
                if (classContext[j] != AbstractEventSource.class) continue;
                return classContext[i + j];
            }
        } else {
            try {
                StackTraceElement[] classNames = Thread.currentThread().getStackTrace();
                for (int j = 0; j < classNames.length; ++j) {
                    if (Class.forName(classNames[j].getClassName()) != AbstractEventSource.class) continue;
                    return Class.forName(classNames[i + j].getClassName());
                }
            }
            catch (ClassNotFoundException classNotFoundException) {

            }
        }
        return null;
    }
}


