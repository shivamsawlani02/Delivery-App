package com.dsdairysystem.deliveryapp.route_tab;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;
import java.util.List;

public class CustomerSocietyAdapter extends RecyclerView.Adapter<CustomerSocietyAdapter.CustomerSocietyViewHolder> {


    List<String> list ;
    Context context;

    public CustomerSocietyAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomerSocietyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerSocietyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customersociety_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerSocietyViewHolder holder, int position) {


        holder.populate(list.get(position));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class CustomerSocietyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        RelativeLayout relativeLayout;

        public CustomerSocietyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv1);
            relativeLayout = itemView.findViewById(R.id.list_item);


        }


        public void populate(String s) {
            tv.setText(s);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CustomerList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("groupname", tv.getText().toString());
                    context.startActivity(intent);

                    Log.i("STATE", String.valueOf(getAdapterPosition()));

                }
            });


        }
    }
}
