package my.project.lhesa;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ChatSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Chatbot.class);
        startActivity(intent);
    }
}
