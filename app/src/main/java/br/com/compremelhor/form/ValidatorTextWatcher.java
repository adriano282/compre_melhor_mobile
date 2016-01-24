package br.com.compremelhor.form;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import br.com.compremelhor.function.MyPredicate;

public class ValidatorTextWatcher implements TextWatcher {
    private EditText editText;
    private Context ctx;
    private MyPredicate predicate;
    private Validation validator;
    private String errMsg;
    private boolean isPasswordField = false;

    public ValidatorTextWatcher(Context ctx, EditText editText, String errMsg, MyPredicate predicate) {
        this(ctx, editText);
        this.errMsg = errMsg;
        this.predicate = predicate;
    }

    public ValidatorTextWatcher(Context ctx, EditText editText) {
        this(ctx);
        this.editText = editText;
    }

    public ValidatorTextWatcher(Context ctx) {
        this.ctx = ctx;
        validator = new Validation(ctx);
    }


    public ValidatorTextWatcher(Context ctx, EditText editText, String errMsg, MyPredicate predicate, boolean isPasswordField) {
        this(ctx, editText);
        this.errMsg = errMsg;
        this.predicate = predicate;
        this.isPasswordField = isPasswordField;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {

        if (isPasswordField && !validator.isValidPassword(editText))
            return;

        else if (predicate != null) {
            validator.isValid(editText, predicate, errMsg, true);
        }
        else {
            validator.isValid(editText);
        }
    }
}

