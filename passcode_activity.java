package com.example.passwordsmanager;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;


public class passcode_activity extends AppCompatActivity {
    Button btnAdd;


    private ArrayList<Service> serviceArrayList;
    private Base dbHandler;
    private Adapter adapter;
    private RecyclerView serviceRV;
    String pathDir = "/storage/emulated/0/Download/cryptBase";
    File Dir = new File(pathDir);
    String pathFile = "/storage/emulated/0/Download/cryptBase/cryptBase.txt";
    File File = new File(pathFile);
    File test = new File("/storage/emulated/0/Download/cryptBase/test.txt");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        Bundle arg = getIntent().getExtras();
        if(arg!=null && MainActivity.refresh!=false )
        {
            byte[] tmp = new byte[8];
            System.arraycopy(tmp,0,Crypt.ivByte,0,8);
            System.arraycopy(Crypt.ivByte,0,Crypt.ivByteOld,0,8);
            Crypt crypt = new Crypt(arg.get("Pass").toString());
            ExportData(crypt,true);
            System.arraycopy(Crypt.ivByte,0,tmp,0,8);
            crypt=null;
            crypt = new Crypt(MainActivity.pass);
            System.arraycopy(Crypt.ivByteOld,0,Crypt.ivByte,0,8);
            ImportData(crypt,true);
            arg=null;

        }
        btnAdd = findViewById(R.id.buttonAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_passcode = new Intent(passcode_activity.this, AddNewResource.class);
                startActivity(intent_passcode);

            }
        });


        serviceArrayList = new ArrayList<>();
        dbHandler = new Base(passcode_activity.this);
        serviceArrayList = dbHandler.readService();
        adapter = new Adapter(serviceArrayList, passcode_activity.this);
        serviceRV = findViewById(R.id.idRVService);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(passcode_activity.this, RecyclerView.VERTICAL, false);
        serviceRV.setLayoutManager(linearLayoutManager);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Toast.makeText(passcode_activity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove swiped item from list and notify the RecyclerView
                AlertDialog.Builder builder = new AlertDialog.Builder(passcode_activity.this);
                builder.setTitle("Delete").setMessage("Are you sure you want to delete this entry?").setIcon(R.drawable.report).setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = viewHolder.getBindingAdapterPosition();
                        dbHandler.deleteService(serviceArrayList.get(position).getService());
                        serviceArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(passcode_activity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(passcode_activity.this, "You've changed your mind so quickly :)", Toast.LENGTH_SHORT).show();
                    }
                });
                adapter.notifyDataSetChanged();
                builder.create();
                builder.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(serviceRV);
        // setting our adapter to recycler view.
        serviceRV.setAdapter(adapter);

    }

////////////////////////////////////////////////////////////////////создание MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       Crypt crypt = new Crypt(MainActivity.pass);
        switch (id) {
            case R.id.importData:
                ImportData(crypt,false);
                return true;
            case R.id.exportData:
                ExportData(crypt, false);
                return true;

            case R.id.changePIN:

                Intent intent = new Intent(passcode_activity.this, MainActivity.class);
                intent.putExtra("menuItem","PIN");
                startActivity(intent);
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }
/////////////////////////////////////////////////////////////////////////////////

    void ImportData(Crypt crypt, boolean flag)
    {
        if(!Dir.exists() && !Dir.isDirectory())
            Dir.mkdirs();
        if(!File.exists()) {
            try {
                File.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                clearTheFile(File);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!flag) {
            try {
                test.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                WriteBaseToFile(test);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(!crypt.EncryptFile(test,File))
            Toast.makeText(passcode_activity.this, "Error!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(passcode_activity.this, "Success!", Toast.LENGTH_SHORT).show();

        test.delete();
    }

boolean ExportData(Crypt crypt, boolean flag)
{
    if(!Dir.exists() && !Dir.isDirectory())
    {
        Toast.makeText(passcode_activity.this, "You do not have /cryptBase directory!", Toast.LENGTH_SHORT).show();
        return false;
    }

    if(!File.exists()) {
        Toast.makeText(passcode_activity.this, "You do not have /cryptBase/cryptBase.txt file!", Toast.LENGTH_SHORT).show();
        return false;
    }

    try {
        test.createNewFile();
    } catch (IOException e) {
        e.printStackTrace();
    }

    if(!crypt.DecryptFile(File,test))
        Toast.makeText(passcode_activity.this, "Error!", Toast.LENGTH_SHORT).show();
    else
        Toast.makeText(passcode_activity.this, "Success!", Toast.LENGTH_SHORT).show();

    try {
        ReadFile(test,flag);
    } catch (IOException e) {
        e.printStackTrace();
    }
if(!flag)
    test.delete();
    return true;
}

void WriteBaseToFile(File test) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(test,true));
    for(int i=0;i<serviceArrayList.size();i++) {
        Service modal = serviceArrayList.get(i);
        writer.write(modal.getService() + "\n" + modal.getLogin()+"\n"+modal.getPassword()+"\n"+modal.getNotes()+"\n");
    }
    writer.close();
}

public static void clearTheFile(File test) throws IOException {
        FileWriter fwOb = new FileWriter(test, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }


void ReadFile(File test, boolean flag) throws IOException {
    FileInputStream inS = new FileInputStream(test);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inS));
    String line;
    int count_line =0;
    int index=0;
    String serv = null;
    String log= null;
    String pass= null;
    String note= null;
    BufferedWriter writer=null;
    if(flag)
         writer = new BufferedWriter(new FileWriter(test,true));
    else
    {
        serviceArrayList.clear();
        dbHandler.deleteAll();
    }
    while ((line=bufferedReader.readLine())!=null)
    {
        if(!flag)
        {count_line++;
        switch (count_line)
        {
            case 1:
               serv = line;
                break;
            case 2:
                log=line;
                break;
            case 3:
                pass=line;
                break;
            case 4:
                note=line;
                count_line=0;
                dbHandler.addNewService(serv,log,pass,note);
                index++;
                break;
        }
        }
        else
        {
            writer.write(line);
        }


    }
    if(!flag) {//пересоздать активити без анимации
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
        MainActivity.refresh=false;
    }
    else
        writer.close();
}



    @Override
    public void onBackPressed() {
        // this method is used to finish the activity
        // when user enters the correct password
        this.finishAffinity();
    }
}