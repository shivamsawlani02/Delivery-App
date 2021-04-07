package com.dsdairysystem.deliveryapp.query;

import android.content.Context;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class QueryProductAdapter extends RecyclerView.Adapter<QueryProductAdapter.ProductVH>{
    ArrayList<QueryProduct> arrayList;
    Context context;
    FirebaseFirestore db;

    public QueryProductAdapter(ArrayList<QueryProduct> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QueryProductAdapter.ProductVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
            holder.populate(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ProductVH extends RecyclerView.ViewHolder{
        TextView quality;
        EditText quantity;
        public ImageView delete;
        public ProductVH(@NonNull View itemView) {
            super(itemView);
            quality=itemView.findViewById(R.id.tv_quality);
            quantity=itemView.findViewById(R.id.ed_quantity);
            delete=itemView.findViewById(R.id.delete);


        }
        void populate(final QueryProduct product)
        {
            db=FirebaseFirestore.getInstance();
            quality.setText(product.getQuality());
            quantity.setText(product.getQuantity());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ORIGINAL ARRAY SIZE:",arrayList.size()+"");
                    final EditOrder editOrder=new EditOrder();
                    db.collection("Delivery").document(editOrder.milkmanMobile).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    ArrayList<Map<String,Object>> productList= (ArrayList<Map<String, Object>>) documentSnapshot.get("Products");
                                    for (int i=0;i<productList.size();i++)
                                    {
                                        if((productList.get(i).get("Quality")+"").equalsIgnoreCase(quality.getText().toString()))
                                        {
                                            Log.d("QUALITY TEXT VIEW",quality.getText().toString());
                                            Log.d("QUALITY",productList.get(i).get("Quality")+"");
                                            long price= (long)productList.get(i).get("Price");
                                            Log.d("PRICE",price+"");
                                            long amt =Long.parseLong(editOrder.amount.getText().toString()) - (Integer.parseInt(quantity.getText().toString())*price);
                                            editOrder.amount.setText("Amount :"+amt+"");
                                            break;
                                        }
                                    }
                                }
                            });

//                    int pos=editOrder.qualityAdapter.getPosition(quality.getText().toString());
//                    editOrder.amt =editOrder.amt - Integer.parseInt(editOrder.amountAdapter.getItem(pos))*Integer.parseInt(quantity.getText().toString());
//                    editOrder.amount.setText(editOrder.amt);
                    arrayList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),arrayList.size());
                    Log.d("NEW ARRAY SIZE:",arrayList.size()+"");
                }
            });
        }
    }
}
