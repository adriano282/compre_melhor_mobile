package br.com.compremelhor.controller.activity;

/**
 * Created by adriano on 05/09/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.TypeDocument;
import br.com.compremelhor.model.User;
import br.com.compremelhor.useful.Constants;

public class ProfileActivity extends Activity
        implements OnClickListener, Constants{
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private long id;

    private Button btnSave;
    private Button btnUndone;
    private Button btnChangePassword;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        setWidgets();
    }

    private void setWidgets() {
        edName = (EditText) findViewById(R.id.profile_name);
        edEmail = (EditText) findViewById(R.id.profile_email);
        edDocument = (EditText) findViewById(R.id.profile_document);

        btnSave = (Button) findViewById(R.id.profile_btn_save);
        btnUndone = (Button) findViewById(R.id.profile_btn_undone);
        btnChangePassword = (Button) findViewById(R.id.profile_btn_change_password);

        rbCnpj = (RadioButton) findViewById(R.id.profile_rbCnpj);
        rbCpf = (RadioButton) findViewById(R.id.profile_rbCpf);

        rdGroup = (RadioGroup) findViewById(R.id.profile_rd_group);

        btnChangePassword.setOnClickListener(this);
        btnUndone.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        fillFields();
    }

    private User getUserView() {
        User user = new User();
        user.setId(this.id);
        user.setName(edName.getText().toString());
        user.setDocument(edDocument.getText().toString());
        user.setEmail(edEmail.getText().toString());

        if (rdGroup.getCheckedRadioButtonId() == R.id.profile_rbCnpj) {
            user.setTypeDocument(TypeDocument.CNPJ.toString());
        } else {
            user.setTypeDocument(TypeDocument.CPF.toString());
        }

        return user;
    }

    private void fillFields() {

        User user = new DAOUser(this).getUser();
        if (user != null) {
            id = user.getId() == null? 0:user.getId();
            edName.setText(user.getName());
            edEmail.setText(user.getEmail());
            edDocument.setText(user.getDocument());
            rbCnpj.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CNPJ.toString().toLowerCase()));
            rbCpf.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CPF.toString().toLowerCase()));
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.profile_btn_change_password:
                intent = new Intent(this, PasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.profile_btn_save:
                DAOUser dao = new DAOUser(this);

                int result = dao.insertOrUpdate(getUserView());

                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);

                if (result == -1) {
                    showMessage("Seus dados nao foram salvos.");
                } else {
                    showMessage("Seus dados foram salvos com sucesso!");
                }
                break;
        }
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}
