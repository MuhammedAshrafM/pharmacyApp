package com.example.muhammed.pharmacyapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

    public class ViewHolderAllBills extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView barcodeDrug,englishNameDrug,countDrug,priceDrug;
        CheckBox checkBox;

        ItemClickListener itemClickListener;
        public ViewHolderAllBills(View itemView) {
            super(itemView);

            barcodeDrug = (TextView) itemView.findViewById(R.id.barcodeDrug);
            englishNameDrug = (TextView) itemView.findViewById(R.id.englishNameDrug);
            priceDrug = (TextView) itemView.findViewById(R.id.priceDrug);
            countDrug = (TextView) itemView.findViewById(R.id.countDrug);
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
