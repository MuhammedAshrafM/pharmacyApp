package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseBillsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManagerRecycler;
    Bill bill;
    String billIds, strBarcode, billNumber;
    ArrayList<Bill> billArrayList;
    SwipeRefreshLayout refreshLayout;

    public List<String> selectedBills;
    RecyclerViewAllBillsAdapter adapterRecycler;
    SearchView searchView;
    static final int SELECTED_BARCODE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_bills);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewPurchase);
        layoutManagerRecycler =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        selectedBills = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.purchasesRefreshLayout);
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.backGround, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getJSON();
                    }
                }, 3000);
            }
        });

        intent = getIntent();
        billNumber = intent.getStringExtra("billNumber");

        getJSON();
    }


    @Override
    protected void onResume() {
        super.onResume();
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
                        billArrayList = new ArrayList<>();
                        if(!jsonArray.equals(null)){
                            for(int i=0;i<jsonArray.length()+1;i++){

                                if(i==0){
                                    bill = createSimpleBill();
                                }
                                else {
                                    jsonObject = jsonArray.getJSONObject(i-1);

                                    bill = new Bill();
                                    bill.setIdBill(jsonObject.getString("idBill"));
                                    bill.setBillNumber(jsonObject.getString("billNumber"));
                                    bill.setDateBill(jsonObject.getString("dateBill"));
                                    bill.setPaymentWayBill(jsonObject.getString("paymentWayBill"));
                                    bill.setDiscountBill(jsonObject.getString("discountBill"));
                                    bill.setCountDrug(jsonObject.getString("countDrug"));
                                    bill.setImagePathDrug(jsonObject.getString("imagePath"));
                                    bill.setBarcodeDrug(jsonObject.getString("barcodeDrug"));
                                    bill.setEnglishNameDrug(jsonObject.getString("englishNameDrug"));
                                    bill.setArabicNameDrug(jsonObject.getString("arabicNameDrug"));
                                    bill.setPriceDrug(jsonObject.getString("priceDrug"));
                                    bill.setIdEmployee(jsonObject.getString("idEmployee"));
                                    bill.setNameEmployee(jsonObject.getString("nameEmployee"));
                                    bill.setTypeEmployee(jsonObject.getString("typeEmployee"));
                                    bill.setIdCompany(jsonObject.getString("idCompany"));
                                    bill.setNameCompany(jsonObject.getString("nameCompany"));

                                }
                                billArrayList.add(bill);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterRecycler=new RecyclerViewAllBillsAdapter(PurchaseBillsActivity.this, billArrayList, "Purchase");
                    recyclerView.setAdapter(adapterRecycler);
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
                map.put("function","getPurchaseBills");
                map.put("billNumber",billNumber);
                return map;
            }
        };

        Singleton.getSingleton(PurchaseBillsActivity.this).setRequestQue(stringRequest);
    }

    private void deleteSelectedBills(){
        billIds = "";

        for (int i = 0; i < selectedBills.size(); i++) {
            if (i == 0) {
                billIds += selectedBills.get(i);
            } else {
                billIds += " " + selectedBills.get(i);
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getJSON();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("function", "deleteSelectedPartPurchaseBill");
                map.put("billIds", billIds);
                return map;
            }
        };

        Singleton.getSingleton(PurchaseBillsActivity.this).setRequestQue(stringRequest);
    }

    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDelete));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteSelectedBills();
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

    private Bill createSimpleBill(){
        bill = new Bill();
        bill.setIdBill("");
        bill.setBillNumber("");
        bill.setDateBill("");
        bill.setPaymentWayBill("");
        bill.setDiscountBill("");
        bill.setCountDrug("Count");
        bill.setImagePathDrug("");
        bill.setBarcodeDrug("Barcode");
        bill.setEnglishNameDrug("En Name");
        bill.setArabicNameDrug("Ar Name");
        bill.setPriceDrug("Price");
        bill.setIdEmployee("");
        bill.setNameEmployee("");
        bill.setTypeEmployee("");
        bill.setIdCompany("");
        bill.setNameCompany("");

        return bill;
    }

    private List<Bill> filter (List<Bill> pl, String query){
        int i=0;
        while (query.startsWith(" ")){
            adapterRecycler.setFilter(billArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return billArrayList;
            }
        }

        query = query.toLowerCase();
        final List<Bill> filterModeList = new ArrayList<>();
        for(Bill bill: pl){
            if(i==0){
                filterModeList.add(bill);
            }
            else {
                final String textName = bill.getEnglishNameDrug().toLowerCase();
                final String textBarcode = bill.getBarcodeDrug();
                if (textName.startsWith(query) || textBarcode.startsWith(query)) {
                    filterModeList.add(bill);
                }
            }
            i++;
        }
        return filterModeList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.list_view_menu,menu);
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView)item.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu=new PopupMenu(PurchaseBillsActivity.this,view);
                MenuInflater menuInflater=popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.type_search_menu,popupMenu.getMenu());
                PurchaseBillsActivity.PopupMenuEvent popupMenuEvent =new PurchaseBillsActivity.PopupMenuEvent(getApplicationContext());
                popupMenu.setOnMenuItemClickListener(popupMenuEvent);
                popupMenu.show();
            }
        });
        searchView.setQueryHint("Barcode or name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery(query,false);
                final List<Bill> filterModeList = filter(billArrayList,query);
                adapterRecycler.setFilter(filterModeList);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Bill> filterModeList = filter(billArrayList,newText);
                adapterRecycler.setFilter(filterModeList);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete:
                selectedBills = RecyclerViewAllBillsAdapter.selectedBills;
                if(selectedBills.size() != 0) {
                    confirmDelete();
                }
                else {
                    Toast.makeText(this, "Select Any Bill to delete", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_BARCODE) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    strBarcode = barcode.displayValue;
                    searchView.setQuery(strBarcode,false);
                }
            }
        }
    }


    public class PopupMenuEvent implements android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        Context context;

        public PopupMenuEvent(Context context){
            this.context=context;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId()==R.id.barcode){
                Intent intent = new Intent(PurchaseBillsActivity.this,ScanActivity.class);
                startActivityForResult(intent,SELECTED_BARCODE);
                return true;
            }
            else if(item.getItemId()==R.id.englishName){
                return true;
            }

            return false;
        }
    }
}
