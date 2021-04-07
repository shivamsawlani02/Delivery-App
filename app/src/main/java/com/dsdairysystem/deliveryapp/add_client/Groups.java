package com.dsdairysystem.deliveryapp.add_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityService;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysystem.deliveryapp.R;
import com.dsdairysystem.deliveryapp.order_placing.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class Groups extends AppCompatActivity {
    RecyclerView recyclerView;
    GroupAdapter adapter;
    ArrayList<GroupModel> arrayList;
    FirebaseFirestore db;
    //String milkManMobile = "+919988776655";
    String milkManMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ImageView back;
    Button addGroup;
    String clientName,clientMobile,clientAddress;
    TextView noGroup;
    Dialog customDialog;
    EditText newGroup, priority;
    Button saveButtonDialog, cancel;
    String gn;
    int prio;
    List<String> originalColonies,updatedColonies;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);


        attachID();

        db.collection("Delivery").document(milkManMobile).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> groupList = (List<String>) documentSnapshot.get("Group");

                            if (groupList != null && !groupList.isEmpty()){

                                noGroup.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                Log.d("SIZE OF GROUP LIST",groupList.size()+"");
                                for (int i = 0; i < groupList.size(); i++) {
                                    GroupModel groupModel = new GroupModel(groupList.get(i));
                                    arrayList.add(groupModel);
                                }
                                //arrayList= (ArrayList<GroupModel>) documentSnapshot.get("Group");
                                adapter = new GroupAdapter(arrayList, getApplicationContext(),clientName,clientMobile,clientAddress);
                                recyclerView.setAdapter(adapter);
                            }
                            else {
                                noGroup.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            addGroup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newGrp();
                                }
                            });
                        }
                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Groups.this,AddClient.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void newGrp() {
        customDialog.setContentView(R.layout.custom_dialog);
        newGroup = customDialog.findViewById(R.id.ed_new_group);
        priority = customDialog.findViewById(R.id.ed_priority);
        saveButtonDialog = customDialog.findViewById(R.id.save_btnl);
        cancel = customDialog.findViewById(R.id.btnl_cancel);
        saveButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gn = newGroup.getText().toString();
                int f = checkGroupExists(gn);
                if (gn.isEmpty()) {
                    Toast.makeText(Groups.this, getResources().getString(R.string.enter_group_name), Toast.LENGTH_SHORT).show();
                }
                else if(priority.getText().toString().isEmpty())
                {
                    Toast.makeText(Groups.this, getResources().getString(R.string.enter_group_name), Toast.LENGTH_SHORT).show();
                }
                else if (f == 1) {
                    Toast.makeText(Groups.this, getResources().getString(R.string.group_already_exist), Toast.LENGTH_SHORT).show();
                } else {
                    prio = Integer.parseInt(priority.getText().toString());
                    rearrangeArrayList(gn, prio);
                    customDialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }
    private void rearrangeArrayList(final String newGroup, final int priority) {
        progressDialog = new ProgressDialog(Groups.this);
        progressDialog.setTitle(getResources().getString(R.string.saving_group));
        progressDialog.setMessage(getResources().getString(R.string.updating_please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        originalColonies = new ArrayList<>();
        updatedColonies = new ArrayList<>();
        db.collection("Delivery").document(milkManMobile).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        originalColonies = (ArrayList) task.getResult().get("Group");

                        if (originalColonies == null || (originalColonies !=null && originalColonies.isEmpty())){
                            if (priority==0){
                                Toast.makeText(Groups.this, getResources().getString(R.string.group_no_cannot_zero), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                updatedColonies.add(0,newGroup);
                                db.collection("Delivery").document(milkManMobile).update("Group", updatedColonies).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            PriorityModel pr = new PriorityModel(1);
                                            db.collection("Delivery").document(milkManMobile).collection("Group").document(updatedColonies.get(0))
                                                    .set(pr, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Intent intent = new Intent(Groups.this, Groups.class);
                                                        intent.putExtra("name",getIntent().getStringExtra("name"));
                                                        intent.putExtra("mobile",getIntent().getStringExtra("mobile"));
                                                        intent.putExtra("address",getIntent().getStringExtra("address"));
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        progressDialog.dismiss();
                                                        startActivity(intent);
                                                    }
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                            //Changing the value of field "priority" inside document of groups.

                            //saveClientDetails(newGroup);
                            //refreshing page to reset spinner with new value.
                        }else {

                            if (  (priority >=1 && priority <= originalColonies.size() + 1) ) {
                                //rearranging array list according to priority
                                if(priority==originalColonies.size()+1)
                                {
                                    int c=0;
                                    for(int i=0;i<originalColonies.size();i++)
                                    {
                                        updatedColonies.add(c,originalColonies.get(i));
                                        ++c;
                                    }
                                    updatedColonies.add(c,newGroup);
                                }
                                else {
                                    int c = 0;
                                    for (int i = 0; i < originalColonies.size(); i++) {
                                        if (i < priority - 1) {
                                            updatedColonies.add(c, originalColonies.get(i));
                                        } else if (i == priority - 1) {
                                            updatedColonies.add(c, newGroup);
                                            ++c;
                                            updatedColonies.add(c, originalColonies.get(i));
                                        } else {
                                            updatedColonies.add(c, originalColonies.get(i));
                                        }
                                        ++c;
                                    }
                                }
                                //updating previous array in fireStore with new rearranged array
                                Log.i("TEST", String.valueOf(updatedColonies.size()));
                                db.collection("Delivery").document(milkManMobile).update("Group", updatedColonies);
                                //Changing the value of field "priority" inside document of groups.
                                for (int i = 0; i < updatedColonies.size(); i++) {
                                    PriorityModel pr = new PriorityModel(i + 1);
                                    db.collection("Delivery").document(milkManMobile).collection("Group").document(updatedColonies.get(i))
                                            .set(pr, SetOptions.merge());
                                }
                                // saveClientDetails(newGroup);
                                //refreshing page to reset spinner with new value.
                                Intent intent = new Intent(Groups.this, Groups.class);
                                intent.putExtra("name",getIntent().getStringExtra("name"));
                                intent.putExtra("mobile",clientMobile);
                                intent.putExtra("address",clientAddress);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                progressDialog.dismiss();
                                startActivity(intent);
                             }
                            else
                            {
                                Toast.makeText(Groups.this, "Group priority number should be between 1 and " + originalColonies.size()+1, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
    //method to check if group already exists
    private int checkGroupExists(final String gN) {
        int flag = 0;

        for (int i=0;i<arrayList.size();i++)
        {
            if(arrayList.get(i).getGroupName().equalsIgnoreCase(gN))
            {
                flag=1;
                Toast.makeText(this, getResources().getString(R.string.group_already_exist), Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return flag;
    }
    void attachID() {
        recyclerView=findViewById(R.id.groups_rv);
        back=findViewById(R.id.back_to_scan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroup=findViewById(R.id.add_group);
        noGroup = findViewById(R.id.tv_no_added_group);

        db= FirebaseFirestore.getInstance();
        arrayList=new ArrayList<>();
        customDialog=new Dialog(this);

        clientName=getIntent().getStringExtra("name");
        clientMobile=getIntent().getStringExtra("mobile");
        clientAddress=getIntent().getStringExtra("address");
    }

}
