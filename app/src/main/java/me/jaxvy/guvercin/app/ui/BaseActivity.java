package me.jaxvy.guvercin.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.jaxvy.guvercin.Guvercin;
import me.jaxvy.guvercin.GuvercinManager;
import me.jaxvy.guvercin.GuvercinUnbinder;

import static me.jaxvy.guvercin.app.ui.MainActivity.BUTTON_3_TAG;

public abstract class BaseActivity extends AppCompatActivity {

    private GuvercinUnbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = GuvercinManager.init(this);
    }

    @Guvercin(BUTTON_3_TAG)
    public void onButton3EventReceived() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Event with tag: " + BUTTON_3_TAG + " received on BaseActivity");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
