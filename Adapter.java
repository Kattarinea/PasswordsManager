package com.example.passwordsmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    // variable for our array list and context
    private ArrayList<Service> serviceArrayList;
    private Context context;
    static String ReturnedPassword;
    private Base dbHandler;
    // constructor
    public Adapter(ArrayList<Service> servlArrayList, Context context) {
        this.serviceArrayList = servlArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_service, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        Service modal = serviceArrayList.get(position);
        holder.txtURL.setText(modal.getService());
        holder.txtLogin.setText(modal.getLogin());
        int len = modal.getPassword().length();
        StringBuffer  pass = new StringBuffer ();
        for(int i=0;i<len;i++)
            pass.append("*");

        holder.txtPass.setText(pass.toString());
        holder.txtNote.setText(modal.getNotes());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
       /* holder.txtURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.custom_toast, null);
                TextView text = layout.findViewById(R.id.OpenPass);
                text.setText(ReturnedPassword);

                Toast toast = new Toast(context.getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 50);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                Intent intent = new Intent(context.getApplicationContext(), AddNewResource.class);
                intent.putExtra("change","changeItem");
                intent.putExtra("Service",holder.txtURL.getText().toString());
                intent.putExtra("Login",holder.txtLogin.getText().toString());
                intent.putExtra("Pass",serviceArrayList.get(position).getPassword());
                intent.putExtra("Note",holder.txtNote.getText().toString());
                context.startActivity(intent);
            }

        });*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), AddNewResource.class);
                intent.putExtra("change","changeItem");
                intent.putExtra("Service",holder.txtURL.getText().toString());
                intent.putExtra("Login",holder.txtLogin.getText().toString());
                intent.putExtra("Pass",serviceArrayList.get(position).getPassword());
                intent.putExtra("Note",holder.txtNote.getText().toString());
                context.startActivity(intent);
            }
        });


        holder.txtPass.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Service modal = serviceArrayList.get(position);
                StringBuffer  passCheck = new StringBuffer ();

                ReturnedPassword = modal.getPassword();
                String str_txt = holder.txtPass.getText().toString();
                for(int i=0;i<ReturnedPassword.length();i++)
                    passCheck.append("*");

                if(str_txt.equals(passCheck.toString())==false)
                {
                    holder.txtPass.setText(passCheck.toString());

                }
                else
                    holder.txtPass.setText(ReturnedPassword);

                return false;
            }
        });


    }



    @Override
    public int getItemCount() {
        // returning the size of our array list
        return serviceArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView txtURL, txtLogin, txtPass, txtNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views
            txtURL = itemView.findViewById(R.id.idService);
            txtLogin = itemView.findViewById(R.id.idLogin);
            txtPass = itemView.findViewById(R.id.idPassword);
            txtNote = itemView.findViewById(R.id.idNotes);
        }
    }
}
