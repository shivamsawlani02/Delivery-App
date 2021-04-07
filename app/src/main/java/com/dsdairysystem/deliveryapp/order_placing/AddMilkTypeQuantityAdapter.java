package com.dsdairysystem.deliveryapp.order_placing;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;


public class AddMilkTypeQuantityAdapter extends RecyclerView.Adapter<AddMilkTypeQuantityAdapter.ViewHolder> {

    private ArrayList<String> milk_types;
    private ArrayList<Long> enteredQuantities = new ArrayList<Long>();

    public AddMilkTypeQuantityAdapter(ArrayList<String> milk_types) {
        this.milk_types = milk_types;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_milk_type_quantity_layout, parent, false);
        return new AddMilkTypeQuantityAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvMilkType.setText(milk_types.get(position));

        if (enteredQuantities.size()<=position || enteredQuantities.get(position) == null){
            enteredQuantities.add(position,0l);
        }

        holder.etEnterQuantity.addTextChangedListener(new TextWatcher() {

            private String ss;
            private long after;
            private Thread t;
            private Runnable runnable_EditTextWatcher = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if ((System.currentTimeMillis() - after) > 200)
                        {
                            Log.d("Debug_EditTEXT_watcher", "(System.currentTimeMillis()-after)>600 ->  " + (System.currentTimeMillis() - after) + " > " + ss);
                            // Do your stuff
                            if (!ss.equals("")){
                                enteredQuantities.remove(position);
                                enteredQuantities.add(position,Long.parseLong(ss));
                            }
                            t = null;
                            break;
                        }
                    }
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ss = s.toString();
                after = System.currentTimeMillis();
                if (t == null)
                {
                    t = new Thread(runnable_EditTextWatcher);
                    t.start();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return milk_types.size();
    }

    public ArrayList<Long> getEnteredQuantities() {
        return enteredQuantities;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMilkType;
        EditText etEnterQuantity;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMilkType = itemView.findViewById(R.id.tvMilkType);
            etEnterQuantity = itemView.findViewById(R.id.etEnterQuantity);

            }
    }

}
