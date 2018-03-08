package com.jaison.app_android.data;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaison.app_android.BaseActivity;
import com.jaison.app_android.Hasura;
import com.jaison.app_android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArticleListActivity extends BaseActivity {

    ArticleListAdapter adapter;
    private static String TAG = "ArticleListActivity";

    public static void startActivity(Activity startingActivity) {
        startingActivity.startActivity(new Intent(startingActivity, ArticleListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new ArticleListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchArticles();

    }

    private void fetchArticles() {
        //Fetch articles
        String url = Hasura.Config.DATA_URL + "query";
        try {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonObject = new JSONObject()
                    .put("type", "select")
                    .put("args", new JSONObject()
                            .put("table", "article")
                            .put("columns", new JSONArray()
                                    .put("*") //to fetch all columns
                            )
                    );

            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            showProgressDialog("Fetching articles");

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    dismissProgressDialog();
                    //Handle failure
                    showToast("Failed to fetch articles: " + e.getLocalizedMessage());
                    Log.d(TAG, e.getLocalizedMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    dismissProgressDialog();
                    //Handle success
                    String jsonResponse = response.body().string();
                    if (response.isSuccessful()) {
                        try {
                            //You can also use GSON to convert it to a POJO
                            final JSONArray array = new JSONArray(jsonResponse);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setArray(array);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("Failed to parse response: " + jsonResponse + "\n error: " + e.getLocalizedMessage());
                        }
                    } else {
                        showToast("Fetching articles failed: " + jsonResponse);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ArticleListViewHolder extends RecyclerView.ViewHolder {

        public TextView title, content;

        public ArticleListViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
        }
    }

    public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {

        JSONArray array;

        public ArticleListAdapter() {
            array = new JSONArray();
        }

        public void setArray(JSONArray array) {
            this.array = array;
        }

        @Override
        public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_article, parent, false);
            return new ArticleListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ArticleListViewHolder holder, int position) {
            try {
                JSONObject jsonObject = array.getJSONObject(position);
                holder.title.setText(jsonObject.getString("title"));
                holder.content.setText(jsonObject.getString("content"));
            } catch (JSONException e) {
                throw new RuntimeException("Unable to parse json at recycler view adapter");
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }
    }
}
