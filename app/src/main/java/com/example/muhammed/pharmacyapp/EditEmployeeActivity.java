package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditEmployeeActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    EditText nameEditText, birthdayEditText, addressEditText, phoneEditText, userNameEditText, passwordEditText, salaryEditText;
    Spinner typeSpinner, genderSpinner;
    Button editEmployee;
    View.OnClickListener onClickListener;
    static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2, PLACE_PICKER_REQUEST = 3;
    Uri imageUri;
    String strId, strPictureName, strPicturePath, strName,strBirthday, strAddress, strPhone, strUserName, strPassword, strSalary, strType,
            strGender;


    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        circleImageView = (CircleImageView)findViewById(R.id.picture);
        nameEditText = (EditText)findViewById(R.id.name);
        birthdayEditText = (EditText)findViewById(R.id.birthday);
        addressEditText = (EditText)findViewById(R.id.address);
        phoneEditText = (EditText)findViewById(R.id.phone);
        userNameEditText = (EditText)findViewById(R.id.userName);
        passwordEditText = (EditText)findViewById(R.id.password);
        salaryEditText = (EditText)findViewById(R.id.salary);
        typeSpinner = (Spinner)findViewById(R.id.type);
        genderSpinner = (Spinner)findViewById(R.id.gender);
        editEmployee = (Button)findViewById(R.id.editEmployee);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] typeEmployee = new String[]{"Type","Employee","Manager"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditEmployeeActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,typeEmployee);
        typeSpinner.setAdapter(arrayAdapter);


        String[] genderEmployee = new String[]{"Gender","Male","Female"};
        arrayAdapter = new ArrayAdapter<String>(EditEmployeeActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,genderEmployee);
        genderSpinner.setAdapter(arrayAdapter);

        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        editor = shredPreferences.edit();

        strPicturePath=" ";

        Intent intent =getIntent();
        strId = intent.getStringExtra("id");
        strPictureName = intent.getStringExtra("imagePath");
        strName = intent.getStringExtra("name");
        strBirthday = intent.getStringExtra("birthday");
        strAddress = intent.getStringExtra("address");
        strPhone = intent.getStringExtra("phoneNumber");
        strUserName = intent.getStringExtra("userName");
        strPassword = intent.getStringExtra("password");
        strSalary = intent.getStringExtra("salary");
        strType = intent.getStringExtra("type");
        strGender = intent.getStringExtra("gender");

        PicassoClient.downloadimage(EditEmployeeActivity.this,strPictureName,circleImageView);
        nameEditText.setText(strName);
        birthdayEditText.setText(strBirthday);
        addressEditText.setText(strAddress);
        phoneEditText.setText(strPhone);
        userNameEditText.setText(strUserName);
        passwordEditText.setText(strPassword);
        salaryEditText.setText(strSalary);
        if(strType.equals("Employee")){
            typeSpinner.setSelection(1);
        }
        else if(strType.equals("Manager")){
            typeSpinner.setSelection(2);
        }

        if(strGender.equals("Male")){
            genderSpinner.setSelection(1);
        }
        else if(strGender.equals("Female")){
            genderSpinner.setSelection(2);
        }

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.picture:
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent,"Select Image From Gallery"),SELECTED_PICTURE);
                        break;

                    case R.id.address:
                        PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();

                        try {
                            intent=builder.build(EditEmployeeActivity.this);
                            startActivityForResult(intent,PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.birthday:
                        DateDialog dialog = new DateDialog(view,false);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                        birthdayEditText.setFocusable(false);
                        birthdayEditText.setClickable(true);
                        break;

                    case R.id.editEmployee:
                        editEmployee();
                        break;

                    default:
                        break;
                }
            }
        };

        circleImageView.setOnClickListener(onClickListener);
        addressEditText.setOnClickListener(onClickListener);
        birthdayEditText.setOnClickListener(onClickListener);
        editEmployee.setOnClickListener(onClickListener);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    strType = "Employee";
                }
                else if(i==2){
                    strType = "Manager";
                }
                else {
                    strType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    strGender = "Male";
                }
                else if(i==2){
                    strGender = "Female";
                }
                else {
                    strGender = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void editEmployee(){
        strName = nameEditText.getText().toString().trim();
        strBirthday = birthdayEditText.getText().toString().trim();
        strAddress = addressEditText.getText().toString().trim();
        strPhone = phoneEditText.getText().toString().trim();
        strUserName = userNameEditText.getText().toString().trim();
        strPassword = passwordEditText.getText().toString().trim();
        strSalary = salaryEditText.getText().toString().trim();


        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response !=null){
                    if(!response.isEmpty()){
                        if(response.equals("Success")){
                            Toast.makeText(EditEmployeeActivity.this,"Employee Edit successfully",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else if (response.equals("FailedEdit")){
                            Toast.makeText(EditEmployeeActivity.this,"Failed",Toast.LENGTH_LONG).show();
                        }
                        else if (response.equals("Failed")){
                            Toast.makeText(EditEmployeeActivity.this,"UserName is used previously",Toast.LENGTH_LONG).show();
                        }
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
                Map<String,String> map = new HashMap<>();
                map.put("function","editEmployee");
                map.put("id",strId);
                map.put("imagePath",strPicturePath);
                map.put("imageName",strPictureName);
                map.put("name",strName);
                map.put("birthday",strBirthday);
                map.put("address",strAddress);
                map.put("phoneNumber",strPhone);
                map.put("userName",strUserName);
                map.put("password",strPassword);
                map.put("type",strType);
                map.put("gender",strGender);
                map.put("salary",strSalary);

                return map;
            }
        };
        Singleton.getSingleton(EditEmployeeActivity.this).setRequestQue(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == SELECTED_PICTURE) {
                if (data != null) {
                    try {
                        imageUri = data.getData();

                        Intent photoPickerCrop = new Intent("com.android.camera.action.CROP");
                        photoPickerCrop.setDataAndType(imageUri, "image/*");
                        photoPickerCrop.putExtra("crop", "true");
                        // indicate aspect of desired crop
                        photoPickerCrop.putExtra("aspectX", 1);
                        photoPickerCrop.putExtra("aspectY", 1);
                        // indicate output X and Y
                        photoPickerCrop.putExtra("outputX", 360);
                        photoPickerCrop.putExtra("outputY", 360);
                        // retrieve data on return
                        photoPickerCrop.putExtra("scaleUpIfNeeded", true);
                        photoPickerCrop.putExtra("return-data", true);

                        startActivityForResult(photoPickerCrop, CROP_PICTURE);
                    } catch (ActivityNotFoundException ex) {

                    }
                }
                else {
                    Toast.makeText(this, "You haven't Selected Picture", Toast.LENGTH_LONG).show();
                }
            }

            else if(requestCode == CROP_PICTURE){
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    Bitmap selectedImage = bundle.getParcelable("data");
                    circleImageView.setImageBitmap(selectedImage);
                    ByteArrayOutputStream byteArrayOutputStreamObject;
                    byteArrayOutputStreamObject = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
                    byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
                    strPictureName = String.valueOf(System.currentTimeMillis());
                    strPicturePath = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
                }
                else {
                    Toast.makeText(this, "You haven't Crop Picture", Toast.LENGTH_LONG).show();
                }
            }

            else if(requestCode == PLACE_PICKER_REQUEST){
                if (data != null) {
                    Place place = PlacePicker.getPlace(data, this);
                    String addressFromMap = String.valueOf(place.getAddress());
                    addressEditText.setText(addressFromMap);
                }
                else {
                    Toast.makeText(this, "You haven't Selected Address", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void deleteEmployee(){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("function", "deleteSelectedEmployees");
                    map.put("employeeIds", strId);
                    return map;
                }
            };

            Singleton.getSingleton(EditEmployeeActivity.this).setRequestQue(stringRequest);
    }

    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDelete));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteEmployee();
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
        menuInflater.inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete:
                confirmDelete();
        }

        return super.onOptionsItemSelected(item);
    }
}
