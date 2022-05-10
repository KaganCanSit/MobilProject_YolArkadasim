package com.huso.yolarkadasim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class KullaniciGirisSayfasi extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    private TextInputEditText kullanciadigiris,sifregiris;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_giris_sayfasi);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        kullanciadigiris=findViewById(R.id.kullanciadi_giris_edittext);
        sifregiris=findViewById(R.id.sifre_giris_edittext);
    }

    public void girisyapbutonu(View view){
        String kullaniciadi=kullanciadigiris.getText().toString();
        String sifre=sifregiris.getText().toString();
        if(kullaniciadi.matches("") || sifre.matches("")){//kullanici adi ve sifre boşmu kontrolü
            Toast.makeText(KullaniciGirisSayfasi.this,"Bos Alan Mevcut",Toast.LENGTH_LONG).show();
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(kullaniciadi,sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {//kullanici adi ve sifre ile firebase giris islemini gerceklestirir
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(KullaniciGirisSayfasi.this,"Giris Basarili...",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(KullaniciGirisSayfasi.this,Anasayfa.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(KullaniciGirisSayfasi.this,"Kullanıcı hesabınız veya şifrenizde hatalı yer mevcut",Toast.LENGTH_LONG).show();
                }
            });

        }


    }
    public void kayitolbutonu(View view){
        Intent intent=new Intent(KullaniciGirisSayfasi.this,KayitOlSayfasi.class);
        startActivity(intent);

    }
    public void sifremiunuttumbutonu(View view){
        LayoutInflater factory=LayoutInflater.from(KullaniciGirisSayfasi.this);
        final View girisler=factory.inflate(R.layout.dialog_sifremi_unuttum,null);
        final TextInputEditText sifremiunuttumedittext=(TextInputEditText) girisler.findViewById(R.id.sifremiunuttum_edittext);
        final AlertDialog.Builder alert=new AlertDialog.Builder(KullaniciGirisSayfasi.this);
        alert.setTitle("Şifremi Unuttum").setView(girisler).setPositiveButton("Gönder", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sifrereset=sifremiunuttumedittext.getText().toString();
                if(sifrereset.matches("")||sifrereset==null){
                    Toast.makeText(KullaniciGirisSayfasi.this,"Bos alan mevcut!",Toast.LENGTH_LONG).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(sifrereset).addOnSuccessListener(new OnSuccessListener<Void>() {// Email adresine sifre yenileme icin link gonderiyor
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(KullaniciGirisSayfasi.this,"Email Adresinize Link Gönderilmiştir",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(KullaniciGirisSayfasi.this,"Hata! Email Adresinize Link Gönderilmedi!"+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        }).setNegativeButton("Gönderme", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alert.show();
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