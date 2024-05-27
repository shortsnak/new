package com.lukkass.rcble;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextViewAngleLeft;
    private TextView mTextViewStrengthLeft;

    private TextView mTextViewAngleRight;
    private TextView mTextViewStrengthRight;
    private TextView mTextViewCoordinateRight;
    
    
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joystick_view_layout);

        mTextViewAngleLeft = findViewById(R.id.textView_angle_left);
        mTextViewStrengthLeft = findViewById(R.id.textView_strength_left);

        JoystickView joystickLeft = findViewById(R.id.joystickView_left);
        joystickLeft.setOnMoveListener((angle, strength, event) -> {

            mTextViewAngleLeft.setText(angle + "°");
            mTextViewStrengthLeft.setText(strength + "%");
        });


        mTextViewAngleRight = findViewById(R.id.textView_angle_right);
        mTextViewStrengthRight = findViewById(R.id.textView_strength_right);
        mTextViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

        final JoystickView joystickRight = findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener((angle, strength, event) -> {
            mTextViewAngleRight.setText(angle + "°");
            mTextViewStrengthRight.setText(strength + "%");
            mTextViewCoordinateRight.setText(
                    String.format("x%03d:y%03d",
                            joystickRight.getNormalizedX(),
                            joystickRight.getNormalizedY())
            );
        });
    }
}
    
