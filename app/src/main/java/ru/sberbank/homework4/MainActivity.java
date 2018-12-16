package ru.sberbank.homework4;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static int capacityTextViews = 0;
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;

    private TextView linearHorizontalTextView1;
    private TextView linearHorizontalTextView2;
    private TextView linearHorizontalTextView3;
    private TextView realativeTextView1;
    private TextView realativeTextView2;
    private TextView realativeTextView3;
    private TextView linearVerticalTextView1;
    private TextView linearVerticalTextView2;
    private TextView linearVerticalTextView3;
    private TextView constraint_textView1;
    private TextView constraint_textView2;
    private TextView constraint_textView3;

    private Messenger mService;
    private Messenger mMessenger = new Messenger(new IncomingHandler());
    private ValueAnimator clockAnimator;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message message = Message.obtain(null, MyIntentService.MSG_REGISTER_CLIENT);
            message.replyTo = mMessenger;

            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        init();
        initListeners();
    }

    private void initListeners() {
        constraint_textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clockAnimator.isPaused()) {
                    clockAnimator.resume();
                    //  Toast.makeText(getApplicationContext(), "Resumed", Toast.LENGTH_SHORT).show();
                } else if (clockAnimator.isRunning()) {
                    //    Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
                    clockAnimator.pause();
                } else
                    clockAnimator.start();

            }
        });
    }

    private void init() {
        myReceiver = new MyReceiver(new ViewCallbackImpl());
        intentFilter = new IntentFilter("ru.sberbank.SEND_MESSAGES_FILTER");
        clockAnimator = animatePointer(TimeUnit.SECONDS.toMillis(5));
    }


    private void addTextViewToArray() {
        textViews.add(linearHorizontalTextView1);
        textViews.add(linearHorizontalTextView2);
        textViews.add(linearHorizontalTextView3);

        textViews.add(linearVerticalTextView1);
        textViews.add(linearVerticalTextView2);
        textViews.add(linearVerticalTextView3);

        textViews.add(constraint_textView1);
        textViews.add(constraint_textView2);
        textViews.add(constraint_textView3);


        textViews.add(realativeTextView1);
        textViews.add(realativeTextView2);
        textViews.add(realativeTextView3);

        capacityTextViews = textViews.size();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnBindService();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addTextViewToArray();
        doBindService();
        registerReceiver(myReceiver, intentFilter, "ru.sberbank.SEND_MESSAGES_PERMISSION", null);
    }

    private void initView() {
        linearHorizontalTextView1 = findViewById(R.id.linear_horizontal_textView1);
        linearHorizontalTextView2 = findViewById(R.id.linear_horizontal_textView2);
        linearHorizontalTextView3 = findViewById(R.id.linear_horizontal_textView3);

        realativeTextView1 = findViewById(R.id.realative_textView1);
        realativeTextView2 = findViewById(R.id.realative_textView2);
        realativeTextView3 = findViewById(R.id.realative_textView3);

        linearVerticalTextView1 = findViewById(R.id.linear_vertical_textView1);
        linearVerticalTextView2 = findViewById(R.id.linear_vertical_textView2);
        linearVerticalTextView3 = findViewById(R.id.linear_vertical_textView3);

        constraint_textView1 = findViewById(R.id.constraint_textView1);
        constraint_textView2 = findViewById(R.id.constraint_textView2);
        constraint_textView3 = findViewById(R.id.constraint_textView3);
    }

    private void doBindService() {
        startService(MyIntentService.newIntent(MainActivity.this));
        bindService(MyIntentService.newIntent(MainActivity.this), mServiceConnection, BIND_AUTO_CREATE);

    }

    private ValueAnimator animatePointer(long orbitDuration) {
        ValueAnimator anim = ValueAnimator.ofInt(0, 359);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) constraint_textView1.getLayoutParams();
                ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) constraint_textView3.getLayoutParams();
                layoutParams.circleAngle = val;
                layoutParams2.circleAngle = 180 + val;
                constraint_textView1.setLayoutParams(layoutParams);
                constraint_textView3.setLayoutParams(layoutParams2);

            }
        });
        anim.setDuration(orbitDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setRepeatCount(ValueAnimator.INFINITE);

        return anim;
    }

    private void doUnBindService() {
        Message message = Message.obtain(null, MyIntentService.MSG_UNREGISTER_CLIENT);
        message.replyTo = mMessenger;

        try {
            mService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mServiceConnection);
    }

    private void setTextViewFromService(Object numbers) {
        ArrayList arrayList = (ArrayList) numbers;
        for (int i = 0; i < capacityTextViews / 2; i++) {
            TextView tempTextView = textViews.get(i);
            String temp = arrayList.get(i).toString();
            tempTextView.setText(temp);

        }
    }

    private void setTextViewFromBroadcast(Object numbers) {
        ArrayList arrayList = (ArrayList) numbers;
        for (int i = capacityTextViews / 2; i < capacityTextViews; i++) {
            TextView tempTextView = textViews.get(i);
            String temp = arrayList.get(i).toString();
            tempTextView.setText(temp);

        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyIntentService.MSG_VALUE:
                    setTextViewFromService(msg.obj);
                    break;
                case MyIntentService.MSG_STOP_SERVICE:
                    //bla bla
                    break;
            }
        }
    }


    private class ViewCallbackImpl implements ViewCallback {

        @Override
        public void onStatusChanged(ArrayList<String> strings) {
            setTextViewFromBroadcast(strings);
        }
    }
}
