package com.huso.yolarkadasim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class KayitOlSayfasi extends AppCompatActivity {
    private TextInputEditText kullaniciadikayitol,sifrekayitol,yenisifrekayitol;
    private FirebaseAuth firebaseAuth;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_sayfasi);

        firebaseAuth=FirebaseAuth.getInstance();

        kullaniciadikayitol=findViewById(R.id.kullaniciadi_kayitol_edittext);
        sifrekayitol=findViewById(R.id.sifre_kayitol_edittext);
        yenisifrekayitol=findViewById(R.id.yenisifre_kayitol_edittext);
    }

    public void kaydetbutonu(View view){
        String kullaniciadi=kullaniciadikayitol.getText().toString();
        String sifre=sifrekayitol.getText().toString();
        String yenisifre=yenisifrekayitol.getText().toString();


        if (kullaniciadi.matches("") || sifre.matches("") || yenisifre.matches("")) {//parametlerin bos olması kontolu gerceklestiriliyor
            Toast.makeText(KayitOlSayfasi.this,"Bos Alan Mevcut....",Toast.LENGTH_LONG).show();
        }else{
            if (!sifre.matches(yenisifre)){
                Toast.makeText(KayitOlSayfasi.this,"Sifreniz Uyusmamaktadir!",Toast.LENGTH_LONG).show();
            }else {
                firebaseAuth.fetchSignInMethodsForEmail(kullaniciadi).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {//Kullanici adinin veritabaninda olup olmadigini kontrol eder
                        if (task.isSuccessful()){
                            if (task.getResult() !=null && task.getResult().getSignInMethods() !=null){
                                if (task.getResult().getSignInMethods().isEmpty()){// veritabaninda yoksa uye olma fonksiyonu calisir
                                    uyeol(kullaniciadi,sifre);
                                }
                                else {//veritabaninda oldugunu gosterir ve hata mesaji verir
                                    Toast.makeText(KayitOlSayfasi.this,"Kullanici Adi Zaten Var!",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
            }
        }
    }
    public void uyeol(String kullaniciadi,String sifre){ // verilen parametlere gore firebase e kayit olma islemi gerceklestirilir
        firebaseAuth.createUserWithEmailAndPassword(kullaniciadi,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(KayitOlSayfasi.this,"Basarili bir sekilde kaydedildi...",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(KayitOlSayfasi.this,Anasayfa.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(KayitOlSayfasi.this,"Uye olma işlemi gerçekleştirilmedi!",Toast.LENGTH_LONG).show();
            }
        });

    }

    //İnternete olan bağlantımızın kontrolünü sürekli olarak kontrol eder.
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetdegistirlistener,filter);
        super.onStart();
    }
    @Override
    protected void onStop() {
        unregisterReceiver(internetdegistirlistener);
        super.onStop();
    }
}