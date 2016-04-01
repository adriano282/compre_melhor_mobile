package br.com.compremelhor.form.validator;

import android.content.Context;
import android.widget.EditText;

import br.com.compremelhor.R;
import br.com.compremelhor.function.MyPredicate;

public class Validation {
    private Context ctx;

    public Validation(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * @param editText: Widget for be validated
     * @param predicate: Predicate that contains de logic for validation
     * @param errMsg: Error message cause the validation fails
     * @param required: If field is required or not
     * @return: If true: The validation passed
     *              false: The validation fails,
     *              the error message is showed on screen
     *              down widget passed
     */
    public boolean isValid(EditText editText, MyPredicate predicate, String errMsg, boolean required) {
        if (required && !hasText(editText))
            return false;

        if (!predicate.test()) {
            editText.setError(errMsg);
            return false;
        }
        return true;
    }

    /**
     *
     * @param editText: Widget for be validated
     * @return boolean: If true: the field has been filled
     *                      false: the field is blank
     */
    public boolean isValid(EditText editText) {
        return hasText(editText);
    }

    /**
     *
     * @param editText: Widget that is the password field
     * @return boolean: If true: The password is valid
     *                      false: The password is invalid,
     *                      and an error message is showed down widget passed
     */
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
