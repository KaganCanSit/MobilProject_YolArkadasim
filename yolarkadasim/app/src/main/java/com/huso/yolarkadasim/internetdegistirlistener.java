package com.huso.yolarkadasim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

public class internetdegistirlistener extends BroadcastReceiver {
    //internet baglanti kontrolu icin dialog xml inin tanimlanip ayarlanmasi saglanir
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!internetcommon.internetkontrol(context)){
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            View internetkontroldialog= LayoutInflater.from(context).inflate(R.layout.internetbaglanti_dialog,null);
            builder.setView(internetkontroldialog);

            AppCompatButton yenilebuton=internetkontroldialog.findViewById(R.id.internetkontrolyenile_button);

            AlertDialog dialog=builder.create();
            dialog.show();
            dialog.setCancelable(false);

            dialog.getWindow().setGravity(Gravity.CENTER);

            yenilebuton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });
        }
    }
}
