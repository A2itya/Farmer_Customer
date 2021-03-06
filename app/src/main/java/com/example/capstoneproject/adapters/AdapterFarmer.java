package com.example.capstoneproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;
import com.example.capstoneproject.activities.FarmerDetailsActivity;
import com.example.capstoneproject.models.ModelFarmer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFarmer extends RecyclerView.Adapter<AdapterFarmer.HolderFarmer>{

    private Context context;
    public ArrayList<ModelFarmer> farmerList;

    public AdapterFarmer(Context context, ArrayList<ModelFarmer> farmerList) {
        this.context = context;
        this.farmerList = farmerList;
    }

    @NonNull
    @Override
    public HolderFarmer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_shop.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_farmer, parent, false);
        return new HolderFarmer(view );
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFarmer holder, int position) {
        ModelFarmer modelFarmer = farmerList.get(position);
        String accountType = modelFarmer.getAccountType();
        String address = modelFarmer.getAddress();
        String city = modelFarmer.getCity();
        String country = modelFarmer.getCountry();
        String deliveryFee = modelFarmer.getDeliveryFee();
        String email = modelFarmer.getEmail();
        String latitude = modelFarmer.getLatitude();
        String longitude = modelFarmer.getLongitude();
        String online = modelFarmer.getOnline();
        String name = modelFarmer.getName();
        String phone = modelFarmer.getPhone();
        String uid = modelFarmer.getUid();
        String timestamp = modelFarmer.getTimestamp();
        String farmerSelling = modelFarmer.getSelling();
        String state = modelFarmer.getState();
        String profileImage = modelFarmer.getProfileImage();

        loadReviews(modelFarmer, holder);

        //set data
        holder.farmerNameTv.setText(name);
        holder.addressTv.setText(address);
        holder.phoneTv.setText(phone);

        if(online.equals("true")){
            //farmer is online
            holder.onlineIv.setVisibility(View.VISIBLE);
        }else{
            holder.onlineIv.setVisibility(View.GONE);
        }

        //check farmer is selling
        if(farmerSelling.equals("true")){
            holder.selling.setVisibility(View.VISIBLE);
            holder.notSelling.setVisibility(View.GONE);
        }
        else{
            holder.notSelling.setVisibility(View.VISIBLE);
            holder.selling.setVisibility(View.GONE);
        }

        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.farmerIv);
        }
        catch (Exception e){
            holder.farmerIv.setImageResource(R.drawable.ic_person_gray);
        }

        //handle click listener ,show farmer list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FarmerDetailsActivity.class);
                intent.putExtra("farmerUid", uid);
                context.startActivity(intent);
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews(ModelFarmer modelFarmer, HolderFarmer holder) {

        String farmerUid = modelFarmer.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(farmerUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum=0;
                        for(DataSnapshot ds:snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum+rating;

                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;
                        holder.ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return farmerList.size();
    }

    //view holder
    class HolderFarmer extends RecyclerView.ViewHolder{

        //ui views of row_shop.xml

        private ImageView farmerIv, onlineIv;
        private TextView notSelling, farmerNameTv, phoneTv, addressTv, selling;
        private RatingBar ratingBar;

        public HolderFarmer(@NotNull View itemView){
            super(itemView);

            //init uid views
            ratingBar = itemView.findViewById(R.id.ratingBar);
            notSelling = itemView.findViewById(R.id.notSelling);
            selling = itemView.findViewById(R.id.selling);
            farmerNameTv = itemView.findViewById(R.id.farmerNameTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            farmerIv = itemView.findViewById(R.id.farmerIv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
        }
    }
}
