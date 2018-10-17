package com.inclass06.inclass06;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThreadViewActivity extends AppCompatActivity {
    String token;
    int user_id;
    String user_email;
    String user_fname;
    String user_lname;
    String user_role;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    CustomAdapter customAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);


        //check for user credentials
        token = getIntent().getStringExtra("token");
        user_id = getIntent().getIntExtra("user_id", 0);
        user_email = getIntent().getStringExtra("user_email");
        user_fname = getIntent().getStringExtra("user_fname");
        user_lname = getIntent().getStringExtra("user_lname");
        user_role = getIntent().getStringExtra("user_role");

        if (token == "" || user_id == 0 || user_email  == "" || user_fname  == "" || user_lname  == "" || user_role == "") {
            Intent i = new Intent(ThreadViewActivity.this, MainActivity.class);
            finish();
            startActivity(i);
        }

        //set user name header
        ((TextView)findViewById(R.id.txt_name)).setText(user_fname + " " + user_lname);

        //load all thread
        ListView listView = (ListView) findViewById(R.id.listview);
        listItems = new ArrayList<String>();
        customAdapter = new CustomAdapter(token, user_id);
        listView.setAdapter(customAdapter);

        //logout
        findViewById(R.id.btn_signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set user data to null
                token = null;
                user_id = 0;
                user_email = null;
                user_fname = null;
                user_lname = null;
                user_role = null;

                //start up the login screen
                Intent i = new Intent(ThreadViewActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        //add thread
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Thread Add: ", "Button Click");
                String thread = ((TextView)findViewById(R.id.txt_addthread)).getText().toString();
                if (thread == ""){
                    Toast.makeText(ThreadViewActivity.this, "Add thread name!", Toast.LENGTH_LONG).show();

                }else{
                    addThread(thread, token);
                }
            }
        });
    }


    public void addThread(String title, final String token){

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("title", title)
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread/add")
                .addHeader("Authorization", "BEARER " + token)
                .post(requestBody)
                .build();

        Log.d("add thread", "going into");

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("OkHTTP", "error in getting response using async okhttp call");
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }

                CreateThreadResponse resp = new Gson().fromJson(response.body().string(), new TypeToken<CreateThreadResponse>(){}.getType());
                Log.i("OKHTTP", resp.toString());
                if (resp.status.equals("ok")){
                    customAdapter.addItem(resp.getThread());
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            customAdapter.notifyDataSetChanged();
                        }
                    });

                    Toast.makeText(ThreadViewActivity.this, "Thread Created!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ThreadViewActivity.this, "Failed to create thread!", Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }
        });
    }
}
