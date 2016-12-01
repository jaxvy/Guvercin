package me.jaxvy.guvercin.app.ui;

import android.os.Bundle;

import me.jaxvy.guvercin.app.R;

/**
 * SecondaryActivity is extending the BaseActivity, which uses Guvercin annotations, but is not using
 * the @Guvercin annotation. Therefore a corresponding SecondaryActivity_Guvercin class is not created.
 * However, SecondaryActivity still works without any side effects from using the Guvercin library.
 */
public class SecondaryActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
    }
}
