package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;
    String userType;
    ImageButton employee, drugs, selling, purchasing, company, report;
    View.OnClickListener onClickListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                    return true;
                case R.id.navigation_notifications:
                    //startActivity(new Intent(HomeActivity.this,EmployeesActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        employee = (ImageButton)findViewById(R.id.employee);
        drugs = (ImageButton)findViewById(R.id.drugs);
        selling = (ImageButton)findViewById(R.id.selling);
        purchasing = (ImageButton)findViewById(R.id.purchasing);
        company = (ImageButton)findViewById(R.id.company);
        report = (ImageButton)findViewById(R.id.report);

        onClickListener =new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.employee:
                        startActivity(new Intent(HomeActivity.this,EmployeesActivity.class));
                        break;

                    case R.id.drugs:
                        startActivity(new Intent(HomeActivity.this,DrugsActivity.class));
                        break;

                    case R.id.selling:
                        startActivity(new Intent(HomeActivity.this,SalesActivity.class));
                        break;

                    case R.id.purchasing:
                        startActivity(new Intent(HomeActivity.this,PurchasesActivity.class));
                        break;

                    case R.id.company:
                        startActivity(new Intent(HomeActivity.this,EmployeesActivity.class));
                        break;

                    case R.id.report:
                        startActivity(new Intent(HomeActivity.this,EmployeesActivity.class));
                        break;

                    default:
                        break;

                }
            }
        };

        employee.setOnClickListener(onClickListener);
        drugs.setOnClickListener(onClickListener);
        selling.setOnClickListener(onClickListener);
        purchasing.setOnClickListener(onClickListener);

        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        editor = shredPreferences.edit();
        userType = shredPreferences.getString("type","");
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private void confirmLogOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmLogOut));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(HomeActivity.this,LogInActivity.class);
                        editor.clear().commit();
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logOut:
                confirmLogOut();
        }

        return super.onOptionsItemSelected(item);
    }
}
