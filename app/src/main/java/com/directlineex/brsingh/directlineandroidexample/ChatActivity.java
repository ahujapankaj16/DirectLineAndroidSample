package com.directlineex.brsingh.directlineandroidexample;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.id;
import static com.directlineex.brsingh.directlineandroidexample.R.layout.activity_chat;

/**
 * A Chat Screen Activity
 */
public class ChatActivity extends AppCompatActivity {
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String localToken = "";
    private String conversationId = "";
    private String primaryToken = "";
    private String botName = "";

    //keep the last Response MsgId, to check if the last response is already printed or not
    private String lastResponseMsgId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_chat);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Chat Bot");// Hard Coded
        chatHistory = new ArrayList<ChatMessage>();

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                displayMessage(chatMessage);
                messageET.setText("");
                 RetrieveFeedTask task = new RetrieveFeedTask();
                 task.execute(messageText);

            }
        });


    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }















    public String GetText(String s) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;
        String answer ="";
        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://setu.azurewebsites.net/qnamaker/knowledgebases/468226e0-a9e3-4f07-8126-fd47ebbae53a/generateAnswer");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "EndpointKey 1062598d-4c1e-4fd7-8a69-e6d079ade0e0");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("question", s);
            Log.d("karma question",s);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
           // Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            Log.d("karma ", "response is " + text);
            if(text != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    JSONArray ansArray = jsonObject.getJSONArray("answers");
                    for(int i=0;i<ansArray.length();i++)
                        answer = ansArray.getJSONObject(i).getString("answer");
                    return answer;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            return answer;
        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

    return answer;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... Strings) {
            try {
                Log.d("karma", "called");
                String answer =GetText(Strings[0]);
                Log.d("karma", "after called");
                return answer;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ChatMessage message = new ChatMessage();
            //message.setId(2);
            message.setMe(false);
            message.setDate(DateFormat.getDateTimeInstance().format(new Date()));
            message.setMessage(s);
            displayMessage(message);
        }
    }


}


