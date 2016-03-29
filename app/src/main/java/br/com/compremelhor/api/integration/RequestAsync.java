package br.com.compremelhor.api.integration;

import android.os.AsyncTask;

import br.com.compremelhor.function.MyFunction;

/**
 * Created by adriano on 27/03/16.
 */
public class RequestAsync<T, R> extends AsyncTask<T, Void, R> {
    private MyFunction<T, R> function;

    public RequestAsync(MyFunction<T, R> function) {
        this.function = function;
    }

    @Override
    protected R doInBackground(T... params) {
        return function.apply(params[0]);
    }
}
