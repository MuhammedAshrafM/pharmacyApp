package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

public class SalesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManagerRecycler;
    Bill bill;
    String billNumbers, strBarcode, billNumber;
    ArrayList<Bill> billArrayList;
    SwipeRefreshLayout refreshLayout;

    public List<String> selectedBills, billNumbersList;
    RecyclerViewBillAdapter adapterRecycler;
    SearchView searchView;
    static final int SELECTED_BARCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(SalesActivity.this,AddSaleBillActivity.class);
                startActivity(intent);
            }
        });


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewSale);
        layoutManagerRecycler =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        selectedBills = new ArrayList<>();
        billNumbersList = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.salesRefreshLayout);
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
                        billNumbersList = new ArrayList<>();
                        if(!jsonArray.equals(null)){
                            for(int i=0;i<jsonArray.length();i++){
                                jsonObject = jsonArray.getJSONObject(i);

                                bill =new Bill();
                                bill.setIdBill(jsonObject.getString("idBill"));

                                billNumber = jsonObject.getString("billNumber");
                                bill.setBillNumber(billNumber);

                                bill.setDateBill(jsonObject.getString("dateBill"));
                                bill.setPaymentWayBill(jsonObject.getString("paymentWayBill"));
                                bill.setCountDrug(jsonObject.getString("countDrug"));
                                bill.setBarcodeDrug(jsonObject.getString("barcodeDrug"));
                                bill.setEnglishNameDrug(jsonObject.getString("englishNameDrug"));
                                bill.setArabicNameDrug(jsonObject.getString("arabicNameDrug"));
                                bill.setPriceDrug(jsonObject.getString("priceDrug"));
                                bill.setIdEmployee(jsonObject.getString("idEmployee"));
                                bill.setNameEmployee(jsonObject.getString("nameEmployee"));
                                bill.setTypeEmployee(jsonObject.getString("typeEmployee"));

                                if(! billNumbersList.contains(billNumber)){
                                    billNumbersList.add(billNumber);
                                    billArrayList.add(bill);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterRecycler=new RecyclerViewBillAdapter(SalesActivity.this, billArrayList);
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
                map.put("function","getSaleBills");
                map.put("billNumber"," ");
                return map;
            }
        };

        Singleton.getSingleton(SalesActivity.this).setRequestQue(stringRequest);
    }

    private void deleteSelectedBills(){
        billNumbers = "";

        for (int i = 0; i < selectedBills.size(); i++) {
            if (i == 0) {
                billNumbers += selectedBills.get(i);
            } else {
                billNumbers += " " + selectedBills.get(i);
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
                map.put("function", "deleteSelectedSaleBills");
                map.put("billNumbers", billNumbers);
                return map;
            }
        };

        Singleton.getSingleton(SalesActivity.this).setRequestQue(stringRequest);
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


    private List<Bill> filter (List<Bill> pl, String query){

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
            final String billNumber = bill.getBillNumber().toLowerCase();
            final String dateBill = bill.getDateBill();
            if(billNumber.startsWith(query) || dateBill.startsWith(query)){
                filterModeList.add(bill);
            }
        }
        return filterModeList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bill_menu,menu);
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView)item.getActionView();

        searchView.setQueryHint("Number or Date Bill");
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
                selectedBills = RecyclerViewBillAdapter.selectedBills;
                if(selectedBills.size() != 0) {
                    confirmDelete();
                }
                else {
                    Toast.makeText(this, "Select Any Bill to delete", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.edit:
                selectedBills = RecyclerViewBillAdapter.selectedBills;
                if(selectedBills.size() == 1) {
                    billNumbers = selectedBills.get(0);
                    Intent intent = new Intent(SalesActivity.this,SaleBillsActivity.class);
                    intent.putExtra("billNumber",billNumbers);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Select only one Bill to edit", Toast.LENGTH_SHORT).show();
                }
                break;
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


}
