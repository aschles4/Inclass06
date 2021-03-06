package com.inclass06.inclass06;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //login
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get user information
                String email = ((TextView)findViewById(R.id.email)).getText().toString();
                String pass = ((TextView)findViewById(R.id.password)).getText().toString();
                //log user in
                login(email, pass);
            }
        });

        //signup
        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start up the signup page and finish main activity
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void login(String email, String password){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
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
                Log.i("OKHTTP", resp.toString());
                Log.d("main", "finished call");
                if (resp != null) {
                    Log.d("main", "resp not null");
                    if (resp.status == "error"){
                        Toast.makeText(MainActivity.this, resp.message, Toast.LENGTH_LONG).show();
                    }else{
                        //send intent to message threads activity
                        Intent i = new Intent(MainActivity.this, ThreadViewActivity.class);
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
                }else{
                    //if fail toast failed to login
                    Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
