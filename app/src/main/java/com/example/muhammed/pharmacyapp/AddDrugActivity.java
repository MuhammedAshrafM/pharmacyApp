package com.example.muhammed.pharmacyapp;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDrugActivity extends AppCompatActivity {


    CircleImageView circleImageView;
    TextView barcodeTextView;
    EditText englishNameEditText, arabicNameEditText, amountEditText, priceEditText, discountEditText, productionDateEditText,
             expiryDateEditText;
    Spinner numBarSpinner, departmentSpinner, companyNameSpinner;
    Button addDrug;
    View.OnClickListener onClickListener;
    static final int SELECTED_PICTURE = 1, CROP_PICTURE = 2, SELECTED_BARCODE = 3, PERMISSION_REQUEST = 200;
    Uri imageUri;
    String strPictureName, strPicturePath, strBarcode, strEnglishName, strArabicName, strAmount, strPrice, strDiscount,
           strProductionDate, strExpiryDate, strNumBar, strDepartment, strCompanyId;

    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;

    String[] departmentDrug, numBarDrug;
    ArrayList<String> companyNames;
    ArrayList<Company> companyArrayList;
    Company company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drug);

        circleImageView = (CircleImageView)findViewById(R.id.picture);

        barcodeTextView = (TextView) findViewById(R.id.barcode);
        englishNameEditText = (EditText)findViewById(R.id.englishName);
        arabicNameEditText = (EditText)findViewById(R.id.arabicName);
        amountEditText = (EditText)findViewById(R.id.amount);
        priceEditText = (EditText)findViewById(R.id.price);
        discountEditText = (EditText)findViewById(R.id.discount);
        productionDateEditText = (EditText)findViewById(R.id.productionDate);
        expiryDateEditText = (EditText)findViewById(R.id.expiryDate);
        numBarSpinner = (Spinner)findViewById(R.id.numBar);
        departmentSpinner = (Spinner)findViewById(R.id.department);
        companyNameSpinner = (Spinner)findViewById(R.id.companyName);
        addDrug = (Button)findViewById(R.id.addDrug);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST);
        }

        numBarDrug = new String[]{getString(R.string.numBar).toString(),"1","2","3","4","5"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddDrugActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,numBarDrug);
        numBarSpinner.setAdapter(arrayAdapter);


        departmentDrug = getResources().getStringArray(R.array.drugDepartments);
        arrayAdapter = new ArrayAdapter<String>(AddDrugActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,departmentDrug);
        departmentSpinner.setAdapter(arrayAdapter);

        companyNames = new ArrayList<>();
        companyNames.add("Company Name");

        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        editor = shredPreferences.edit();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.picture:
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(Intent.createChooser(intent,"Select Image From Gallery"),SELECTED_PICTURE);
                        break;

                    case R.id.barcode:
                        intent = new Intent(AddDrugActivity.this,ScanActivity.class);
                        startActivityForResult(intent,SELECTED_BARCODE);
                        break;

                    case R.id.productionDate:
                        DateDialog dialog = new DateDialog(view,false);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                        productionDateEditText.setFocusable(false);
                        productionDateEditText.setClickable(true);
                        break;

                    case R.id.expiryDate:
                        dialog = new DateDialog(view,false);
                        ft = getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");
                        expiryDateEditText.setFocusable(false);
                        expiryDateEditText.setClickable(true);
                        break;

                    case R.id.addDrug:
                        addDrug();
                        break;

                    default:
                        break;
                }
            }
        };

        circleImageView.setOnClickListener(onClickListener);
        barcodeTextView.setOnClickListener(onClickListener);
        productionDateEditText.setOnClickListener(onClickListener);
        expiryDateEditText.setOnClickListener(onClickListener);
        addDrug.setOnClickListener(onClickListener);

        numBarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    strNumBar = "1";
                }
                else if(i==2){
                    strNumBar = "2";
                }
                else if(i==3){
                    strNumBar = "3";
                }
                else if(i==4){
                    strNumBar = "4";
                }
                else if(i==5){
                    strNumBar = "5";
                }
                else {
                    strNumBar = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    strDepartment = "";
                }
                else {
                    strDepartment = departmentDrug[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        companyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    strCompanyId = "";
                }
                else {
                    strCompanyId = companyArrayList.get(i-1).getId().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getJSON();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private void getJSON (){
        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        companyArrayList = new ArrayList<>();
                        if(!jsonArray.equals(null)){
                            for(int i=0;i<jsonArray.length();i++){
                                jsonObject = jsonArray.getJSONObject(i);

                                company =new Company();
                                company.setId(jsonObject.getString("idCompany"));
                                company.setName(jsonObject.getString("nameCompany"));

                                companyArrayList.add(company);
                                companyNames.add(jsonObject.getString("nameCompany"));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddDrugActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,companyNames);
                companyNameSpinner.setAdapter(arrayAdapter);
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
                map.put("function","getCompanies");
                return map;
            }
        };

        Singleton.getSingleton(AddDrugActivity.this).setRequestQue(stringRequest);
    }

    private void addDrug(){
        strEnglishName = englishNameEditText.getText().toString().trim();
        strArabicName = arabicNameEditText.getText().toString().trim();
        strAmount = amountEditText.getText().toString().trim();
        strPrice = priceEditText.getText().toString().trim();
        // check on discount if more than 100 or not
        strDiscount = discountEditText.getText().toString().trim();
        strProductionDate = productionDateEditText.getText().toString().trim();
        strExpiryDate = expiryDateEditText.getText().toString().trim();


        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response !=null){
                    if(!response.isEmpty()){
                        if(response.equals("Success")){
                            Toast.makeText(AddDrugActivity.this,"Drug added successfully",Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else if (response.equals("SuccessUpdate")){
                            Toast.makeText(AddDrugActivity.this,"Drug updated successfully",Toast.LENGTH_LONG).show();
                        }
                        else if (response.equals("FailedUpdate") || response.equals("FailedInsert")){
                            Toast.makeText(AddDrugActivity.this,"Failed",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(AddDrugActivity.this,response,Toast.LENGTH_LONG).show();
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
                map.put("function","addDrug");
                map.put("imagePath",strPicturePath);
                map.put("imageName",strPictureName);
                map.put("barcode",strBarcode);
                map.put("englishName",strEnglishName);
                map.put("arabicName",strArabicName);
                map.put("amount",strAmount);
                map.put("numBar",strNumBar);
                map.put("price",strPrice);
                map.put("discount",strDiscount);
                map.put("productionDate",strProductionDate);
                map.put("expiryDate",strExpiryDate);
                map.put("department",strDepartment);
                map.put("companyId",strCompanyId);

                return map;
            }
        };
        Singleton.getSingleton(AddDrugActivity.this).setRequestQue(stringRequest);
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

            else if(requestCode == SELECTED_BARCODE){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    strBarcode = barcode.displayValue;
                    barcodeTextView.setText(strBarcode);
                }
            }

        }
    }

}
