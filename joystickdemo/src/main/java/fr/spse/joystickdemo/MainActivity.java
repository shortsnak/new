package fr.spse.joystickdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {
    @SuppressLint({"DefaultLocale","SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoystickView joystick = findViewById(R.id.joystick);

        CheckBox checkBoxH = findViewById(R.id.horizontal);
        CheckBox checkBoxV = findViewById(R.id.vertical);

        checkBoxH.setOnCheckedChangeListener((compoundButton, b) -> {
            checkBoxV.setChecked(false);
            joystick.setButtonDirection(b ? JoystickView.BUTTON_DIRECTION_HORIZONTAL : 0);
        });

        checkBoxV.setOnCheckedChangeListener((compoundButton, b) -> {
            checkBoxH.setChecked(false);
            joystick.setButtonDirection(b ? JoystickView.BUTTON_DIRECTION_VERTICAL : 0);
        });

        joystick.setButtonDirection(JoystickView.BUTTON_DIRECTION_BOTH);


        Switch Switch = findViewById(R.id.switch_use_Rectangle);
        Switch.setOnCheckedChangeListener((buttonView, isChecked) -> joystick.setRectangle(isChecked) );
        joystick.setRectangle (Switch.isChecked());

        Switch fixedCenterSwitch = findViewById(R.id.switch_fixed_center);
        fixedCenterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> joystick.setFixedCenter(isChecked));
        joystick.setFixedCenter(fixedCenterSwitch.isChecked());

        Switch stickyBorderSwitch = findViewById(R.id.switch_sticky_border);
        stickyBorderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> joystick.setButtonStickToBorder(isChecked));
        joystick.setButtonStickToBorder(stickyBorderSwitch.isChecked());

        Switch autoCenterSwitch = findViewById(R.id.switch_auto_center);
        autoCenterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> joystick.setAutoReCenterButton(isChecked));
        joystick.setAutoReCenterButton(autoCenterSwitch.isChecked());

        TextView normalized = findViewById(R.id.normalized);
        TextView del = findViewById(R.id.del);
        TextView mStrength = findViewById(R.id.strength);
        
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            
            @Override
            public void onMove(int angle, int strength, MotionEvent event) {
                mStrength.setText(strength + "%");
                
                normalized.setText(
                        String.format("norm "+"x%03d:y%03d",
                                joystick.getNormalizedX(),
                                joystick.getNormalizedY())
                );
                del.setText(
                        String.format("del "+"x%03d:y%03d",
                                joystick.getDelX(),
                                joystick.getDelY())
                );
            }
        });

    }
}