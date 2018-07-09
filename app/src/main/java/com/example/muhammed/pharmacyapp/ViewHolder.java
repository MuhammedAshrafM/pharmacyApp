package com.example.muhammed.pharmacyapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mu7ammed_A4raf on 01-Mar-18.
 */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name,type;
        CircleImageView picture;
        CheckBox checkBox;

        ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            picture = (CircleImageView) itemView.findViewById(R.id.picture);
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
