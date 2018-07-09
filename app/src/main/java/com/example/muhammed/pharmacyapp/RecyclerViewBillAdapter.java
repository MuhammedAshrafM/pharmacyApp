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

class RecyclerViewBillAdapter extends RecyclerView.Adapter<ViewHolderBill> {

    ArrayList<Bill> billArrayList;
    Context context;
    Intent intent;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;
    public static List<String> selectedBills;

    public RecyclerViewBillAdapter(Context context, ArrayList<Bill> billArrayList) {
        this.context = context;
        this.billArrayList = billArrayList;
        this.selectedBills = new ArrayList<>();

        displayMetrics = context.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        intent = new Intent(context, PrintBillActivity.class);
    }

    @Override
    public ViewHolderBill onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_bill, parent, false);

        ViewHolderBill vh = new ViewHolderBill(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(final ViewHolderBill holder, final int position) {

        final String billNumber = billArrayList.get(position).getBillNumber().toString();
        final String dateBill = billArrayList.get(position).getDateBill().toString();

        if(billNumber.length() > (numberOfCharacters+8) ) {
            holder.billNumber.setText("Number: " + billNumber.substring(0,numberOfCharacters)+"  ");
        }
        else {
            holder.billNumber.setText("Number: " + billNumber);
        }


        if(billNumber.length() > (numberOfCharacters+6) ) {
            holder.dateBill.setText("Date: " + dateBill.substring(0,numberOfCharacters)+"  ");
        }
        else {
            holder.dateBill.setText("Date: " + dateBill.substring(0,16));
        }

        holder.checkBox.setTag(position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String idBill = billArrayList.get(position).getIdBill().toString();
                String billNumber = billArrayList.get(position).getBillNumber().toString();
                String dateBill = billArrayList.get(position).getDateBill().toString();
                String paymentWayBill = billArrayList.get(position).getPaymentWayBill().toString();
                String countDrug = billArrayList.get(position).getCountDrug().toString();
                String barcodeDrug = billArrayList.get(position).getBarcodeDrug().toString();
                String englishNameDrug = billArrayList.get(position).getEnglishNameDrug().toString();
                String arabicNameDrug = billArrayList.get(position).getArabicNameDrug().toString();
                String priceDrug = billArrayList.get(position).getPriceDrug().toString();
                String idEmployee = billArrayList.get(position).getIdEmployee().toString();
                String nameEmployee = billArrayList.get(position).getNameEmployee().toString();
                String typeEmployee = billArrayList.get(position).getTypeEmployee().toString();
                String idCompany, nameCompany;
                try {
                    idCompany = billArrayList.get(position).getIdCompany().toString();
                    nameCompany = billArrayList.get(position).getNameCompany().toString();
                } catch (Exception e) {
                    idCompany = " ";
                    nameCompany = " ";
                    e.printStackTrace();
                }

                intent.putExtra("idBill",idBill);
                intent.putExtra("billNumber",billNumber);
                intent.putExtra("dateBill",dateBill);
                intent.putExtra("paymentWayBill",paymentWayBill);
                intent.putExtra("countDrug",countDrug);
                intent.putExtra("barcodeDrug",barcodeDrug);
                intent.putExtra("englishNameDrug",englishNameDrug);
                intent.putExtra("arabicNameDrug",arabicNameDrug);
                intent.putExtra("priceDrug",priceDrug);
                intent.putExtra("idEmployee",idEmployee);
                intent.putExtra("nameEmployee",nameEmployee);
                intent.putExtra("typeEmployee",typeEmployee);
                intent.putExtra("idCompany",idCompany);
                intent.putExtra("nameCompany",nameCompany);
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                int position = (int) compoundButton.getTag();
                if (b == true) {

                    selectedBills.add("" + billArrayList.get(position).getBillNumber());
                }
                if (b == false) {

                    selectedBills.remove(billArrayList.get(position).getBillNumber());
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
