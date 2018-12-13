package ru.sberbank.homework4;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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

    private ArrayList<TextView> textViews = new ArrayList<>(12);
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


    }

    private void addTextViewToArray() {
        textViews.add(linearHorizontalTextView1);
        textViews.add(linearHorizontalTextView2);
        textViews.add(linearHorizontalTextView3);

//        textViews.add(realativeTextView1);
//        textViews.add(realativeTextView2);
//        textViews.add(realativeTextView3);

        textViews.add(linearVerticalTextView1);
        textViews.add(linearVerticalTextView2);
        textViews.add(linearVerticalTextView3);

//        textViews.add(constraint_textView1);
//        textViews.add(constraint_textView2);
//        textViews.add(constraint_textView3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addTextViewToArray();
        doBindService();
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

    private void setTextView(Object numbers) {
        ArrayList arrayList = (ArrayList) numbers;
        for (int i = 0; i < textViews.size(); i++) {
            TextView tempTextView = textViews.get(i);
            String temp = arrayList.get(i).toString();
            Log.d("TEST", "bla " + arrayList.size());
            tempTextView.setText(temp);

        }
    }

    private void setTextView(String s) {
        linearHorizontalTextView1.setText(s);
    }


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyIntentService.MSG_VALUE:
                    setTextView(msg.obj);
                    break;
                case MyIntentService.MSG_STOP_SERVICE:
                    //bla bla
                    break;
            }
        }
    }
}
