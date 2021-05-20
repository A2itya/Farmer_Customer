package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTv, satbaraNumberTv, emailTv, tapProductsTv, tapOrdersTv, filteredProductsTv;
    private ImageButton logoutBtn, editProfileBtn, addProductBtn, filterProductBtn;
    private ImageView profileIv;
    private EditText searchProductEt;
    private RelativeLayout productsRl, ordersRl;
    private RecyclerView productsRv;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv = findViewById(R.id.nameTv);
        satbaraNumberTv = findViewById(R.id.satbaraNumberTv);
        emailTv = findViewById(R.id.emailTv);
        tapProductsTv = findViewById(R.id.tapProductsTv);
        tapOrdersTv = findViewById(R.id.tapOrdersTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        profileIv = findViewById(R.id.profileIv);
        productsRv = findViewById(R.id.productsRv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        addProductBtn = findViewById(R.id.addProductBtn);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        loadAllProducts();
        showProductsUI();

        //search
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterProductSeller.getFilter().filter(charSequence);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit profile activity
                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open edit and product activity
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });

        tapProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load products
                showProductsUI();
            }
        });

        tapOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load orders
                showOrdersUI();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //get selected Item
                                String selected = Constants.productCategories1[i];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    //load All
                                    loadAllProducts();
                                }
                                else{
                                    //load filtered
                                    loadFilteredProducts(selected);
                                }
                            }
                        }).show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {

        productList = new ArrayList<>();

        //get All products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        for(DataSnapshot ds:snapshot.getChildren()){

                            String productCategory = ""+ds.child("productCategory").getValue();

                            //if selected category matches product category then add in list
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set Adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        //get All products
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        //set Adapter
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        //show products ui and hides orders ui
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tapProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tapProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tapOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tapOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUI() {
        //show orders ui and hides products ui
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tapProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tapProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tapOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tapOrdersTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private void makeMeOffline() {
        //after logging in make user online
        progressDialog.setMessage("Logging out...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //update Successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed Updating
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("name").getValue();
                            String accountType = ""+ds.child("accountType").getValue();
                            String email = ""+ds.child("email").getValue();
                            String satbaraNumber = ""+ds.child("satbaraNumber").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();

                            nameTv.setText(name);
                            satbaraNumberTv.setText(satbaraNumber);
                            emailTv.setText(email);
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}