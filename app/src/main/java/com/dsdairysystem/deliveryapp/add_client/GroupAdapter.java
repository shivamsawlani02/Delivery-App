package com.dsdairysystem.deliveryapp.add_client;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysystem.deliveryapp.R;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupVH> {
    ArrayList<GroupModel> arrayList;
    Context context;
    String clientName,clientMobile,clientAddress;


    public GroupAdapter(ArrayList<GroupModel> arrayList, Context context, String clientName, String clientMobile, String clientAddress) {
        this.arrayList = arrayList;
        this.context = context;
        this.clientName=clientName;
        this.clientMobile=clientMobile;
        this.clientAddress=clientAddress;
    }

    @NonNull
    @Override
    public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupVH holder, int position) {
        holder.populate(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class GroupVH extends RecyclerView.ViewHolder {
        TextView groupName;
        ImageView forward;
        public GroupVH(@NonNull View itemView) {
            super(itemView);
            groupName=itemView.findViewById(R.id.group_name);
            forward=itemView.findViewById(R.id.forward);
        }
        void populate(final GroupModel groupModel) {

            groupName.setText(groupModel.getGroupName());
            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,ProductActivity.class);
                    intent.putExtra("name",clientName);
                    intent.putExtra("mobile",clientMobile);
                    intent.putExtra("address",clientAddress);
                    intent.putExtra("groupName",groupModel.getGroupName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }
    }
}
