package br.com.compremelhor.activity;

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

import com.facebook.login.widget.ProfilePictureView;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.RequestAsync;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.UserResource;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.function.MyFunction;
import br.com.compremelhor.model.TypeDocument;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.SP_FACEBOOK_USER_ID;
import static br.com.compremelhor.useful.Constants.SP_USER_ID;

public class ProfileActivity extends AppCompatActivity implements OnClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private SharedPreferences preferences;
    private int id;
    private final int change_password_id = 0;

    private Button btnSave;
    private Button btnUndone;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;
    private DAOUser dao;

    ProfilePictureView profilePictureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        id = preferences.getInt(SP_USER_ID, 0);

        dao = new DAOUser(this);
        setToolbar();
        setWidgets();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.profile_btn_save:

                User user = getUserView();

                if (!user.getDocument().isEmpty() && user.getTypeDocument() == null ) {
                    rbCnpj.setError("!?");
                    rbCpf.setError("!?");
                    break;
                } else if (user.getDocument().isEmpty() && user.getTypeDocument() != null) {
                    edDocument.setError(getString(R.string.user_document_is_null_message_error));
                    edDocument.requestFocus();
                    break;
                }

                int result = (int) dao.insertOrUpdate(getUserView());
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);

                if (result == -1) {
                    showMessage("Seus dados nao foram salvos.");
                } else {
                    showMessage("Seus dados foram salvos com sucesso!");

                    final UserResource resource = new UserResource(this);
                    if (resource.isConnectedOnInternet()) {
                        MyFunction<User, ResponseServer<User>> function = new MyFunction<User, ResponseServer<User>>() {
                            @Override
                            public ResponseServer<User> apply(User user) {
                                return resource.updateResource(user);
                            }
                        };
                        RequestAsync<User, ResponseServer<User>> requestAsync = new RequestAsync<>(function);
                        requestAsync.execute(user);
                    }
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

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

        profilePictureView = (ProfilePictureView) findViewById(R.id.image);

        btnUndone.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        fillFields();
    }

    private User getUserView() {
        User user = dao.getUserById(id);

        if (user == null) throw new RuntimeException("User wouldn't be null here");

        user.setName(edName.getText().toString());
        user.setDocument(edDocument.getText().toString());
        user.setEmail(edEmail.getText().toString());

        if (rdGroup.getCheckedRadioButtonId() == R.id.profile_rbCnpj) {
            user.setTypeDocument(TypeDocument.CNPJ.toString());
        } else if (rdGroup.getCheckedRadioButtonId() == R.id.profile_rbCpf) {
            user.setTypeDocument(TypeDocument.CPF.toString());
        }
        return user;
    }

    private void fillFields() {
        id = preferences.getInt(SP_USER_ID, 0);
        User user = new DAOUser(this).getUserById(id);

        if (user == null) return;

        String facebookId = preferences.getString(SP_FACEBOOK_USER_ID, "");
        profilePictureView.setProfileId(facebookId);

        id = user.getId();
        edName.setText(user.getName());
        edEmail.setText(user.getEmail());
        edDocument.setText(user.getDocument());

        if (user.getTypeDocument() != null) {
            rbCnpj.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CNPJ.toString().toLowerCase()));
            rbCpf.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CPF.toString().toLowerCase()));
        }

        if (user.isLoggedByFacebook()) {
            Toast.makeText(this,
                    "Você está logado por meio do Facebook. Complete seu cadastro e cadastre uma " +
                            "senha para que você consiga acessar sua conta mesmo OFFLINE", Toast.LENGTH_LONG).show();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
