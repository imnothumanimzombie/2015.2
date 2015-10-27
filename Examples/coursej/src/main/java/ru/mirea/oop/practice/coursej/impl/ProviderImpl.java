package ru.mirea.oop.practice.coursej.impl;

import ru.mirea.oop.practice.coursej.api.Provider;

import java.lang.reflect.Constructor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ProviderImpl<T> implements Provider<T> {
    private final Class<T> klass;
    private final Object[] params;
    private volatile T created;

    public ProviderImpl(Class<T> klass, Object... objects) {
        this.klass = klass;
        this.params = objects == null ? new Object[0] : objects;
    }

    @Override
    public T get() {
        if (created == null) {
            synchronized (ProviderImpl.class) {
                if (created == null) {
                    try {
                        Class[] classes = Stream.of(params).map(Object::getClass)
                                .collect(Collectors.toList()).toArray(new Class[params.length]);
                        Constructor<T> constructor = klass.getDeclaredConstructor(classes);
                        constructor.setAccessible(true);
                        created = constructor.newInstance(params);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return created;
    }

    @Override
    public void put(T value) {
        synchronized (ProviderImpl.class) {
            created = value;
        }
    }
}
