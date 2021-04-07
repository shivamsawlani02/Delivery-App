package com.dsdairysystem.deliveryapp.route_tab;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder> {

    List<String> list,listNumbers;
    Context context;
    String groupName;

    public CustomerListAdapter(List<String> list, List<String> listNumbers, Context context, String groupName) {
        this.list = list;
        this.listNumbers = listNumbers;
        this.context = context;
        this.groupName = groupName;
    }


    @NonNull
    @Override
    public CustomerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerListAdapter.CustomerListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customersociety_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CustomerListViewHolder holder, int position) {
        holder.populate(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomerListViewHolder  extends RecyclerView.ViewHolder{
        TextView textView;
        RelativeLayout relativeLayout;
        public CustomerListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tv1);
            relativeLayout=itemView.findViewById(R.id.list_item);

        }

        public void populate(String s) {
            textView.setText(s);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,CustomerDetail.class);
                    intent.putExtra("number",listNumbers.get(getAdapterPosition()));
                    intent.putExtra("groupname",groupName);
                    context.startActivity(intent);
                }
            });
        }
    }
}
