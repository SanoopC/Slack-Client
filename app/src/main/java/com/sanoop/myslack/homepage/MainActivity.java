package com.sanoop.myslack.homepage;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanoop.myslack.Models.ChannelAdapterModel;
import com.sanoop.myslack.Models.ChannelInfo;
import com.sanoop.myslack.Models.ChannelInfoResponse;
import com.sanoop.myslack.Models.RtmConnectResponse;
import com.sanoop.myslack.Models.SlackDetails;
import com.sanoop.myslack.Models.UserInfo;
import com.sanoop.myslack.R;
import com.sanoop.myslack.rest.ApiClient;
import com.sanoop.myslack.rest.ApiInterface;
import com.sanoop.myslack.rest.MyConfig;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChannelAdapter adapter;
    private List<ChannelAdapterModel> channelList;
    private ImageView sendButton;
    private String mBotId;
    private RtmConnectResponse rtmConnectResponse;
    private SlackDetails slackSlackDetails;
    private ChannelInfoResponse channelInfoResponse;
    private WebSocket mWebSocket;
    private Request request;
    private MyWebSocketListener myWebSocketListener;
    private OkHttpClient client;
    private JSONObject rtmObject;
    private String messageType;
    private HashMap<String, ChannelInfo> channelsWithAccess;
    private HashMap<String, String> activeUsers;
    private String[] availableChannels;
    private int numberOfChannels = 0;
    private Iterator<String> myVeryOwnIterator;
    private EditText messageEditText;
    private int i;
    private AlertDialog.Builder builder;
    private String channelName;
    private ChannelAdapterModel channelAdapterModel;
    private String replyTo;
    private String selectedChannel;
    private ApiInterface apiService;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    private final class MyWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            mWebSocket = webSocket;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            handleMessage(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            handleMessage(bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            try {
                client.dispatcher().cancelAll();// to cancel all requests
                connectSocket();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "onFailure : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
            mWebSocket = null;
        }
    }

    private void handleMessage(final String json) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (json != null) {
                        messageType = null;
                        replyTo = null;
                        rtmObject = new JSONObject(json);
                        if (rtmObject.has("type"))
                            messageType = rtmObject.getString("type");
                        if (rtmObject.has("reply_to"))
                            replyTo = rtmObject.getString("reply_to");
                        if (messageType != null) {
                            if (messageType.equalsIgnoreCase(String.valueOf(getString(R.string.event_hello)))) {
                                Log.d("SocketConnection", "Connection Completed");
                            } else if (messageType.equalsIgnoreCase(String.valueOf(getString(R.string.event_message)))) {
                                updateRecyclerView(rtmObject.getString("text"),
                                        rtmObject.getString("user"),
                                        rtmObject.getString("channel"));
                            }
                        } else if (replyTo != null && !replyTo.equals("")) {
                            updateRecyclerView(rtmObject.getString("text"),
                                    slackSlackDetails.getSelfInfo().getSelfId(),
                                    channelsWithAccess.get(selectedChannel).getChannelId());
                        }
                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                }
            }
        });
    }

    private void updateRecyclerView(String text, String userID, String channelID) {
        for (i = 0; i < channelList.size(); i++) {
            if (channelList.get(i).getChannelID().equals(channelID)) {
                myVeryOwnIterator = channelsWithAccess.keySet().iterator();
                while (myVeryOwnIterator.hasNext()) {
                    final String key = myVeryOwnIterator.next();
                    if (channelsWithAccess.get(key).getChannelId().equals(channelID))
                        channelName = key;
                }
                channelAdapterModel = new ChannelAdapterModel(text, activeUsers.get(userID), channelName,
                        channelList.get(i).getChannelID());
                channelList.set(i, channelAdapterModel);
                adapter.notifyItemChanged(i);
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);
        progressBar = findViewById(R.id.progress_bar);
        noDataTextView = findViewById(R.id.no_data_text_view);
        rtmConnectResponse = new RtmConnectResponse();
        slackSlackDetails = new SlackDetails();
        channelList = new ArrayList<>();
        adapter = new ChannelAdapter(this, channelList);
        client = new OkHttpClient();
        channelsWithAccess = new HashMap<>();
        activeUsers = new HashMap<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if (isNetworkAvailable())
            getSlackDetails();
        else {
            noDataTextView.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    if (!messageEditText.getText().toString().trim()
                            .equals("")) {
                        showChannelDialog();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getSlackDetails() {
        progressBar.setVisibility(View.VISIBLE);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SlackDetails> call = apiService.getSlackResources("Bearer " + MyConfig.BOT_TOKEN);
        call.enqueue(new Callback<SlackDetails>() {
            @Override
            public void onResponse(Call<SlackDetails> call, Response<SlackDetails> response) {
                if (response.isSuccessful()) {
                    slackSlackDetails = response.body();
                    if (slackSlackDetails != null) {
                        if (slackSlackDetails.getWebsocketUrl() != null)
                            startWebSocket(slackSlackDetails.getWebsocketUrl());
                        if ((slackSlackDetails.getChannelInfoList() != null &&
                                slackSlackDetails.getChannelInfoList().size() > 0))
                            generateChannelsWithAccess(slackSlackDetails.getChannelInfoList());
                        if ((slackSlackDetails.getUserInfoList() != null &&
                                slackSlackDetails.getUserInfoList().size() > 0))
                            generateActiveUsers(slackSlackDetails.getUserInfoList());
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<SlackDetails> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWebSocket(String URL) {
        try {
            request = new Request.Builder().url(URL).build();
            myWebSocketListener = new MyWebSocketListener();
            WebSocket ws = client.newWebSocket(request, myWebSocketListener);
        } catch (Exception e) {
            Log.e("WebSocket_exception : ", e.getMessage());
        }
    }

    public void generateChannelsWithAccess(List<ChannelInfo> channels) {
        channelsWithAccess.clear();
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getMember()) {
                channelsWithAccess.put(channels.get(i).getChannelName(), channels.get(i));
                numberOfChannels++;
                fetchChannelLatestMessages(channels.get(i).getChannelId());
            }
        }
    }

    public void fetchChannelLatestMessages(String channelID) {
        progressBar.setVisibility(View.VISIBLE);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ChannelInfoResponse> call = apiService.getChannelInfo("Bearer " + MyConfig.BOT_TOKEN,
                channelID);
        call.enqueue(new Callback<ChannelInfoResponse>() {
            @Override
            public void onResponse(Call<ChannelInfoResponse> call, Response<ChannelInfoResponse> response) {
                if (response.isSuccessful()) {
                    ChannelInfo channelInfo = response.body().getChannelInfo();
                    channelAdapterModel = new ChannelAdapterModel(
                            channelInfo.getLatestMessage().getTextMessage(),
                            activeUsers.get(channelInfo.getLatestMessage().getUserID()),
                            channelInfo.getChannelName(), channelInfo.getChannelId());
                    channelList.add(channelAdapterModel);
                    adapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ChannelInfoResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void generateActiveUsers(List<UserInfo> userInfoList) {
        activeUsers.clear();
        for (int i = 0; i < userInfoList.size(); i++) {
            if (!userInfoList.get(i).getUserDeleted()) {
                if (!userInfoList.get(i).getUserProfile().getDisplayName().equals(""))
                    activeUsers.put(userInfoList.get(i).getUserId(),
                            userInfoList.get(i).getUserProfile().getDisplayName());
                else
                    activeUsers.put(userInfoList.get(i).getUserId(),
                            userInfoList.get(i).getUserProfile().getRealName());
            }
        }
    }

    public void showChannelDialog() {
        availableChannels = new String[numberOfChannels];
        i = 0;
        myVeryOwnIterator = channelsWithAccess.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            final String key = myVeryOwnIterator.next();
            availableChannels[i++] = channelsWithAccess.get(key).getChannelName();
        }
        if (availableChannels.length > 0) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.select_channel);
            builder.setItems(availableChannels, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    selectedChannel = availableChannels[item];
                    sendMessage(channelsWithAccess.get(availableChannels[item]).getChannelId(),
                            messageEditText.getText().toString().trim());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(this, R.string.no_channel, Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMessage(String channelId, String message) {
        progressBar.setVisibility(View.VISIBLE);
        if (mWebSocket != null) {
            messageEditText.setText("");
            mWebSocket.send("{\"id\": 100 ," +
                    "\"type\": \"message\"," +
                    "\"channel\": \"" + channelId + "\"," +
                    "\"text\": \"" + message + "\"}");
        }
    }

    public void connectSocket() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RtmConnectResponse> call = apiService.getWebSocketURL("Bearer " + MyConfig.BOT_TOKEN);
        call.enqueue(new Callback<RtmConnectResponse>() {
            @Override
            public void onResponse(Call<RtmConnectResponse> call, Response<RtmConnectResponse> response) {
                if (response.isSuccessful()) {
                    rtmConnectResponse = response.body();
                    if (rtmConnectResponse != null && rtmConnectResponse.getWebsocketUrl() != null)
                        startWebSocket(rtmConnectResponse.getWebsocketUrl());
                }
            }

            @Override
            public void onFailure(Call<RtmConnectResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
