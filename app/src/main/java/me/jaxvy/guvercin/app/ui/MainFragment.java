package me.jaxvy.guvercin.app.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.jaxvy.guvercin.Guvercin;
import me.jaxvy.guvercin.GuvercinManager;
import me.jaxvy.guvercin.GuvercinUnbinder;
import me.jaxvy.guvercin.app.R;
import me.jaxvy.guvercin.app.ui.BaseActivity;

public class MainFragment extends Fragment {
    public static final String TAG = "MainFragment";

    private GuvercinUnbinder unbinder;
    private TextView outputTextView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = GuvercinManager.init(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.fragment_main, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button button1 = (Button) view.findViewById(R.id.MainFragment_button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Message is received within this fragment and also in MainActivity since the @Guvercin
                 * annotation is used to register BUTTON_2 event in both places
                 */
                Intent broadcastIntent = new Intent(MainActivity.BUTTON_2_TAG);
                broadcastIntent.putExtra("data", "Hello from Fragment");
                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(broadcastIntent);

            }
        });

        outputTextView = (TextView) view.findViewById(R.id.MainFragment_output);
    }

    @Guvercin(MainActivity.BUTTON_2_TAG)
    public void onButton2EventReceived(Intent intent) {
        outputTextView.setText("Event with tag: " + MainActivity.BUTTON_2_TAG + " and data: " + intent.getStringExtra("data") + " is received");
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        outputTextView = null;
        super.onDestroy();
    }
}
