package com.dsdairysystem.deliveryapp.order_placing;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;

import static com.dsdairysystem.deliveryapp.order_placing.ClientAdapter.extTypeViewHolder.amount;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Long> quantity_list;
    private ArrayList<String> quality_list;
    private ArrayList<String> quality_names;
    private ArrayAdapter productAdapter;
    private ArrayList<String> product_list;
    private Context context;
    private boolean mSpinnerInitialized;

    public OrderAdapter(ArrayList<Long> quantity_list, ArrayList<String> quality_list, Context context) {
        this.quantity_list = quantity_list;
        this.quality_list = quality_list;
        this.context = context;
        this.quality_names = quality_list;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new OrderAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.ViewHolder holder, final int position) {
        mSpinnerInitialized=false;
        String milkType = quality_list.get(position);

        holder.quality.setText(milkType);

        holder.quantity.setText(Long.toString(quantity_list.get(position)));

        product_list = Index.getProduct_list();

        productAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,product_list);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinner.setAdapter(productAdapter);

        if (!quality_list.get(position).equals("Add Product")){
            holder.spinner.setSelection(productAdapter.getPosition(quality_list.get(position)));
        }else {
            quality_list.remove(position);
            quality_list.add(position,(String) holder.spinner.getSelectedItem());
        }

        productAdapter.notifyDataSetChanged();

        holder.spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSpinnerInitialized=true;
                return false;
            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                if (mSpinnerInitialized) {
                    if (Index.getTotal_amount()!=null ){
                        long l = Index.getTotal_amount();
                        String s = quality_list.get(position);
                        String s1 = (String) holder.spinner.getSelectedItem();
                        Index.setTotal_amount(l-((Index.getProduct_detail_map().get(s)-Index.getProduct_detail_map().get(s1))*quantity_list.get(position) ));
                        amount.findViewById(R.id.tvAmount);
                        amount.setText(Index.getTotal_amount().toString());
                        quality_list.remove(position);
                        quality_list.add(position,(String) holder.spinner.getSelectedItem());
                        mSpinnerInitialized=false;
                        notifyItemChanged(position);
                    }
                    mSpinnerInitialized = false;
                }
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.btIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long s = quantity_list.get(position);

                if ((Index.getCurrentMilkTypeAvailability().get(quality_list.get(position)) != null) && ((s + 1l) <= Index.getCurrentMilkTypeAvailability().get(quality_list.get(position)))){
                    if (Index.getTotal_amount() == null || Index.getTotal_amount() == 0l) {
                        long aLong = Index.getProduct_detail_map().get((String) holder.spinner.getSelectedItem());
                        Index.setTotal_amount((aLong));
                    } else
                        Index.setTotal_amount(Index.getTotal_amount() + (Index.getProduct_detail_map().get((String) holder.spinner.getSelectedItem())));
                amount.findViewById(R.id.tvAmount);
                Long l = Index.getTotal_amount();
                amount.setText(l.toString());
                quantity_list.remove(position);
                quantity_list.add(position, s + 1l);
                holder.quantity.setText(quantity_list.get(position).toString());
                notifyItemChanged(position);
            } else Toast.makeText(context,quality_list.get(position)+context.getResources().getString(R.string.out_of_stock),Toast.LENGTH_SHORT).show();
            }
        });

        holder.btDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long s = quantity_list.get(position);

                if (s==0){
                   // deleteItem(position,v);
                }else {
                    if (Index.getTotal_amount()==null){
                        Index.setTotal_amount((Index.getProduct_detail_map().get((String) holder.spinner.getSelectedItem())));
                    }else {
                        Index.setTotal_amount(Index.getTotal_amount()-(Index.getProduct_detail_map().get((String) holder.spinner.getSelectedItem())));}
                    amount.findViewById(R.id.tvAmount);
                    amount.setText(Index.getTotal_amount().toString());
                    quantity_list.remove(position);
                    quantity_list.add(position, s - 1l);
                    holder.quantity.setText(quantity_list.get(position).toString());
                    notifyItemChanged(position);
                }
            }
        });

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                deleteProduct(position);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setMessage(context.getResources().getString(R.string.are_you_sure_to_delete)).setPositiveButton(context.getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.no), dialogClickListener).show();

            }
        });

 }


    private void deleteProduct(int position) {
        getItemCount();
        if (Index.getTotal_amount() != null) {
            Index.setTotal_amount(Index.getTotal_amount() - ((Index.getProduct_detail_map().get(quality_list.get(position))) * quantity_list.get(position)));
            amount.findViewById(R.id.tvAmount);
            amount.setText(Index.getTotal_amount().toString());
        }
        quantity_list.remove(position);
        quality_list.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return quality_list.size();
    }

    public ArrayList<String> getModifyQualityList() {
        return quality_list;
    }

    public ArrayList<Long> getModifyQuantityList() {
        return quantity_list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView quality,quantity;
        Spinner spinner;
        ImageButton btIncrease, btDecrease,btDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            quality=itemView.findViewById(R.id.textView6);
            quantity=itemView.findViewById(R.id.textView9);
            spinner=itemView.findViewById(R.id.spinner);
            btDecrease=itemView.findViewById(R.id.btDecrease);
            btIncrease=itemView.findViewById(R.id.btIncrease);
            btDelete=itemView.findViewById(R.id.btDelete);
        }
    }

}
