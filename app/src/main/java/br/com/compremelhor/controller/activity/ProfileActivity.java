package br.com.compremelhor.controller.activity;

/**
 * Created by adriano on 05/09/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import br.com.compremelhor.R;

public class ProfileActivity extends Activity
        implements View.OnClickListener, OnItemClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private Button btnSave;
    private Button btnUndone;
    private Button btnChangePassword;
    private Button btnAddNewAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        edName = (EditText) findViewById(R.id.profile_name);
        edEmail = (EditText) findViewById(R.id.profile_email);
        edDocument = (EditText) findViewById(R.id.profile_name);

        btnSave = (Button) findViewById(R.id.profile_button_save);
        btnUndone = (Button) findViewById(R.id.profile_button_undone);
        btnChangePassword = (Button) findViewById(R.id.profile_button_change_password);

        btnChangePassword.setOnClickListener(this);
        btnUndone.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view, int position,
                            long id) {

    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {

        }
    }


}
