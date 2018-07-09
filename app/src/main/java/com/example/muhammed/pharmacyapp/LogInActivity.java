package com.example.muhammed.pharmacyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class LogInActivity extends AppCompatActivity {


    EditText userNameEditText,passwordEditText;
    Button loginButton;
    ProgressDialog progressDialog;
    SharedPreferences shredPreferences;
    SharedPreferences.Editor editor;
    TextInputLayout inputLayoutUserName,inputLayoutPassword;
    Vibrator vibrator;
    Animation animation;
    String userName,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        userNameEditText = (EditText)findViewById(R.id.userNameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        loginButton = (Button)findViewById(R.id.login);
        inputLayoutUserName = (TextInputLayout)findViewById(R.id.inputLayoutUserName);
        inputLayoutPassword = (TextInputLayout)findViewById(R.id.inputLayoutPassword);

        animation = AnimationUtils.loadAnimation(this,R.anim.shake);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressDialog=new ProgressDialog(this);
        shredPreferences = getSharedPreferences("UserLogin",MODE_PRIVATE);
        editor = shredPreferences.edit();

        autoLogin();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {

        if (!checkUserName()) {
            userNameEditText.setAnimation(animation);
            userNameEditText.startAnimation(animation);
            vibrator.vibrate(120);
            return;
        }
        if (!checkPassword()) {
            passwordEditText.setAnimation(animation);
            passwordEditText.startAnimation(animation);
            vibrator.vibrate(120);
            return;
        }

        inputLayoutUserName.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);

        logIn();

    }

    private boolean checkUserName() {
        userName = userNameEditText.getText().toString().trim();
        if (userName.isEmpty()) {
            inputLayoutUserName.setErrorEnabled(true);
            inputLayoutUserName.setError(getString(R.string.err_msg_user_name));
            userNameEditText.setError(getString(R.string.err_msg_required));
            requestFocus(userNameEditText);
            return false;
        }
        inputLayoutUserName.setErrorEnabled(false);
        return true;
    }

    private boolean checkPassword() {
        password = passwordEditText.getText().toString();
        if (password.startsWith(" ")||password.endsWith(" "))
        {
            inputLayoutPassword.setErrorEnabled(true);
            passwordEditText.setError(getString(R.string.err_msg_required));
            requestFocus(passwordEditText);
            if(password.isEmpty()) {
                inputLayoutPassword.setError(getString(R.string.err_msg_password));
            }
            else if (password.startsWith(" ")){
                inputLayoutPassword.setError(getString(R.string.err_msg_password_beginning_space));
            }
            else {
                inputLayoutPassword.setError(getString(R.string.err_msg_password_end_spaces));
            }
            return false;
        }
        if(password.isEmpty()) {
            inputLayoutPassword.setErrorEnabled(true);
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            passwordEditText.setError(getString(R.string.err_msg_required));
            return false;
        }
        inputLayoutPassword.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void autoLogin()
    {
        userName = shredPreferences.getString("userName","");
        password = shredPreferences.getString("password","");
        if(!(userName.isEmpty())&&!(password.isEmpty()))
        {
            startActivity(new Intent(LogInActivity.this,HomeActivity.class));
        }
    }
    private void saveDataUser(String id,String image_path,String name,String type)
    {
        editor.putString("userName", userNameEditText.getText().toString().trim()).commit();
        editor.putString("password",passwordEditText.getText().toString().trim()).commit();
        editor.putString("id",id).commit();
        editor.putString("imagePath",image_path).commit();
        editor.putString("name",name).commit();
        editor.putString("type",type).commit();
    }

    private void logIn(){

        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.URL_PHARMACY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null)
                {

                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        if(!(jsonArray.isNull(0)))
                        {
                            JSONObject user_object=jsonArray.getJSONObject(0);
                            String id=user_object.getString("id");
                            String image_path=user_object.getString("imagePath");
                            String name=user_object.getString("name");
                            String type=user_object.getString("type");
                            Toast.makeText(LogInActivity.this,getString(R.string.loginSuccessfully),Toast.LENGTH_LONG).show();
                            Intent user_intent=new Intent(LogInActivity.this,HomeActivity.class);
                            progressDialog.dismiss();
                            saveDataUser(id,image_path,name,type);
                            startActivity(user_intent);
                            passwordEditText.setText("");
                        }
                        else
                        {
                            Toast.makeText(LogInActivity.this,getString(R.string.loginFailed),Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mapData=new HashMap<>();
                mapData=new HashMap<>();
                mapData.put("function","logIn");
                mapData.put("userName",userName);
                mapData.put("password",password);
                return mapData;

            }
        };

        Singleton.getSingleton(LogInActivity.this).setRequestQue(stringRequest);

    }
}
