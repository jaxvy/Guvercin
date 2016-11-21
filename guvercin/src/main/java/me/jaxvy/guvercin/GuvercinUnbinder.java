package me.jaxvy.guvercin;

/**
 * This interface is provided to the consumer after GuvercinManager.init(). It is used to later on
 * unbind any bindings (usually on onDestory())
 */
public interface GuvercinUnbinder {
    void unbind();

    GuvercinUnbinder EMPTY = new GuvercinUnbinder() {
        @Override
        public void unbind() {
        }
    };
}
