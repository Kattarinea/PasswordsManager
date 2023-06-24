package com.example.passwordsmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNewResource  extends AppCompatActivity {
    Button btnAdd;
    Button btnSave;
    TextView txtService;
    TextView txtNotes;
    TextView txtLogin;
    TextView txtLabel;
    TextView txtPassword;
    private Base dbHandler;
    private Base dbHandlerUpdate;
    String oldName = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_res);

        btnAdd = findViewById(R.id.button);
        btnSave = findViewById(R.id.buttonSave);
        txtService = findViewById(R.id.URL);
        txtNotes = findViewById(R.id.Notes);
        txtLogin = findViewById(R.id.Login);
        txtPassword = findViewById(R.id.Password);
        txtLabel = findViewById(R.id.Label);
        txtPassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", txtPassword.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(AddNewResource.this, "Copied", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Bundle arg = getIntent().getExtras();
        if(arg!=null) {
            String arg_name = arg.get("change").toString();
            if (arg_name == "changeItem");
            {
                dbHandlerUpdate = new Base(AddNewResource.this);
                oldName = arg.get("Service").toString();
                txtService.setText(arg.get("Service").toString());
                txtLogin.setText(arg.get("Login").toString());
                txtPassword.setText(arg.get("Pass").toString());
                txtNotes.setText(arg.get("Note").toString());
                btnSave.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                txtLabel.setText("Update the record");
            }

        }
        else
        {
            btnSave.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
            txtLabel.setText("Add new service");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldName!=null) {
                    dbHandlerUpdate.updateService(oldName, txtService.getText().toString(), txtLogin.getText().toString(), txtPassword.getText().toString(), txtNotes.getText().toString());
                    Toast.makeText(AddNewResource.this, "Service was updated", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddNewResource.this, passcode_activity.class);
                    startActivity(intent);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbHandler = new Base(AddNewResource.this);

                if(dbHandler.CheckIsDataAlreadyInDBorNot(txtService.getText().toString()))
                    Toast.makeText(AddNewResource.this, "You already have record with such name of service. Ypu can only Update this record.", Toast.LENGTH_SHORT).show();
                else {
                    dbHandler.addNewService(txtService.getText().toString(), txtLogin.getText().toString(), txtPassword.getText().toString(), txtNotes.getText().toString());
                    Toast.makeText(AddNewResource.this, "New Service was added", Toast.LENGTH_SHORT).show();
                }
                Intent intent= new Intent(AddNewResource.this, passcode_activity.class);
                startActivity(intent);
            }
        });
    }
}
