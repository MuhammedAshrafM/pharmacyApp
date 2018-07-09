package com.example.muhammed.pharmacyapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

    public class ViewHolderBill extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView billNumber,dateBill;
        CheckBox checkBox;

        ItemClickListener itemClickListener;
        public ViewHolderBill(View itemView) {
            super(itemView);

            billNumber = (TextView) itemView.findViewById(R.id.billNumber);
            dateBill = (TextView) itemView.findViewById(R.id.dateBill);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }
    }
