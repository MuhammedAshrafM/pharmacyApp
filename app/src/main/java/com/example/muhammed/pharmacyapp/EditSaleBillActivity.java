package com.example.muhammed.pharmacyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditSaleBillActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView barcodeTextView;
    EditText countEditText, discountEditText;
    Spinner paymentWaySpinner;
    Button editBill;
    View.OnClickListener onClickListener;
    static final int SELECTED_BARCODE = 1, PERMISSION_REQUEST = 200;

    SharedPreferences shredPreferences;

    String strPictureName, idBill, strBarcode, strCount, strDiscount, strPaymentWay, employeeId, billNumber = " ", date = " ";
    String[] paymentWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sale_bill);


        circleImageView = (CircleImageView)findViewById(R.id.picture);
        barcodeTextView = (TextView) findViewById(R.id.barcode);
        countEditText = (EditText)findViewById(R.id.count);
        discountEditText = (EditText)findViewById(R.id.discount);
        paymentWaySpinner = (Spinner)findViewById(R.id.paymentWay);
        editBill = (Button)findViewById(R.id.editBill);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},PERMISSION_REQUEST);
        }

        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        employeeId = shredPreferences.getString("id","");

        billNumber = " ";
        date = " ";

        paymentWay = getResources().getStringArray(R.array.paymentWay);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EditSaleBillActivity.this,R.layout.layout_spinner_item,R.id.textViewSpinnerItem,paymentWay);
        paymentWaySpinner.setAdapter(arrayAdapter);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){

                    case R.id.barcode:
                        Intent intent = new Intent(EditSaleBillActivity.this,ScanActivity.class);
                        startActivityForResult(intent,SELECTED_BARCODE);
                        break;

                    case R.id.editBill:
                        editBill();
                        break;

                    default:
                        break;
                }
            }
        };


        barcodeTextView.setOnClickListener(onClickListener);
        editBill.setOnClickListener(onClickListener);

        paymentWaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    strPaymentWay = "";
                }
                else {
                    strPaymentWay = paymentWay[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Intent intent =getIntent();
        idBill = intent.getStringExtra("idBill");
        strPaymentWay = intent.getStringExtra("paymentWayBill");
        strDiscount = intent.getStringExtra("discountBill");
        strCount = intent.getStringExtra("countDrug");
        strBarcode = intent.getStringExtra("barcodeDrug");
        strPictureName = intent.getStringExtra("imagePathDrug");

        PicassoClient.downloadimage(EditSaleBillActivity.this,strPictureName,circleImageView);
        barcodeTextView.setText(strBarcode);
        countEditText.setText(strCount);
        discountEditText.setText(strDiscount);

        if(strPaymentWay.equals("Monetary")){
            paymentWaySpinner.setSelection(1);
        }
        else if(strPaymentWay.equals("Visa")){
            paymentWaySpinner.setSelection(2);
        }


        //getPictureDrug();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void editBill(){
        strBarcode =  barcodeTextView.getText().toString().trim();
        strCount = countEditText.getText().toString().trim();

        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response !=null){
                    if(!response.isEmpty()){
                        if(response.equals("Drug Not Available")){
                            Toast.makeText(EditSaleBillActivity.this,"Drug Not Available",Toast.LENGTH_LONG).show();
                        }
                        else if (response.equals("Select a lower count")){
                            Toast.makeText(EditSaleBillActivity.this,"Select a lower count",Toast.LENGTH_LONG).show();
                        }
                        else if (response.equals("Edited successfully")){
                            Toast.makeText(EditSaleBillActivity.this,"Edited successfully",Toast.LENGTH_LONG).show();
                            finish();
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
                map.put("function","editPartSaleBill");
                map.put("id",idBill);
                map.put("paymentWay",strPaymentWay);
                map.put("barcode",strBarcode);
                map.put("count",strCount);
                map.put("discount",strDiscount);
                map.put("employeeId",employeeId);
                return map;
            }
        };
        Singleton.getSingleton(EditSaleBillActivity.this).setRequestQue(stringRequest);
    }


    private void getPictureDrug(){

        StringRequest stringRequest =new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        if(!jsonArray.equals(null)){
                            jsonObject = jsonArray.getJSONObject(0);
                            strPictureName = jsonObject.getString("imagePath");
                            PicassoClient.downloadimage(EditSaleBillActivity.this,strPictureName,circleImageView);
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
                Map<String,String> map = new HashMap<>();
                map.put("function","getDrug");
                map.put("barcode",strBarcode);
                return map;
            }
        };
        Singleton.getSingleton(EditSaleBillActivity.this).setRequestQue(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == SELECTED_BARCODE){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    strBarcode = barcode.displayValue;
                    barcodeTextView.setText(strBarcode);
                    getPictureDrug();
                }
            }

        }
    }
}
