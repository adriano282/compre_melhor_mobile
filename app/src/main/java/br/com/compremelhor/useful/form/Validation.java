package br.com.compremelhor.useful.form;

import android.content.Context;
import android.widget.EditText;

import br.com.compremelhor.R;
import br.com.compremelhor.useful.function.MyPredicate;

public class Validation {
    private Context ctx;

    public Validation(Context ctx) {
        this.ctx = ctx;
    }

    public boolean isValid(EditText editText, MyPredicate predicate, String errMsg, boolean required) {
        if (required && !hasText(editText))
            return false;

        if (!predicate.test()) {
            editText.setError(errMsg);
            return false;
        }
        return true;
    }

    public boolean isValid(EditText editText) {
        return hasText(editText);
    }

    public boolean isValidPassword(EditText editText) {
        if (!hasText(editText))
            return false;

        if (editText.getText().toString().contains(" ")) {
            editText.setError(ctx.getString(R.string.err_invalid_password_cannot_contain_space));
            return false;
        }
        return true;
    }

    public boolean hasText(EditText editText) {
        if (!editText.getText().toString().trim().isEmpty())
            return true;

        editText.setError(ctx.getString(R.string.err_field_not_filled));
        return false;
    }
}
