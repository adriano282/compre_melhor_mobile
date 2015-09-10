package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import br.com.compremelhor.R;

/**
 * Created by adriano on 10/09/15.
 */
public class PasswordActivity extends Activity {
    private Button btnChancePassword;
    private Button btnCancelOperation;

    private EditText etRepeatPassword;
    private EditText etNewPassword;
    private EditText etOldPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        setWidgets();
    }

    private void setWidgets() {
        btnCancelOperation = (Button) findViewById(R.id.btn_cancel);
        btnChancePassword = (Button) findViewById(R.id.btn_change_password);

        etRepeatPassword = (EditText) findViewById(R.id.et_new_password_confirmation);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
    }

}
