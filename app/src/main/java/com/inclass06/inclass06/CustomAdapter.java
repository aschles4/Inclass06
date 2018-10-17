package com.inclass06.inclass06;

import android.app.Activity;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class CustomAdapter extends BaseAdapter {
    private static final String TAG = CustomAdapter.class.getSimpleName();
    ArrayList<Thread> listArray;
    int userId;
    String token;

    public CustomAdapter(String token, int userId) {
        this.userId = userId;
        this.token = token;
        listArray = new ArrayList<Thread>();
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread")
                .addHeader("Authorization", "BEARER " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("OkHTTP", "error in getting response using async okhttp call");
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                Looper.prepare();
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }

                final GetThreadResponse getThreadResponse = new Gson().fromJson(response.body().string(), new TypeToken<GetThreadResponse>(){}.getType());
                Log.i("OKHTTP", getThreadResponse.getStatus().toString());
                if (getThreadResponse.status.equals("ok")){
                    //Log.d("thread", getThreadResponse.getThreads().get(i).toString());
                    for(int i = 0; i < getThreadResponse.getThreads().size(); i++){
                        listArray.add(getThreadResponse.getThreads().get(i));
                    }
                }else{
                    //Toast.makeText(CustomAdapter.this, "Could not get list of threads", Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }
        });
    }

    @Override
    public int getCount() {
        return listArray.size();    // total number of elements in the list
    }

    @Override
    public Object getItem(int i) {
        return listArray.get(i);    // single item in the list
    }

    @Override
    public long getItemId(int i) {
        return i;                   // index number
    }

    public void addItem(Thread thread){
        listArray.add(thread);
    }

    @Override
    public View getView(int index, View view, final ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.listitem, parent, false);
        }

        final  Thread thread = listArray.get(index);

        TextView textView = (TextView) view.findViewById(R.id.tv_string_data);
        textView.setText(thread.getTitle());

        ImageView button = (ImageView) view.findViewById(R.id.imageView);

        if (thread.getUser_id() != userId) {
            button.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/thread/delete/" + thread.getId())
                        .addHeader("Authorization", "BEARER " + token)
                        .get()
                        .build();

                Log.d("delete thread", "going into");

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
                            Toast.makeText(parent.getContext(), "Thread Deleted!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(parent.getContext(), "Failed to create thread!", Toast.LENGTH_LONG).show();
                        }
                        Looper.loop();
                    }
                });

                listArray.remove(thread);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
