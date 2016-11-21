package me.jaxvy.guvercin;

/**
 * Used to manage generated _Guvercin classes in runtime
 */
public interface GuvercinBinder<T> {
    GuvercinUnbinder bind(T target);
}
