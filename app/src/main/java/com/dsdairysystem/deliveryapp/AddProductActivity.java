package com.dsdairysystem.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {
    private TextInputEditText etName,etQuality,etAmount;
    private Button btAddProduct;
    //private String user_id="+919988776655";
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private   RecyclerView.Adapter myAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etAmount=findViewById(R.id.input_product_amount);
        // etName=findViewById(R.id.input_product_name);
        etQuality=findViewById(R.id.input_product_quality);
        btAddProduct=findViewById(R.id.btAddProduct);


        btAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle(v.getContext().getResources().getString(R.string.product_added));
                progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String amount = etAmount.getText().toString();
                String quality= etQuality.getText().toString();


                if (amount.isEmpty()||quality.isEmpty()){
                    Toast.makeText(AddProductActivity.this,v.getContext().getResources().getString(R.string.please_fill_all_the_details),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    long amt = Long.parseLong(amount);
                    final Map<String,Object> product = new HashMap<>();
                    product.put("Price",amt);
                    product.put("Quality",quality);

                    FirebaseFirestore.getInstance().collection("Delivery").document(user_id).update("Products", FieldValue.arrayUnion(product)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(AddProductActivity.this,v.getContext().getResources().getString(R.string.product_added),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddProductActivity.this,DeliveryProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                progressDialog.dismiss();
                                startActivity(intent);
                            }

                        }
                    });

                }
            }
        });


    }
}