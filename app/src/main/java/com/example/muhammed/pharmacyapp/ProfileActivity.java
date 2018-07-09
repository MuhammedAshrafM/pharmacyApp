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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    String userType,idUser;
    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;
    TextView name, birthday, address, phoneNumber, userName, type, gender, salary;
    CircleImageView circleImageView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                    return true;
                case R.id.navigation_profile:
                    return true;
                case R.id.navigation_notifications:
                    startActivity(new Intent(ProfileActivity.this,EmployeesActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);

        name= (TextView)findViewById(R.id.name);
        birthday= (TextView)findViewById(R.id.birthday);
        address= (TextView)findViewById(R.id.address);
        phoneNumber= (TextView)findViewById(R.id.phoneNumber);
        userName= (TextView)findViewById(R.id.userName);
        type= (TextView)findViewById(R.id.type);
        gender= (TextView)findViewById(R.id.gender);
        salary= (TextView)findViewById(R.id.salary);
        circleImageView = (CircleImageView)findViewById(R.id.picture);

        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        idUser = shredPreferences.getString("id","");

        getJSON();
    }
    private void getJSON (){
        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        if(!jsonArray.equals(null)){
                                jsonObject = jsonArray.getJSONObject(0);

                                //employee.setText(jsonObject.getString("id"));
                                PicassoClient.downloadimage(ProfileActivity.this,
                                        jsonObject.getString("imagePath"),circleImageView);
                                name.setText(jsonObject.getString("name"));
                                birthday.setText(jsonObject.getString("birthday"));
                                address.setText(jsonObject.getString("address"));
                                phoneNumber.setText(jsonObject.getString("phoneNumber"));
                                userName.setText(jsonObject.getString("userName"));
                                //employee.setText(jsonObject.getString("password"));
                                type.setText(jsonObject.getString("type"));
                                gender.setText(jsonObject.getString("gender"));
                                salary.setText(jsonObject.getString("salary"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map =new HashMap<>();
                map.put("function","getEmployee");
                map.put("id",idUser);
                return map;
            }
        };

        Singleton.getSingleton(ProfileActivity.this).setRequestQue(stringRequest);
    }

    private void confirmLogOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmLogOut));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(ProfileActivity.this,LogInActivity.class);
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
