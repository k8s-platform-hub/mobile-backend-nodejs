package com.jaison.app_android.data;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jaison.app_android.BaseActivity;
import com.jaison.app_android.Hasura;
import com.jaison.app_android.R;
import com.jaison.app_android.auth.AuthActivity;

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

public class DataActivity extends BaseActivity {

    EditText name, gender;
    TextView message;
    RecyclerView recyclerView;
    EducationAdapter adapter;

    private static String TAG = "DataActivity";

    public static void startActivity(Activity startingActivity) {
        startingActivity.startActivity(new Intent(startingActivity, DataActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        name = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EducationAdapter();
        recyclerView.setAdapter(adapter);
        message = findViewById(R.id.message);
        if (Hasura.getSessionToken(DataActivity.this) == null) {
            message.setText("You need to be logged in to view user details");
            return;
        }
        message.setVisibility(View.GONE);

        try {
            String url = Hasura.Config.DATA_URL + "query";

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonObject = new JSONObject()
                    .put("type", "select")
                    .put("args", new JSONObject()
                            .put("table", "user_details")
                            .put("columns", new JSONArray()
                                    .put("user_id")
                                    .put("name")
                                    .put("gender")
                                    .put(new JSONObject()
                                            .put("name", "education")
                                            .put("columns", new JSONArray()
                                                    .put("institution_name")
                                                    .put("degree")
                                                    .put("user_id")
                                                    .put("id")
                                            )
                                    )
                            )
                    );

            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + Hasura.getSessionToken(this))
                    .build();
            showProgressDialog("Fetching user details");
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    dismissProgressDialog();
                    //Handle failure
                    showToast("Failed to fetch user details: " + e.getLocalizedMessage());
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
                                    try {
                                        if (array.length() > 0) {
                                            JSONObject responseObject = array.getJSONObject(0);
                                            name.setText(responseObject.getString("name"));
                                            gender.setText(responseObject.getString("gender"));

                                            adapter.setData(responseObject.getJSONArray("education"));
                                            adapter.notifyDataSetChanged();
                                        }
                                    } catch (JSONException e) {

                                    }
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

    class EducationView extends RecyclerView.ViewHolder {

        public TextView institutionName, degree;

        public EducationView(View itemView) {
            super(itemView);
            institutionName = itemView.findViewById(R.id.institution);
            degree = itemView.findViewById(R.id.degree);
        }
    }


    class EducationAdapter extends RecyclerView.Adapter<EducationView> {

        JSONArray array = new JSONArray();

        public void setData(JSONArray array) {
            this.array = array;
        }

        @Override
        public EducationView onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_education, parent, false);
            return new EducationView(view);
        }

        @Override
        public void onBindViewHolder(EducationView holder, int position) {
            try {
                JSONObject education = array.getJSONObject(position);
                holder.institutionName.setText(education.getString("institution_name"));
                holder.degree.setText(education.getString("degree"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }
    }
}
