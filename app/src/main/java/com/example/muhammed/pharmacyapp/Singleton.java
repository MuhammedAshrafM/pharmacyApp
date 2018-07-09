package com.example.muhammed.pharmacyapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by Mu7ammed_A4raf on 19-Jan-18.
 */

public class Singleton {

    private static Singleton singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private Singleton(Context context){
        this.context =context;
        requestQueue=getRequestQue();
    }

    public RequestQueue getRequestQue(){
        if(this.requestQueue==null){
            requestQueue= Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static synchronized Singleton getSingleton (Context context){

        if(singleton==null){
            singleton=new Singleton(context);
        }
        return singleton;
    }

    public<T> void setRequestQue(Request<T> request){
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(request);
    }
}