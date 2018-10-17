package com.inclass06.inclass06;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //signup
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get user information
                String fname = ((TextView)findViewById(R.id.firstName)).getText().toString();
                String lname = ((TextView)findViewById(R.id.lastName)).getText().toString();
                String pass1 = ((TextView)findViewById(R.id.email)).getText().toString();
                String pass2 = ((TextView)findViewById(R.id.password1)).getText().toString();
                String email = ((TextView)findViewById(R.id.password2)).getText().toString();
                //log user in
                if (pass1.equals(pass2)) {
                    signup(email, pass1, fname, lname);
                }else{
                    Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //cancel
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start up the signup page and finish main activity
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
    }

    public void signup(String email, String password, String fName, String lName){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .addFormDataPart("fname", fName)
                .addFormDataPart("lname", lName)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("OkHTTP", "error in getting response using async okhttp call");
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }

                LoginResponse resp = new Gson().fromJson(response.body().string(), new TypeToken<LoginResponse>(){}.getType());
                if (resp != null) {
                    if (resp.status.equals("error")){
                        Toast.makeText(SignupActivity.this, resp.message, Toast.LENGTH_LONG).show();
                    }else{
                        //send intent to message threads activity
                        Intent i = new Intent(SignupActivity.this, ThreadViewActivity.class);
                        i.putExtra("token", resp.getToken());
                        i.putExtra("user_id", resp.getUser_id());
                        i.putExtra("user_email", resp.getUser_email());
                        i.putExtra("user_fname", resp.getUser_fname());
                        i.putExtra("user_lname", resp.getUser_lname());
                        i.putExtra("user_role", resp.getUser_role());
                        startActivity(i);
                        finish();
                        //finish this view
                    }
                } else {
                    //if fail toast failed to login
                    Toast.makeText(SignupActivity.this, "Signup failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
