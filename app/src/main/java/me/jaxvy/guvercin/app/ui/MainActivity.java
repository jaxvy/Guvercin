package me.jaxvy.guvercin.app.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import me.jaxvy.guvercin.Guvercin;
import me.jaxvy.guvercin.app.R;

/**
 * Sample class showing how to use @Guvercin annotation to receive messages. The initialization of the
 * GuvercinManager happens in onCreate of BaseActivity.
 */
public class MainActivity extends BaseActivity {

    private static final String BUTTON_1_TAG = "button_1_tag";
    public static final String BUTTON_2_TAG = "button_2_tag";
    public static final String BUTTON_3_TAG = "button_3_tag";

    private TextView outputTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.MainActivity_button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Sending a simple broadcast message with no data
                 */
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(new Intent(BUTTON_1_TAG));
            }
        });

        Button button2 = (Button) findViewById(R.id.MainActivity_button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Sending a simple broadcast message with data
                 */
                Intent broadcastIntent = new Intent(BUTTON_2_TAG);
                broadcastIntent.putExtra("data", "Hello from Send Message Button 2");
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(broadcastIntent);
            }
        });

        Button button3 = (Button) findViewById(R.id.MainActivity_button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * The event will be received on BaseActivity
                 */
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .sendBroadcast(new Intent(BUTTON_3_TAG));
            }
        });

        Button button4 = (Button) findViewById(R.id.MainActivity_button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(android.R.id.content, MainFragment.newInstance());
                ft.addToBackStack(MainFragment.TAG);
                ft.commitAllowingStateLoss();
            }
        });

        Button button5 = (Button) findViewById(R.id.MainActivity_button5);
        button5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondaryActivity.class));
            }
        });

        outputTextView = (TextView) findViewById(R.id.MainActivity_output);
    }

    /**
     * Sample usage of @Guvercin. Method names can be anything.
     */
    @Guvercin(BUTTON_1_TAG)
    public void onButton1EventReceived() {
        outputTextView.setText("Event with tag: " + BUTTON_1_TAG + " is received.");
    }

    /**
     * Sample usage of @Guvercin when you need the extra data inside the intent.
     */
    @Guvercin(BUTTON_2_TAG)
    public void onButton2EventReceived(Intent intent) {
        outputTextView.setText("Event with tag: " + BUTTON_2_TAG + " and data: " + intent.getStringExtra("data") + " is received");
    }
}
