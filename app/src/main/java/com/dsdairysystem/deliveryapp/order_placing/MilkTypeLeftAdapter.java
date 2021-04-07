package com.dsdairysystem.deliveryapp.order_placing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;
import java.util.Map;

public class MilkTypeLeftAdapter extends RecyclerView.Adapter<MilkTypeLeftAdapter.ViewHolder> {
    private Map<String,Long> map;
    private ArrayList<String> milk_types = new ArrayList<>();
    private ArrayList<Long> milk_Quantities = new ArrayList<>();

    public MilkTypeLeftAdapter(Map<String, Long> map) {
        this.map = map;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.milk_type_left_layout, parent, false);
        return new MilkTypeLeftAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        milk_types.addAll(map.keySet());
        milk_Quantities.addAll(map.values());

        Map<String,Long> initialMap = Index.getTotalMilkTypeAvailability();
        long initialTotalMilk = initialMap.get(milk_types.get(position));

        holder.tvMilkLeft.setText(Long.toString(milk_Quantities.get(position)));
        holder.tvMilkTypeName.setText(milk_types.get(position));

        long currentMilk = (map.get(milk_types.get(position)));
        double percentage_left = (((double)currentMilk/initialTotalMilk)*100.0);
        double l = percentage_left/20.0;

        switch ((int) l) {
            case 5 :
                holder.ivMilkTypeLeft.setImageResource(R.drawable.milk_left_100);
                break;
            case 4 :
                holder.ivMilkTypeLeft.setImageResource(R.drawable.milk_left_80);
                break;
            case 3 :
                holder.ivMilkTypeLeft.setImageResource(R.drawable.milk_left_60);
                break;
            case 2 :
                holder.ivMilkTypeLeft.setImageResource(R.drawable.milk_left_40);
                break;
            default :
                holder.ivMilkTypeLeft.setImageResource(R.drawable.milk_left_20);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMilkTypeName, tvMilkLeft;
        ImageView ivMilkTypeLeft;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMilkTypeName = itemView.findViewById(R.id.tvMilkTypeName);
            tvMilkLeft = itemView.findViewById(R.id.tvMilkLeft);
            ivMilkTypeLeft = itemView.findViewById(R.id.ivMilkTypeLeft);
        }
    }


}
