package br.com.compremelhor.function;

/**
 * Created by adriano on 27/03/16.
 */
public interface MyBiFunction<T, U, R> {
    T apply(T t, U u);
}
