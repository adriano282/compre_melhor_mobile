package br.com.compremelhor.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import br.com.compremelhor.R;

/**
 * Created by adriano on 10/09/15.
 */
public class PasswordActivity extends AppCompatActivity implements OnClickListener {
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

    @Override
    public void onClick(View view) {

    }

    private void setWidgets() {
        etRepeatPassword = (EditText) findViewById(R.id.et_new_password_confirmation);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);

        btnCancelOperation = (Button) findViewById(R.id.btn_cancel);
        btnChancePassword = (Button) findViewById(R.id.btn_change_password);

        btnChancePassword.setOnClickListener(this);
        btnCancelOperation.setOnClickListener(this);
    }
}
