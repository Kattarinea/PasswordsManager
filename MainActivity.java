package com.example.passwordsmanager;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.hanks.passcodeview.PasscodeView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    PasscodeView passcodeView;
    static String pass;
    String encr_pass;
    String pass_old;
    static boolean refresh = true;
    String pathPass = "/storage/emulated/0/Download/password.txt";
    File passFile = new File(pathPass);
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Environment.isExternalStorageManager()) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(permissionIntent);
        }
        passcodeView = findViewById(R.id.passcodeview);

        Bundle arg = getIntent().getExtras();
        if(arg!=null) {
            String arg_name = arg.get("menuItem").toString();
            if (arg_name == "PIN");

            passcodeView = findViewById(R.id.passcodeview);
            pass_old=pass;

            try {
                checkNewPassword(true);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }

        }
        else {
            try {
                checkNewPassword(false);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    void checkNewPassword(boolean flag) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        if(!passFile.exists() || flag==true) {
            if(!passFile.exists()) {
                passFile.createNewFile();
            }
            passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail(String wrongNumber) {
                    Toast.makeText(MainActivity.this, wrongNumber, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    Intent intent_passcode = new Intent(MainActivity.this, passcode_activity.class);
                    new SecureRandom().nextBytes(Crypt.ivByte);
                    if(pass_old!=null) {
                        intent_passcode.putExtra("Pass", pass_old);

                    }
                    else
                        System.arraycopy(Crypt.ivByteOld,0,Crypt.ivByte,0,8);

                    refresh = true;
                    pass = number;

                    try {
                        clearTheFile(passFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Crypt crypt = new Crypt("heL13@w0");
                    try {
                        encr_pass=crypt.EncryptPass(pass);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }

                    try {
                        WriteToFile(passFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    startActivity(intent_passcode);

                }
            });
        }
        else {
            ReadFile(passFile);
            passcodeView.setPasscodeLength(8).setLocalPasscode(pass).setIsAutoClear(true).setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail(String wrongNumber) {
                    Toast.makeText(MainActivity.this, wrongNumber, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    Intent intent_passcode = new Intent(MainActivity.this, passcode_activity.class);
                    refresh=true;
                    startActivity(intent_passcode);
                }
            });
        }
    }

    void WriteToFile(File test) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(test,true));
        writer.write(encr_pass);
        writer.close();
    }

    public static void clearTheFile(File test) throws IOException {
        FileWriter fwOb = new FileWriter(test, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }

    void ReadFile(File test) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FileInputStream inS = new FileInputStream(test);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inS));
        encr_pass= bufferedReader.readLine();
        Crypt crypt = new Crypt("heL13@w0");
        pass=crypt.DecryptPass(encr_pass);

    }

}

