package com.dsdairysystem.deliveryapp.add_client;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;

public class AddClientProductAdapter extends RecyclerView.Adapter<AddClientProductAdapter.ProductVH>{
    ArrayList<ProductModel> arrayList;
    Context context;

    public AddClientProductAdapter(ArrayList<ProductModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddClientProductAdapter.ProductVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        holder.populate(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProductVH extends RecyclerView.ViewHolder {
            TextView productName;
            EditText quantity;
            ImageView add;
        public ProductVH(@NonNull View itemView) {
            super(itemView);
            productName=itemView.findViewById(R.id.product_name);
            quantity=itemView.findViewById(R.id.quantity);
            add=itemView.findViewById(R.id.add);
        }
        void populate(final ProductModel productModel) {
            productName.setText(productModel.getProductName());
            quantity.setText(productModel.getQuantity());

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(String.valueOf(add.getTag()).equals("add"))
                     {
                        quantity.setVisibility(View.VISIBLE);
                        quantity.setText("1");
                        add.setImageResource(R.drawable.ic_close);
                        add.setTag("cancel");
//                        add.setBackgroundResource(R.drawable.circle);
                    }
                    else
                    {
                        add.setImageResource(R.drawable.ic_add);
                        add.setBackgroundResource(R.drawable.circle);
                        quantity.setText("0");
                        add.setTag("add");
                        quantity.setVisibility(View.INVISIBLE);
                    }
                }
            });
            quantity.addTextChangedListener(filterTextWatcher);

        }
        private TextWatcher filterTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("CHANGED TEXT",charSequence+"");
                arrayList.get(getAdapterPosition()).setQuantity(charSequence+"");
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }
}
