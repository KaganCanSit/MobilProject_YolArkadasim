package com.huso.yolarkadasim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Anasayfa extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<String> advesoyadarray;
    private TextView isteklistesindekikisisayisitextview;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        isteklistesindekikisisayisitextview=findViewById(R.id.isteklistesindekikisisayisi_textview);

        advesoyadarray=new ArrayList<>();

        isteklistesindekikisisayisikontrolu();
    }

    //Asagidaki butonlarda hangi sayfalara yonlendirilecegini gosterir
    public void cvduzenlebutonu(View view){
        Intent intent=new Intent(Anasayfa.this, ProfilSayfasi.class);
        startActivity(intent);

    }
    public void yolculukilaniverbutonu(View view){
        Intent intent=new Intent(Anasayfa.this, IlanVermeSayfasi.class);
        startActivity(intent);
    }
    public void yolculukilanlaributonu(View view){
        Intent intent=new Intent(Anasayfa.this, IlanlarSayfasi.class);
        startActivity(intent);

    }
    public void isteklistesibutonu(View view){
        Intent intent=new Intent(Anasayfa.this, IlanlarListesiSayfasi.class);
        startActivity(intent);

    }
    public void cikisbutonu(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(Anasayfa.this);
        builder.setTitle("ÇIKIŞ YAP");
        builder.setMessage("Çıkış Yapmak İstermisiniz ?");
        builder.setNegativeButton("HAYIR",null);
        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//cikis islemlerini gerceklestirir
                firebaseAuth.signOut();
                Intent intenttosignup=new Intent(Anasayfa.this,KullaniciGirisSayfasi.class);
                startActivity(intenttosignup);
                finish();
            }
        });
        builder.show();
    }
    public void paylasbutonu(View view){//cesitli platformlarda paylasma islemleri gerceklestirilir
        final String appPackageName = getApplicationContext().getPackageName();
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String sharebody="https://play.google.com/store/apps/details?id=" + appPackageName;
        String sharesub="Your subject here";
        intent.putExtra(Intent.EXTRA_SUBJECT,sharesub);
        intent.putExtra(Intent.EXTRA_TEXT,sharebody);
        startActivity(Intent.createChooser(intent,"PAYLAŞ"));
    }

    public void isteklistesindekikisisayisikontrolu(){//isteklistesindeki kisi sayilarini bulur
        String sayfadakikisi=firebaseUser.getUid();
        firebaseFirestore.collection("isteklistesi").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                advesoyadarray.clear();
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        String yolculukilaniniverenkisi=(String) snapshot.getString("yolculukilaniniverenkisi");
                        System.out.println("---"+yolculukilaniniverenkisi);
                        if (sayfadakikisi.matches(yolculukilaniniverenkisi)&&yolculukilaniniverenkisi!=null){
                            String basvurbutonunabasankisi=(String) snapshot.getString("basvurbutonunabasankisi");
                            advesoyadarray.add(basvurbutonunabasankisi);
                            int isteklistesindekikisisayisi=advesoyadarray.size();
                            String isteklistesisayisi=String.valueOf(isteklistesindekikisisayisi);
                            if (isteklistesindekikisisayisi<1 && isteklistesisayisi==null){
                                isteklistesindekikisisayisitextview.setVisibility(View.INVISIBLE);
                            }else if (isteklistesindekikisisayisi>0){
                                isteklistesindekikisisayisitextview.setText(isteklistesisayisi);
                            }
                        }
                    }
                }
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