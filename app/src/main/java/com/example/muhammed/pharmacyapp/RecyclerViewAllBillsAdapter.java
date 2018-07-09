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

class RecyclerViewAllBillsAdapter extends RecyclerView.Adapter<ViewHolderAllBills> {

    ArrayList<Bill> billArrayList;
    Context context;
    Intent intent;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;
    public static List<String> selectedBills;

    public RecyclerViewAllBillsAdapter(Context context, ArrayList<Bill> billArrayList, String typeBill) {
        this.context = context;
        this.billArrayList = billArrayList;
        this.selectedBills = new ArrayList<>();

        displayMetrics = context.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        if(typeBill.equals("Sale")){
            intent = new Intent(context, EditSaleBillActivity.class);
        }
        else {
            intent = new Intent(context, EditPurchaseBillActivity.class);
        }
    }

    @Override
    public ViewHolderAllBills onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_all_bills, parent, false);

        ViewHolderAllBills vh = new ViewHolderAllBills(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolderAllBills holder, final int position) {

        if(position == 0){
            holder.checkBox.setVisibility(View.INVISIBLE);
        }

        final String barcodeDrug = billArrayList.get(position).getBarcodeDrug().toString();
        final String englishNameDrug = billArrayList.get(position).getEnglishNameDrug().toString();
        final String priceDrug = billArrayList.get(position).getPriceDrug().toString();
        final String countDrug = billArrayList.get(position).getCountDrug().toString();

        if(barcodeDrug.length() > numberOfCharacters) {
            holder.barcodeDrug.setText(barcodeDrug.substring(0,numberOfCharacters)+"  ");
        }
        else {
            holder.barcodeDrug.setText(barcodeDrug);
        }

        if(englishNameDrug.length() > numberOfCharacters) {
            holder.englishNameDrug.setText(englishNameDrug.substring(0,numberOfCharacters)+"  ");
        }
        else {
            holder.englishNameDrug.setText(englishNameDrug);
        }
        holder.priceDrug.setText(priceDrug);
        holder.countDrug.setText(countDrug);

        holder.checkBox.setTag(position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position != 0) {

                    String idBill = billArrayList.get(position).getIdBill().toString();
                    String billNumber = billArrayList.get(position).getBillNumber().toString();
                    String dateBill = billArrayList.get(position).getDateBill().toString();
                    String paymentWayBill = billArrayList.get(position).getPaymentWayBill().toString();
                    String discountBill = billArrayList.get(position).getDiscountBill().toString();
                    String countDrug = billArrayList.get(position).getCountDrug().toString();
                    String barcodeDrug = billArrayList.get(position).getBarcodeDrug().toString();
                    String imagePathDrug = billArrayList.get(position).getImagePathDrug().toString();
                    String englishNameDrug = billArrayList.get(position).getEnglishNameDrug().toString();
                    String arabicNameDrug = billArrayList.get(position).getArabicNameDrug().toString();
                    String priceDrug = billArrayList.get(position).getPriceDrug().toString();
                    String nameEmployee = billArrayList.get(position).getNameEmployee().toString();
                    String typeEmployee = billArrayList.get(position).getTypeEmployee().toString();

                    intent.putExtra("idBill", idBill);
//                intent.putExtra("billNumber",billNumber);
//                intent.putExtra("dateBill",dateBill);
                    intent.putExtra("paymentWayBill", paymentWayBill);
                    intent.putExtra("countDrug", countDrug);
                    intent.putExtra("barcodeDrug", barcodeDrug);
                    intent.putExtra("imagePathDrug", imagePathDrug);
                    intent.putExtra("discountBill", discountBill);
//                intent.putExtra("englishNameDrug",englishNameDrug);
//                intent.putExtra("arabicNameDrug",arabicNameDrug);
//                intent.putExtra("priceDrug",priceDrug);
//                intent.putExtra("nameEmployee",nameEmployee);
//                intent.putExtra("typeEmployee",typeEmployee);
                    context.startActivity(intent);
                }
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    int position = (int) compoundButton.getTag();
                    selectedBills.add("" + billArrayList.get(position).getIdBill());
                }
                if (b == false) {
                    selectedBills.remove(billArrayList.get(position).getIdBill());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return billArrayList.size();
    }


    public void setFilter(List<Bill> invoiceList){
        billArrayList =new ArrayList<>();
        billArrayList.addAll(invoiceList);
        notifyDataSetChanged();
    }
}
