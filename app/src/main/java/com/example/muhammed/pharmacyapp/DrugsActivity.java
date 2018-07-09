package com.example.muhammed.pharmacyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class DrugsActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManagerRecycler;
    Drug drug;
    String drugIds, strBarcode;
    ArrayList<Drug> drugArrayList;
    SwipeRefreshLayout refreshLayout;

    public List<String> selectedDrugs;
    RecyclerViewDrugAdapter adapterRecycler;
    SearchView searchView;
    static final int SELECTED_BARCODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(DrugsActivity.this,AddDrugActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewDrug);
        layoutManagerRecycler =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        selectedDrugs = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.drugsRefreshLayout);
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
                        drugArrayList = new ArrayList<>();
                        if(!jsonArray.equals(null)){
                            for(int i=0;i<jsonArray.length();i++){
                                jsonObject = jsonArray.getJSONObject(i);

                                drug =new Drug();
                                drug.setId(jsonObject.getString("id"));
                                drug.setImagePath(jsonObject.getString("imagePath"));
                                drug.setBarcode(jsonObject.getString("barcode"));
                                drug.setEnglishName(jsonObject.getString("englishName"));
                                drug.setArabicName(jsonObject.getString("arabicName"));
                                drug.setAmount(jsonObject.getString("amount"));
                                drug.setNumBar(jsonObject.getString("numBar"));
                                drug.setPrice(jsonObject.getString("price"));
                                drug.setDiscount(jsonObject.getString("discount"));
                                drug.setDepartment(jsonObject.getString("department"));
                                drug.setProductionDate(jsonObject.getString("productionDate"));
                                drug.setExpiryDate(jsonObject.getString("expiryDate"));
                                drug.setCompanyId(jsonObject.getString("companyId"));

                                drugArrayList.add(drug);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterRecycler=new RecyclerViewDrugAdapter(DrugsActivity.this, drugArrayList);
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
                map.put("function","getDrugs");
                return map;
            }
        };

        Singleton.getSingleton(DrugsActivity.this).setRequestQue(stringRequest);
    }

    private void deleteSelectedDrugs(){
        drugIds = "";

        for (int i = 0; i < selectedDrugs.size(); i++) {
            if (i == 0) {
                drugIds += selectedDrugs.get(i);
            } else {
                drugIds += " " + selectedDrugs.get(i);
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
                map.put("function", "deleteSelectedDrugs");
                map.put("drugIds", drugIds);
                return map;
            }
        };

        Singleton.getSingleton(DrugsActivity.this).setRequestQue(stringRequest);
    }

    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDelete));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteSelectedDrugs();
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

    private List<Drug> filter (List<Drug> pl, String query){

        while (query.startsWith(" ")){
            adapterRecycler.setFilter(drugArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return drugArrayList;
            }
        }

        query = query.toLowerCase();
        final List<Drug> filterModeList = new ArrayList<>();
        for(Drug drug: pl){
            final String textName = drug.getEnglishName().toLowerCase();
            final String textBarcode = drug.getBarcode();
            if(textName.startsWith(query) || textBarcode.startsWith(query)){
                filterModeList.add(drug);
            }
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

                PopupMenu popupMenu=new PopupMenu(DrugsActivity.this,view);
                MenuInflater menuInflater=popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.type_search_menu,popupMenu.getMenu());
                PopupMenuEvent popupMenuEvent =new PopupMenuEvent(getApplicationContext());
                popupMenu.setOnMenuItemClickListener(popupMenuEvent);
                popupMenu.show();
            }
        });
        searchView.setQueryHint("Barcode or name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery(query,false);
                final List<Drug> filterModeList = filter(drugArrayList,query);
                adapterRecycler.setFilter(filterModeList);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Drug> filterModeList = filter(drugArrayList,newText);
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
                selectedDrugs = RecyclerViewDrugAdapter.selectedDrugs;
                if(selectedDrugs.size() != 0) {
                    confirmDelete();
                }
                else {
                    Toast.makeText(this, "Select Any Drug to delete", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(DrugsActivity.this,ScanActivity.class);
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
