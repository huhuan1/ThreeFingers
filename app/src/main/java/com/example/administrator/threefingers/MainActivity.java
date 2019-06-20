package com.example.administrator.threefingers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private View view;
    private LinearLayout linearLayout;
    private float downY, dy, lastY;
    private int scaledDoubleTapSlop=10;//最小的滑动距离（小于这个值，认为不滑动，防止误触）
    private LinearLayout.LayoutParams params;
    private int slideHeight;//view的当前高度
    private static final int FINGER_COUNT = 3;//多点触控，触发的个数
    private static final int SLIDE_SPEED = 25;//view滑动时候，每次滑动的距离
    private static final int MAX_SLIDE_HEIGHT = 800;//view的最大高度
    private static final int HANDLER_REMOVE_VIEW = 1000;//移除view的handler的消息

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_REMOVE_VIEW:
                    linearLayout.removeAllViews();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linearLayout);
//        scaledDoubleTapSlop = ViewConfiguration.get(this).getScaledDoubleTapSlop();
        view = LayoutInflater.from(this).inflate(R.layout.layout_slide, null);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() < FINGER_COUNT) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                onDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onUp();
                break;

        }
        return super.onTouchEvent(event);
    }

    public void onDown(MotionEvent event) {
        downY = event.getY();
        handler.removeMessages(HANDLER_REMOVE_VIEW);
        if (view.getParent() == null) {
            params.height = 0;
            view.setLayoutParams(params);
            linearLayout.addView(view);
        }
    }

    public void onMove(MotionEvent event) {
        dy = event.getY() - downY;
        if (dy > scaledDoubleTapSlop) {
            if (lastY - dy > 0) {
                //手指下滑之后在上滑
                params.height = slideHeight - SLIDE_SPEED < 0 ? 0 : params.height - SLIDE_SPEED;
            } else {
                //手指下滑
                params.height = slideHeight + SLIDE_SPEED < MAX_SLIDE_HEIGHT ? slideHeight + SLIDE_SPEED : MAX_SLIDE_HEIGHT;
            }
        } else if (-dy > scaledDoubleTapSlop) {
            //手指上滑
            params.height = slideHeight - SLIDE_SPEED < 0 ? 0 : slideHeight - SLIDE_SPEED;
        }
        view.setLayoutParams(params);
        slideHeight = params.height;
        lastY = dy;
    }

    public void onUp() {
        if (slideHeight > MAX_SLIDE_HEIGHT / 2) {
            params.height = MAX_SLIDE_HEIGHT;
            view.setLayoutParams(params);
            handler.sendEmptyMessageDelayed(HANDLER_REMOVE_VIEW, 5000);
        } else {
            linearLayout.removeAllViews();
        }

    }

}
