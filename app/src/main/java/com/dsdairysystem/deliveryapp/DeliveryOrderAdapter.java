package com.dsdairysystem.deliveryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.ClientOrdersViewHolder>{
    ArrayList<DeliveryOrder> arrayList;
    Context context;
    ProgressDialog progressDialog;

    public DeliveryOrderAdapter(ArrayList<DeliveryOrder> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClientOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeliveryOrderAdapter.ClientOrdersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_order, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientOrdersViewHolder holder, int position) {
        holder.populate(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ClientOrdersViewHolder extends RecyclerView.ViewHolder
    {
        TextView date,quantity;
        LinearLayout linearLayout;
        public ClientOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            quantity=itemView.findViewById(R.id.quantity);
            linearLayout=itemView.findViewById(R.id.linear_layout);
        }
        void populate(final DeliveryOrder order)
        {
            date.setText(context.getResources().getString(R.string.date)+" :"+order.getDate());
            Map<String,Object> map;
            map=order.getMap();
            quantity.setText("");
            for(Map.Entry<String,Object> entry : map.entrySet())
            {
                quantity.append(entry.getKey()+" : "+entry.getValue());
                quantity.append("\n");
            }

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    progressDialog = new ProgressDialog(view.getContext());
                    progressDialog.setTitle(view.getContext().getResources().getString(R.string.order_details));
                    progressDialog.setMessage(view.getContext().getResources().getString(R.string.updating_please_wait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);


                    Intent intent=new Intent(context, DeliveryDetailedOrder.class);
                    intent.putExtra("ID",order.getID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);

                }
            });

        }
    }
}
