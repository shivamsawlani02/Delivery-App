package com.dsdairysystem.deliveryapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.order_placing.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Map> product_list;
    private TextInputEditText etName,etQuality,etAmount;
    //private String user_id="+919988776655";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ProgressDialog progressDialog;


    public ProductAdapter(ArrayList<Map> product_list) {
        this.product_list = product_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        return new ProductAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Map<String,Object> map = (Map<String, Object>) product_list.get(position);
        final String product_name = (String) map.get("Name");
        final String product_quality = (String) map.get("Quality");
        final Long product_price = (Long) map.get("Price");

        holder.tvProductPrice.setText(Long.toString(product_price));
        holder.tvProductName.setText(product_name);
        holder.tvProductQuality.setText(product_quality);

        if (Index.getProductEditable()){
            holder.fabProductDelete.setVisibility(View.VISIBLE);
            holder.fabProductEdit.setVisibility(View.VISIBLE);
        }else{
            holder.fabProductDelete.setVisibility(View.GONE);
            holder.fabProductEdit.setVisibility(View.GONE);
        }

        holder.fabProductDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                progressDialog = new ProgressDialog(v.getContext());
                                progressDialog.setTitle(v.getContext().getResources().getString(R.string.product_deleted));
                                progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                final Map<String,Object> product = new HashMap<>();
                                product.put("Price",product_price);
                                product.put("Quality",product_quality);

                                FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Products", FieldValue.arrayRemove(product)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(v.getContext(), DeliveryProfileActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.product_deleted),Toast.LENGTH_SHORT).show();
                                                    v.getContext().startActivity(intent);

                                                }
                                            }, 2000);
                                        }
                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                        }


                    }
                };
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                builder.setMessage(v.getContext().getResources().getString(R.string.are_you_sure_to_delete)).setPositiveButton(v.getContext().getResources().getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(v.getContext().getResources().getString(R.string.no), dialogClickListener).show();


            }
        });

        holder.fabProductEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view =  LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.add_order_dialog, null);
                builder.setView(view)
                        .setNegativeButton(v.getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton(v.getContext().getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final long amount = Long.parseLong(etAmount.getText().toString());
                                final String quality= etQuality.getText().toString();

                                if ( quality==product_quality || amount==product_price){}
else {
                                    progressDialog = new ProgressDialog(v.getContext());
                                    progressDialog.setTitle(v.getContext().getResources().getString(R.string.edit_product_details));
                                    progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    final Map<String,Object> product = new HashMap<>();
                                    product.put("Price",product_price);
                                    product.put("Quality",product_quality);



                                    FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Products", FieldValue.arrayRemove(product)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                final Map<String,Object> update_product = new HashMap<>();
                                                update_product.put("Price",amount);
                                                update_product.put("Quality",quality);

                                                FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Products", FieldValue.arrayUnion(update_product)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                public void run() {
                                                                    progressDialog.dismiss();
                                                                    Intent intent = new Intent(v.getContext(), DeliveryProfileActivity.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.changes_saved),Toast.LENGTH_SHORT).show();
                                                                    v.getContext().startActivity(intent);

                                                                }
                                                            }, 2000);
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                    });

                                }
                            }
                        });
                etQuality = view.findViewById(R.id.tvEditQuality);
                etAmount = view.findViewById(R.id.tvEditAmount);
                etAmount.setText(Long.toString(product_price));
                etQuality.setText(product_quality);
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName,tvProductPrice,tvProductQuality;
        ExtendedFloatingActionButton fabProductEdit,fabProductDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice=itemView.findViewById(R.id.tvProductPrice);
            fabProductEdit=itemView.findViewById(R.id.fabEditProduct);
            tvProductQuality=itemView.findViewById(R.id.tvProductQuality);
            fabProductDelete=itemView.findViewById(R.id.fabDeleteProduct);
        }
    }
}
