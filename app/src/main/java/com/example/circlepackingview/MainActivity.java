package com.example.circlepackingview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private CirclePackingView circle;
    private int num = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circle = (CirclePackingView) findViewById(android.R.id.primary);
        num = 5;
        circle.setCircleNumber(num);
    }

    public void animateAngle(View view) {
        ValueAnimator anim = ObjectAnimator.ofFloat(circle, "rotation", 0f, 360f);
        anim.setDuration(5000);
        anim.setRepeatCount(2);
        anim.start();
    }

    public void animateFill(View view) {
        ValueAnimator numberAnim = ObjectAnimator.ofFloat(circle, "fill", 1.0f, 0.9f);
        numberAnim.setDuration(500);
        numberAnim.setRepeatCount(1);
        numberAnim.setRepeatMode(ValueAnimator.REVERSE);
        numberAnim.start();
    }

    public void animateNumber(View view) {
        ValueAnimator numberAnim = ObjectAnimator.ofInt(circle, "circleNumber", 8, 0);
        numberAnim.setDuration(5000);
        numberAnim.setRepeatCount(1);
        numberAnim.setRepeatMode(ValueAnimator.REVERSE);
        numberAnim.start();
    }

    public void addNumber(View view) {
        circle.setCircleNumber(++num);
    }

    public void removeNumber(View view) {
        circle.setCircleNumber(--num);
    }
}
