package ru.sberbank.homework4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {

    ViewCallback viewCallback;

    public MyReceiver() {

    }

    public MyReceiver(ViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("Numbers");
        ArrayList arrayList = bundle.getIntegerArrayList("Numbers");

        viewCallback.onStatusChanged(arrayList);
    }
}
