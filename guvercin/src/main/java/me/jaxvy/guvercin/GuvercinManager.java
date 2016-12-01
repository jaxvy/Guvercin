package me.jaxvy.guvercin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GuvercinManager {

    private static final String BINDER_SUFFIX = "_Guvercin";
    private static Map<Class<?>, Constructor<? extends GuvercinBinder>> BINDINGS = new HashMap<>();

    private static final GuvercinBinder<Object> NOP_GUVERCIN_BINDER = new GuvercinBinder<Object>() {
        @Override
        public GuvercinUnbinder bind(Object target) {
            return GuvercinUnbinder.EMPTY;
        }
    };

    public static GuvercinUnbinder init(Object target) {
        return getBinder(target).bind(target);
    }

    private static GuvercinBinder getBinder(Object target) {
        Class<?> targetClass = target.getClass();
        Constructor<? extends GuvercinBinder> constructor = BINDINGS.get(targetClass);
        boolean isClassGuvercinAnnotated = true;
        if (constructor == null) {
            String binderKey = targetClass.getCanonicalName() + BINDER_SUFFIX;
            try {
                Class<?> guvercinBinderClass = Class.forName(binderKey);
                constructor = (Constructor<? extends GuvercinBinder>) guvercinBinderClass.getConstructor();
                BINDINGS.put(targetClass, constructor);
            } catch (ClassNotFoundException e) {
                isClassGuvercinAnnotated = false;
            } catch (NoSuchMethodException e) {
                isClassGuvercinAnnotated = false;
            }
        }

        GuvercinBinder guvercinBinder = NOP_GUVERCIN_BINDER;
        if (isClassGuvercinAnnotated) {
            try {
                guvercinBinder = constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return guvercinBinder;
    }
}
