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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManagerRecycler;
    Employee employee;
    String employeeIds;
    RecyclerViewEmployeeAdapter adapterRecycler;
    ArrayList<Employee> employeeArrayList;
    SwipeRefreshLayout refreshLayout;

    public List<String> selectedEmployees;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(EmployeesActivity.this,AddEmployeeActivity.class);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewEmployee);
        layoutManagerRecycler =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        selectedEmployees = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.employeesRefreshLayout);
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
                        employeeArrayList = new ArrayList<>();
                        if(!jsonArray.equals(null)){
                            for(int i=0;i<jsonArray.length();i++){
                                jsonObject = jsonArray.getJSONObject(i);

                                employee =new Employee();
                                employee.setId(jsonObject.getString("id"));
                                employee.setImagePath(jsonObject.getString("imagePath"));
                                employee.setName(jsonObject.getString("name"));
                                employee.setBirthday(jsonObject.getString("birthday"));
                                employee.setAddress(jsonObject.getString("address"));
                                employee.setPhoneNumber(jsonObject.getString("phoneNumber"));
                                employee.setUserName(jsonObject.getString("userName"));
                                employee.setPassword(jsonObject.getString("password"));
                                employee.setType(jsonObject.getString("type"));
                                employee.setGender(jsonObject.getString("gender"));
                                employee.setSalary(jsonObject.getString("salary"));

                                employeeArrayList.add(employee);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapterRecycler = new RecyclerViewEmployeeAdapter(EmployeesActivity.this, employeeArrayList);
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
                map.put("function","getEmployees");
                return map;
            }
        };

        Singleton.getSingleton(EmployeesActivity.this).setRequestQue(stringRequest);
    }

    private void deleteSelectedEmployees(){
            employeeIds = "";

            for (int i = 0; i < selectedEmployees.size(); i++) {
                if (i == 0) {
                    employeeIds += selectedEmployees.get(i);
                } else {
                    employeeIds += " " + selectedEmployees.get(i);
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
                    map.put("function", "deleteSelectedEmployees");
                    map.put("employeeIds", employeeIds);
                    return map;
                }
            };

            Singleton.getSingleton(EmployeesActivity.this).setRequestQue(stringRequest);
    }

    private void confirmDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmDelete));

        alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteSelectedEmployees();
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



    private List<Employee> filter (List<Employee> pl, String query){
        while (query.startsWith(" ")){
            adapterRecycler.setFilter(employeeArrayList);
            if(query.length() != 1){
                query = query.substring(1);
            }
            else {
                return employeeArrayList;
            }
        }
        query = query.toLowerCase();
        final List<Employee> filterModeList = new ArrayList<>();
        for(Employee employee: pl){
            final String textName = employee.getName().toLowerCase();
            final String textUserName = employee.getUserName().toLowerCase();
            final String textType = employee.getType().toLowerCase();
            // can using contains method instead of startsWith method
            if(textName.startsWith(query) || textUserName.startsWith(query) || textType.startsWith(query)){
                filterModeList.add(employee);
            }
        }
        return filterModeList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.list_view_menu,menu);
        final MenuItem item = menu.findItem(R.id.search);

        searchView = (SearchView)item.getActionView();

        searchView.setQueryHint("Name or userName ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery(query,false);
                final List<Employee> filterModeList = filter(employeeArrayList,query);
                adapterRecycler.setFilter(filterModeList);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Employee> filterModeList = filter(employeeArrayList,newText);
                adapterRecycler.setFilter(filterModeList);
                return true;
            }
        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                final List<Employee> filterModeList = filter(employeeArrayList,"");
//                adapterRecycler.setFilter(filterModeList);
//                return true;
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete:
                selectedEmployees = RecyclerViewEmployeeAdapter.selectedEmployees;
                if(selectedEmployees.size() != 0) {
                    confirmDelete();
                }
                else {
                    Toast.makeText(this, "Select Any Employee to delete", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }

}
