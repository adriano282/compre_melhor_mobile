package br.com.compremelhor.function;

/**
 * Created by adriano on 27/03/16.
 */
public interface MyFunction<T, R> {
    R apply(T t);
}
