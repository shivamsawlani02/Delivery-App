package com.dsdairysystem.deliveryapp.route_tab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    List<OrderModel> list;

    public OrderListAdapter(List<OrderModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.populate(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView name,date,time,amount,quality,quantity;
        String qualitys,quantitys;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.date_orderlist_item);
            time = itemView.findViewById(R.id.time_orderlist_item);
            amount=itemView.findViewById(R.id.totalamount_orderlist_item);
            quality= itemView.findViewById(R.id.quality_orderlist_item);
            quantity=itemView.findViewById(R.id.quantity_orderlist_item);

        }

        public void populate(OrderModel orderModel) {
            date.setText(orderModel.getDate());
            time.setText(orderModel.getTime());
            amount.setText(String.valueOf(orderModel.getAmount()));

            quantitys="";
            qualitys="";
            int i = 0;
            for (String key : orderModel.getMilk().keySet()){

                if(i!=orderModel.getMilk().keySet().size()-1){
                qualitys+=key+"\n";
                quantitys+=orderModel.getMilk().get(key)+"\n";
                }
                else {
                    qualitys+=key;
                    quantitys+=orderModel.getMilk().get(key);
                }
                i++;
            }



            quality.setText(qualitys);
            quantity.setText(quantitys);

        }
    }
}
