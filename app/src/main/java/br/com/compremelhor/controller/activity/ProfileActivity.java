package br.com.compremelhor.controller.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import static br.com.compremelhor.useful.Constants.*;

public class ProfileActivity extends AppCompatActivity implements OnClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private SharedPreferences preferences;
    private Long id;
    private final int change_password_id = 0;

    private Button btnSave;
    private Button btnUndone;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        setToolbar();
        setWidgets();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.profile_btn_save:
                DAOUser dao = new DAOUser(this);

                Long result = dao.insertOrUpdate(getUserView());

                SharedPreferences.Editor edit = preferences.edit();
                edit.putLong(USER_ID, result != -1 ? result : 0);
                edit.commit();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, change_password_id, 0, R.string.change_password)
                .setIcon(R.drawable.lock)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case change_password_id:
                startActivity(new Intent(this, PasswordActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
    }

    private void setWidgets() {
        edName = (EditText) findViewById(R.id.profile_name);
        edEmail = (EditText) findViewById(R.id.profile_email);
        edDocument = (EditText) findViewById(R.id.profile_document);

        btnSave = (Button) findViewById(R.id.profile_btn_save);
        btnUndone = (Button) findViewById(R.id.profile_btn_undone);

        rbCnpj = (RadioButton) findViewById(R.id.profile_rbCnpj);
        rbCpf = (RadioButton) findViewById(R.id.profile_rbCpf);

        rdGroup = (RadioGroup) findViewById(R.id.profile_rd_group);

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
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        id = preferences.getLong(USER_ID, 0);
        User user = new DAOUser(this).getUserById(id);
        if (user != null) {
            id = user.getId() == null ? 0 : user.getId();
            edName.setText(user.getName());
            edEmail.setText(user.getEmail());
            edDocument.setText(user.getDocument());

            if (user.getTypeDocument() != null) {
                rbCnpj.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CNPJ.toString().toLowerCase()));
                rbCpf.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CPF.toString().toLowerCase()));
            }
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
