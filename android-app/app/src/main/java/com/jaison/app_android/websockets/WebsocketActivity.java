package com.jaison.app_android.websockets;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaison.app_android.Hasura;
import com.jaison.app_android.R;
import com.jaison.app_android.data.DataActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class WebsocketActivity extends AppCompatActivity {

    Socket socket;
    RecyclerView recyclerView;
    EditText message;
    Button sendButton;
    MessageAdapter adapter;

    private static final String TAG = "WebsocketActivity";
    private static final String SOCKET_EVENT = "message";


    public static void startActivity(Activity startingActivity) {
        startingActivity.startActivity(new Intent(startingActivity, WebsocketActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);

        message = findViewById(R.id.message);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageString = message.getText().toString().trim();
                if (!messageString.isEmpty()) {
                    socket.emit(SOCKET_EVENT, messageString);
                    message.setText("");
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        try {

            socket = IO.socket(Hasura.Config.CUSTOM);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Connected");
                            Toast.makeText(WebsocketActivity.this, "Connected", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).on(SOCKET_EVENT, new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = (String) args[0];
                            adapter.addMessage(message);
                        }
                    });
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Disconnected");
                            Toast.makeText(WebsocketActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "Connection Error: " + args[0]);
                            Toast.makeText(WebsocketActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView message;

        public MessageViewHolder(View itemView) {
            super(itemView);

            message = (TextView) itemView;
        }
    }


    class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        List<String> messages = new ArrayList<>();

        public void addMessage(String s) {
            messages.add(s);
            notifyDataSetChanged();
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MessageViewHolder(new TextView(WebsocketActivity.this));
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            holder.message.setText(messages.get(position));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
