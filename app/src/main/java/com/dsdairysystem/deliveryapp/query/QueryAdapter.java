package com.dsdairysystem.deliveryapp.query;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.QueryVH> {

    ArrayList<Query> arrayList;
    Context context;

    public QueryAdapter(ArrayList<Query> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public QueryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QueryVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QueryVH holder, int position) {
        holder.populate(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class QueryVH extends RecyclerView.ViewHolder {
        TextView description, clientName;
        LinearLayout linearLayout;

        public QueryVH(@NonNull View itemView) {
            super(itemView);
            //date = itemView.findViewById(R.id.tv_date);
            description = itemView.findViewById(R.id.tv_query_description);
            clientName = itemView.findViewById(R.id.tv_client_name);
            linearLayout=itemView.findViewById(R.id.linear_layout);

        }

        void populate(final Query query) {
           // date.setText("Date of Order : " + query.getDate());
            clientName.setText("Query raised by " + query.getClientName());
            description.setText(query.getDescription());

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,EditOrder.class);
                    intent.putExtra("clientMobile",query.getClientMobile());
                    intent.putExtra("orderID",query.getOrderID());
                    intent.putExtra("documentID",query.getDocumentID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
    }
}
