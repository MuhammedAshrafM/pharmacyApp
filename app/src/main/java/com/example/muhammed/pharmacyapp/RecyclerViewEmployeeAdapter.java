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

class RecyclerViewEmployeeAdapter extends RecyclerView.Adapter<ViewHolder>{

    ArrayList<Employee> employeeArrayList;
    Context context;
    Intent intent;

    DisplayMetrics displayMetrics;
    float dpWidth;
    int numberOfCharacters;
    public static List<String> selectedEmployees;

    public RecyclerViewEmployeeAdapter(Context context, ArrayList<Employee> employeeArrayList) {
        this.context = context;
        this.employeeArrayList = employeeArrayList;
        this.selectedEmployees = new ArrayList<>();

        displayMetrics = context.getResources().getDisplayMetrics();
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        numberOfCharacters = (int) (dpWidth / 18);
        intent = new Intent(context, EditEmployeeActivity.class);


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

        final String imagePath = employeeArrayList.get(position).getImagePath().toString();
        final String name = employeeArrayList.get(position).getName().toString();
        final String type = employeeArrayList.get(position).getType().toString();

        holder.type.setText(type);

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
                String id = employeeArrayList.get(position).getId().toString();
                String imagePath = employeeArrayList.get(position).getImagePath().toString();
                String name = employeeArrayList.get(position).getName().toString();
                String birthday = employeeArrayList.get(position).getBirthday().toString();
                String address = employeeArrayList.get(position).getAddress().toString();
                String phoneNumber = employeeArrayList.get(position).getPhoneNumber().toString();
                String userName = employeeArrayList.get(position).getUserName().toString();
                String password = employeeArrayList.get(position).getPassword().toString();
                String type = employeeArrayList.get(position).getType().toString();
                String gender = employeeArrayList.get(position).getGender().toString();
                String salary = employeeArrayList.get(position).getSalary().toString();

                intent.putExtra("id",id);
                intent.putExtra("imagePath",imagePath);
                intent.putExtra("name",name);
                intent.putExtra("birthday",birthday);
                intent.putExtra("address",address);
                intent.putExtra("phoneNumber",phoneNumber);
                intent.putExtra("userName",userName);
                intent.putExtra("password",password);
                intent.putExtra("type",type);
                intent.putExtra("gender",gender);
                intent.putExtra("salary",salary);
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    int position = (int) compoundButton.getTag();
                    selectedEmployees.add("" + employeeArrayList.get(position).getId());
                }
                if (b == false) {
                    selectedEmployees.remove(employeeArrayList.get(position).getId());
                }
            }
        });

        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(context, FullScreenImage.class);
                intent.putExtra("imagePath",employeeArrayList.get(position).getImagePath());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeArrayList.size();
    }

    public void setFilter(List<Employee> employeeList){
        employeeArrayList =new ArrayList<>();
        employeeArrayList.addAll(employeeList);
        notifyDataSetChanged();
    }
}
