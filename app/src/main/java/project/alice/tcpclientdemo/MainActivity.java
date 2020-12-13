package project.alice.tcpclientdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    TCPClient client=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client=new TCPClient((TCPClient.SocketCallBack) info -> {
            Log.e("callback",info);
        },"192.168.0.102",8888);
        client.start();
        findViewById(R.id.button).setOnClickListener(v->{
            new Thread(()->client.Send("123")).start();

        });
    }

    @Override
    protected void onDestroy() {
        client.disconnect();
        super.onDestroy();
    }
}