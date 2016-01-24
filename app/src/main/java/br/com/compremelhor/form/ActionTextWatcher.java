package br.com.compremelhor.form;

import android.text.Editable;
import android.text.TextWatcher;

import br.com.compremelhor.function.MyConsumer;
import br.com.compremelhor.function.MyPredicate;

public class ActionTextWatcher implements TextWatcher {
    private MyConsumer<MyPredicate> consumer;
    private MyPredicate predicate;

    public ActionTextWatcher(MyConsumer consumer, MyPredicate predicate) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        consumer.accept(predicate);
    }
}
