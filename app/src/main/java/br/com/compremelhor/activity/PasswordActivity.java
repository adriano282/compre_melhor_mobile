package br.com.compremelhor.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.RequestAsync;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.UserResource;
import br.com.compremelhor.dao.impl.DAOUser;
import br.com.compremelhor.form.validator.ActionTextWatcher;
import br.com.compremelhor.form.validator.ValidatorTextWatcher;
import br.com.compremelhor.util.function.MyConsumer;
import br.com.compremelhor.util.function.MyFunction;
import br.com.compremelhor.util.function.MyPredicate;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class PasswordActivity extends AppCompatActivity implements OnClickListener {
    private Button btnChangePassword;
    private Button btnCancelOperation;

    private EditText etRepeatPassword;
    private EditText etNewPassword;
    private EditText etOldPassword;

    private TextView tvOldPassword;
    private TextView tvNewPassword;
    private TextView tvRepeatPassword;

    private int userId;

    private DAOUser daoUser;

    private SharedPreferences preferences;

    private Toolbar myToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        daoUser = DAOUser.getInstance(this);
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        userId = preferences.getInt(SP_USER_ID, 0);

        setToolbar();
        setWidgets();
        registerViews();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.btn_change_password:
                if (matcherPasswordOnDatabase() && updatePassword()) {
                    Toast.makeText(this, R.string.password_updated_successful_message, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    etOldPassword.setText(""); etOldPassword.requestFocus();
                    etOldPassword.setError(getString(R.string.err_wrong_password));
                }
                break;

            case R.id.btn_cancel:
                Toast.makeText(this, R.string.btn_change_password_cancel_operation, Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean matcherPasswordOnDatabase() {
        User user = daoUser.find(userId);
        String password = etOldPassword.getText().toString();
        return user.isLoggedByFacebook() ||
                user.getPassword() == null
                || user.getPassword().equals(password);
    }

    private boolean updatePassword() {
        User user = daoUser.find(userId);

        String newPassword = etNewPassword.getText().toString();

        user.setPassword(newPassword);
        user.setLoggedByFacebook(false);
        boolean res = daoUser.insertOrUpdate(user) != -1;

        if (res) {
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
        return res;
    }

    private void setWidgets() {
        tvRepeatPassword = (TextView) findViewById(R.id.tv_new_password_confirmation);
        tvNewPassword = (TextView) findViewById(R.id.tv_new_password);
        tvOldPassword = (TextView) findViewById(R.id.tv_old_password);

        etRepeatPassword = (EditText) findViewById(R.id.et_new_password_confirmation);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);

        btnCancelOperation = (Button) findViewById(R.id.btn_cancel);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);
        btnChangePassword.setEnabled(false);

        if (daoUser.find(userId).isLoggedByFacebook()) {
            tvOldPassword.setVisibility(View.GONE);
            etOldPassword.setVisibility(View.GONE);
            tvNewPassword.setText(getString(R.string.edit_text_label_password));
            tvRepeatPassword.setText("Repita a senha");
            btnChangePassword.setText("Salvar Senha");

        } else {
            tvOldPassword.setVisibility(View.VISIBLE);
            etOldPassword.setVisibility(View.VISIBLE);
            tvNewPassword.setText("Nova Senha");
            tvRepeatPassword.setText("Repita a nova Senha");
            btnChangePassword.setText("Alterar Senha");
        }
    }

    private void registerViews() {
        final MyPredicate isDivergentPasswords = new MyPredicate() {
            public boolean test() {
                String password = etNewPassword.getText().toString();
                String confirmation = etRepeatPassword.getText().toString();
                return password.isEmpty() || confirmation.isEmpty() || password.equals(confirmation);
            }
        };

        etNewPassword.addTextChangedListener(new ValidatorTextWatcher(this, etNewPassword, getString(R.string.err_differents_password), isDivergentPasswords, true));
        etRepeatPassword.addTextChangedListener(new ValidatorTextWatcher(this, etRepeatPassword, getString(R.string.err_differents_password), isDivergentPasswords, true));

        MyPredicate predicate = new MyPredicate() {
            public boolean test() {
                return !etNewPassword.getText().toString().isEmpty()
                        && !etRepeatPassword.getText().toString().isEmpty() && isDivergentPasswords.test();
            }
        };

        MyConsumer<MyPredicate> consumer = new MyConsumer<MyPredicate>() {
            public void accept(MyPredicate myPredicate) {
                btnChangePassword.setEnabled(myPredicate.test());
            }
        };

        etNewPassword.addTextChangedListener(new ActionTextWatcher(consumer, predicate));
        etRepeatPassword.addTextChangedListener(new ActionTextWatcher(consumer, predicate));

        btnChangePassword.setOnClickListener(this);
        btnCancelOperation.setOnClickListener(this);
    }

    private void setToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}