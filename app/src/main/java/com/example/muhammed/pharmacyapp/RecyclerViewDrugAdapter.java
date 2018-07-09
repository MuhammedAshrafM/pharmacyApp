package com.example.muhammed.pharmacyapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mu7ammed_A4raf on 06-Dec-17.
 */

class RecyclerViewDrugAdapter extends RecyclerView.Adapter<ViewHolder> {

    ArrayList<Drug> drugArrayList;
    Context context;
    Intent intent;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;
    public static List<String> selectedDrugs;

    public RecyclerViewDrugAdapter(Context context, ArrayList<Drug> drugArrayList) {
        this.context = context;
        this.drugArrayList = drugArrayList;
        this.selectedDrugs = new ArrayList<>();

        displayMetrics = context.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        intent = new Intent(context, EditDrugActivity.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String imagePath = drugArrayList.get(position).getImagePath().toString();
        final String name = drugArrayList.get(position).getEnglishName().toString();
        final String amount = drugArrayList.get(position).getAmount().trim().toString();

        if(amount.equals("1") || amount.equals("0")){
            holder.type.setText(amount + " Drug");
        }
        else {
            holder.type.setText(amount + " Drugs");
        }

        if(name.length() >numberOfCharacters ) {
            holder.name.setText(name.substring(0,numberOfCharacters)+"  ");
        }
        else {
            holder.name.setText(name);
        }

        PicassoClient.downloadimage(context, imagePath, holder.picture);
        holder.checkBox.setTag(position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = drugArrayList.get(position).getId().toString();
                String imagePath = drugArrayList.get(position).getImagePath().toString();
                String barcode = drugArrayList.get(position).getBarcode().toString();
                String englishName = drugArrayList.get(position).getEnglishName().toString();
                String arabicName = drugArrayList.get(position).getArabicName().toString();
                String amount = drugArrayList.get(position).getAmount().toString();
                String numBar = drugArrayList.get(position).getNumBar().toString();
                String price = drugArrayList.get(position).getPrice().toString();
                String discount = drugArrayList.get(position).getDiscount().toString();
                String department = drugArrayList.get(position).getDepartment().toString();
                String productionDate = drugArrayList.get(position).getProductionDate().toString();
                String expiryDate = drugArrayList.get(position).getExpiryDate().toString();
                String companyId = drugArrayList.get(position).getCompanyId().toString();

                intent.putExtra("id",id);
                intent.putExtra("imagePath",imagePath);
                intent.putExtra("barcode",barcode);
                intent.putExtra("englishName",englishName);
                intent.putExtra("arabicName",arabicName);
                intent.putExtra("amount",amount);
                intent.putExtra("numBar",numBar);
                intent.putExtra("price",price);
                intent.putExtra("discount",discount);
                intent.putExtra("department",department);
                intent.putExtra("productionDate",productionDate);
                intent.putExtra("expiryDate",expiryDate);
                intent.putExtra("companyId",companyId);
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    int position = (int) compoundButton.getTag();
                    selectedDrugs.add("" + drugArrayList.get(position).getId());
                }
                if (b == false) {
                    selectedDrugs.remove(drugArrayList.get(position).getId());
                }
            }
        });

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(context, FullScreenImage.class);
                intent.putExtra("imagePath",drugArrayList.get(position).getImagePath());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return drugArrayList.size();
    }


    public void setFilter(List<Drug> drugList){
        drugArrayList =new ArrayList<>();
        drugArrayList.addAll(drugList);
        notifyDataSetChanged();
    }
}
