/*
* This activity displays the username, full name, interests, update frequency
* Also, it has buttons to navigate to the configure, find friends and get news activities
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_CHATBOT;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_CHATBOT_GET_BOT_ANSWER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
//import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class Chatbot extends AppCompatActivity implements BackgroundTaskCallback {
    private static final String TAG = Chatbot.class.getSimpleName();
    // Inputs
    private RecyclerView recyclerView;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private EditText inputMessage;

    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private OkHttpClient httpClient;
    private HttpUrl.Builder httpBuilder;
    private Request.Builder httpRequestBuilder;
    private static final AtomicBoolean recordingInProgress = new AtomicBoolean(false);
    private AudioRecord recorder;
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, AUDIO_FORMAT) * 10;

    private Thread recordingThread;

    private final String WIT_AI_VERSION = "20210927";
    private final String CLIENT_ACCESS_TOKEN = "ESXIGUXDCVBG23J3OLQ5PITRL5S4AS7A";
//    Wit wit = (Wit)new Wit.EndpointsBuilder().entity_id("3677483848").entities().build();
//    Log.i(TAG, wit.url);

    private Bundle bundle;
    private String loginUserName;
    private volatile List<JSONObject> userConfig = new ArrayList<>();

    // Thread Related data
    private static final int THREAD_SLEEP_TIME_IN_MS = 1000;
//    Thread updateTrackerThread = new Thread(Chatbot.this);
    private volatile boolean running = true;
    private volatile Boolean isDbRespReceived = false;
    private boolean listening = false;
    private boolean initialRequest;
    private SpeechToText speechService;
    private MicrophoneInputStream capture;
    private SpeakerLabelsDiarization.RecoTokens recoTokens;
    private MicrophoneHelper microphoneHelper;
    com.ibm.watson.developer_cloud.assistant.v1.model.Context context = null;
    StreamPlayer streamPlayer;

    private volatile String botResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot);
        Log.i(TAG, "buffer size = " + BUFFER_SIZE);
        if (!checkPermissionsFromDevice()) requestPermissions();

        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);
        btnRecord= findViewById(R.id.btn_record);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        inputMessage.setTypeface(typeface);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);
        microphoneHelper = new MicrophoneHelper(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                recordMessage();
            }
        });

        //  UPDATE speakButton OnClickListener to invoke startRecording and stopRecording
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("speakButton", "clicked");
                if (!recordingInProgress.get()) {
                    startRecording();
//                    speakButton.setText("Listening ...");
                    Log.i(TAG, "Listening ...");
                } else {
                    stopRecording();
                    Log.i(TAG, "Speak ...");
//                    speakButton.setText("Speak");
                }
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO
        }, 1000);
    }

    private boolean checkPermissionsFromDevice() {
        int recordAudioResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int internetResult = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        return recordAudioResult == PackageManager.PERMISSION_GRANTED
                && internetResult == PackageManager.PERMISSION_GRANTED;
    }

    /*
     * ADD function to instantiate a new instance of AudioRecord and
     * start the Runnable to record and stream to the Wit Speech API
     */
    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, AUDIO_FORMAT, BUFFER_SIZE);
        recorder.startRecording();
        recordingInProgress.set(true);
        recordingThread = new Thread(new StreamRecordingRunnable(), "Stream Recording Thread");
        recordingThread.start();
    }

    /*
     * ADD function to stop the recording and release the memory for
     * AudioRecord, Runnable thread, etc
     */
    private void stopRecording() {
        if (recorder == null) return;
        recordingInProgress.set(false);
        recorder.stop();
        recorder.release();
        recorder = null;
        recordingThread = null;
    }

    /*
     * ADD a function to respond to the user based on the response
     * returned from the Wit Speech API.
     */
    private void respondToUser(String response) {
        Log.v("respondToUser", response);
        String intentName = null;
        String speakerName = null;
        String responseText = "";
        try {
            JSONObject data = new JSONObject(response);
            // Update the TextView with the voice transcription
            // Run it on the MainActivity's UI thread since it's the owner
            final String utterance = data.getString("text");
            Chatbot.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    speechTranscription.setText(utterance);
                    Log.i(TAG, "utterance = " + utterance);
                }
            });
            // Get most confident intent
            JSONObject intent = getMostConfident(data.getJSONArray("intents"));
            if (intent == null) {
//                textToSpeech.speak("Sorry, I didn't get that. What is your name?", TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
                return;
            }
            intentName = intent.getString("name");
            Log.v("respondToUser", intentName);
            // Parse and get the most confident entity value for the name
            JSONObject nameEntity = getMostConfident((data.getJSONObject("entities")).getJSONArray("wit$contact:contact"));
            speakerName = (String) nameEntity.get("value");
            Log.v("respondToUser", speakerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Handle intents
        if (intentName.equals("Greeting_Intent")) {
            responseText = speakerName != null ? "Nice to meet you " + speakerName : "Nice to meet you";
//            textToSpeech.speak(responseText, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
        } else {
//            textToSpeech.speak("What did you say is your name?", TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString());
        }
    }
    /*
     * ADD helper function to select the most confident intents and entities
     * from the response to be used.
     */
    private JSONObject getMostConfident(JSONArray list) {
        JSONObject confidentObject = null;
        double maxConfidence = 0.0;
        for (int i = 0; i < list.length(); i++) {
            try {
                JSONObject object = list.getJSONObject(i);
                double currConfidence = object.getDouble("confidence");
                if (currConfidence > maxConfidence) {
                    maxConfidence = currConfidence;
                    confidentObject = object;
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return confidentObject;
    }

    /*
     * ADD the following function to initialize OkHttp for streaming to the Speech API
     */
    private void initializeHttpClientVoice() {
        httpClient = new OkHttpClient();
        httpBuilder = HttpUrl.parse("https://api.wit.ai/message").newBuilder();
        httpBuilder.addQueryParameter("v", WIT_AI_VERSION);
        httpRequestBuilder = new Request.Builder()
                .url(httpBuilder.build())
                .header("Authorization", "Bearer " + CLIENT_ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .header("Transfer-Encoding", "chunked");

//        .header("Content-Type", "audio/raw")
    }

    private String witAiConn(String message) {
        String result = "";
        try {
            URL url = new URL("https://api.wit.ai/message?v=20170218&q=" + message);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + CLIENT_ACCESS_TOKEN);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                result += output;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * ADD a Runnable to record and stream the voice data to Wit
     */
    private class StreamRecordingRunnable implements Runnable {
        @Override
        public void run() {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            RequestBody requestBody = new RequestBody() {
                @Override
                public MediaType contentType() {
                    return MediaType.parse("audio/raw;encoding=signed-integer;bits=16;rate=8000;endian=little");
                }
                @Override
                public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                    while (recordingInProgress.get()) {
                        int result = recorder.read(buffer, BUFFER_SIZE);
                        if (result < 0) {
                            throw new RuntimeException("Reading of audio buffer failed: " +
                                    getBufferReadFailureReason(result));
                        }
                        bufferedSink.write(buffer);
                        buffer.clear();
                    }
                }
            };
            Request request = httpRequestBuilder.post(requestBody).build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    respondToUser(responseData);
                    Log.v("Streaming Response", responseData);
                }
            } catch (IOException e) {
                Log.e("Streaming Response", e.getMessage());
            }
        }
        private String getBufferReadFailureReason(int errorCode) {
            switch (errorCode) {
                case AudioRecord.ERROR_INVALID_OPERATION:
                    return "ERROR_INVALID_OPERATION";
                case AudioRecord.ERROR_BAD_VALUE:
                    return "ERROR_BAD_VALUE";
                case AudioRecord.ERROR_DEAD_OBJECT:
                    return "ERROR_DEAD_OBJECT";
                case AudioRecord.ERROR:
                    return "ERROR";
                default:
                    return "Unknown (" + errorCode + ")";
            }
        }
    }

    // Initialize the Android TextToSpeech
    // https://developer.android.com/reference/android/speech/tts/TextToSpeech
//    private void initializeTextToSpeech(Context applicationContext) {
////        textToSpeech = new TextToSpeech(applicationContext, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int ttsStatus) {
//                // Disable the speakButton and provide the status of app while waiting for TextToSpeech to initialize
////                speechTranscription.setHint("Loading app ...");
//                Log.i(TAG, "Loading app ...");
//                btnRecord.setEnabled(false);
//
//                if (ttsStatus == TextToSpeech.SUCCESS) {
////                    textToSpeech.speak("Hi! Welcome to the Wit a.i. voice demo. My name is Wit. What is your name?",
//                            TextToSpeech.QUEUE_FLUSH, null,
//                            UUID.randomUUID().toString());
////                    speechTranscription.setHint("Press Speak and say something!");
//                    Log.i(TAG, "Press Speak and say something!");
//                    btnRecord.setEnabled(true);
//                } else {
//                    Log.e(TAG, "TextToSpeech initialization failed");
//                }
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        launchHomeActivity();
        return true;
    }

    private void launchHomeActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(Chatbot.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void getBotResponses(String keywords) {
        final DatabaseServer db_server = new DatabaseServer(Chatbot.this);
        db_server.execute(CMD_CHATBOT, CMD_CHATBOT_GET_BOT_ANSWER, loginUserName, keywords);
    }

    private void handleDatabaseResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            String botResp = "";
            for (JSONObject c : respData) {
                try {
                    JSONArray resp = c.getJSONArray("response");
                    Log.d(TAG, "DB response: " + resp.toString());
                    for(int i = 0; i<resp.length(); i++) {
                        botResp += resp.get(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            botResponse = botResp.replace("\"", "");;
        }
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_ERROR)) {
            Log.e(TAG, "Failed to fetch database response: " + respStatus.get(RESPONSE_MESSAGE));
            Toast.makeText(getApplicationContext(), "Invalid database response", Toast.LENGTH_LONG).show();
            isDbRespReceived = true;
            return;
        }

        if (CMD_CHATBOT.equals(Objects.requireNonNull(respStatus.get(REQUEST_CMD)))) {
            if (CMD_CHATBOT_GET_BOT_ANSWER.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                handleDatabaseResponse(respData);
            }
        }

        // Set the flag after processing the response
        isDbRespReceived = true;
    }

    // Sending a message to Watson Conversation Service
    private void sendMessage(String inputmessage) {
//        final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    if (inputmessage != null && !inputmessage.isEmpty()) {
                      String result = witAiConn(inputmessage);
                      if (result != null && !result.isEmpty()) {
                          try {
                              JSONObject jsonObject = new JSONObject(result);
                              JSONObject entities = jsonObject.getJSONObject("entities");
                              JSONArray issues = entities.getJSONArray("issues");
                              String msg = issues.toString();
                              getBotResponses(msg);
                          }catch (JSONException err){
                              Log.d("Error", err.toString());
                          }
                      }
                    }
                    isDbRespReceived=false;
                    while(!isDbRespReceived){
                      try {
                          // Sleep for until we get response
                          Thread.sleep(THREAD_SLEEP_TIME_IN_MS);

                      } catch (InterruptedException e) {
                          Log.d(TAG, "Thread interrupted, so exiting....");
                          break;
                      }
                    }

                    Message outMessage=new Message();
                    if(botResponse!=null)
                    {
                        outMessage.setMessage(botResponse);
                        outMessage.setId("2");
                        messageArrayList.add(outMessage);
                        botResponse = null;
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(
                                        recyclerView, null, mAdapter.getItemCount()-1);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void sendMessage() {
        final String inputmessage = this.inputMessage.getText().toString().trim();
        sendMessage(inputmessage);
    }

    //Record a message via Watson Speech to Text
    private void recordMessage() {
//        speechService = new SpeechToText();
//        //Use "apikey" as username and apikey as your password
//        speechService.setUsernameAndPassword("apikey", "<SPEECH_TO_TEXT_API_VALUE>");
//        //Default: https://stream.watsonplatform.net/text-to-speech/api
//        speechService.setEndPoint("<SPEECH_TO_TEXT_URL>");

        if(listening != true) {
            capture = microphoneHelper.getInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
//                    try {
//                        speechService.recognizeUsingWebSocket(getRecognizeOptions(capture), new MicrophoneRecognizeDelegate());
//                    } catch (Exception e) {
//                        showError(e);
//                    }
                }
            }).start();
            listening = true;
            Toast.makeText(Chatbot.this,"Listening....Click to Stop", Toast.LENGTH_LONG).show();

        } else {
            try {
                microphoneHelper.closeInputStream();
                listening = false;
                Toast.makeText(Chatbot.this,"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions(InputStream audio) {
        return new RecognizeOptions.Builder()
                .audio(audio)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                //TODO: Uncomment this to enable Speaker Diarization
                //.speakerLabels(true)
                .build();
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            //TODO: Uncomment this to enable Speaker Diarization
            /*SpeakerLabelsDiarization.RecoTokens recoTokens = new SpeakerLabelsDiarization.RecoTokens();
            if(speechResults.getSpeakerLabels() !=null)
            {
                recoTokens.add(speechResults);
                Log.i("SPEECHRESULTS",speechResults.getSpeakerLabels().get(0).toString());
            }*/
            if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                sendMessage(text);
            }
        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override public void onDisconnected() {
            enableMicButton();
        }

        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {

        }

        @Override
        public void onListening() {

        }

        @Override
        public void onTranscriptionComplete() {

        }
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(Chatbot.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

}