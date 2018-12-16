package ru.sberbank.homework4;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


public class MyIntentService extends IntentService {


    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_VALUE = 3;
    public static final int MSG_STOP_SERVICE = 4;
    private static boolean shoudFinish = false;

    private Random random = new Random();

    private ArrayList<Messenger> mClients = new ArrayList<>();

    private Messenger mMessenger = new Messenger(new IncomingHandler());


    public MyIntentService() {
        super("MyIntentService");
    }

    public static final Intent newIntent(Context context) {
        return new Intent(context, MyIntentService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; ; i++) {
                    Intent intent1 = new Intent("ru.sberbank.SEND_MESSAGES_FILTER");
                    Bundle bundle = new Bundle();
                    ArrayList<Integer> integers = getRandomNumbers(MainActivity.capacityTextViews);
                    bundle.putIntegerArrayList("Numbers", integers);
                    intent1.putExtra("Numbers", bundle);
                    sendBroadcast(intent1, "ru.sberbank.SEND_MESSAGES_PERMISSION");
                    // Log.d("DEBIL","ITS WORK");


                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        for (int i = 0; ; i++) {
            if (shoudFinish) break;

            sendToClients(Message.obtain(null, MSG_VALUE, getRandomNumbers(MainActivity.capacityTextViews)));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    private ArrayList<Integer> getRandomNumbers(int count) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            arrayList.add(getRandom(100));
        }
        Log.d("TEST", "bla2 " + arrayList.size());
        return arrayList;
    }


    private int getRandom(int max) {
        return random.nextInt(max);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }


    private void sendToClients(Message message) {
        for (Messenger messenger : mClients) {
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }


    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
            }
        }
    }
}