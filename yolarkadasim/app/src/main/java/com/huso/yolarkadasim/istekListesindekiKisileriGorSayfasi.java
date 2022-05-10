package com.huso.yolarkadasim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class istekListesindekiKisileriGorSayfasi extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ImageView isteklistesindekikisiprofilresmi;
    private TextView adsoyadtextview,yastextview,cinsiyettextview,sehirtextview,emailtextview,telefontextview,digertextview;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istek_listesindeki_kisileri_gor_sayfasi);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        isteklistesindekikisiprofilresmi=findViewById(R.id.isteklistesindeki_profil_image);

        adsoyadtextview=findViewById(R.id.adsoyad_isteklistesi_textview);
        yastextview=findViewById(R.id.yas_isteklistesi_textview);
        cinsiyettextview=findViewById(R.id.cinsiyet_isteklistesi_textview);
        sehirtextview=findViewById(R.id.sehir_isteklistesi_textview);
        emailtextview=findViewById(R.id.email_isteklistesi_textview);
        telefontextview=findViewById(R.id.telefon_isteklistesi_textview);
        digertextview=findViewById(R.id.diger_isteklistesi_textview);

        Intent intent=getIntent();//isteklistesi adapterdan tiklandiginda gonderilen degerleri buradan aliyoruz
        String isteklistesindekikisiler=intent.getStringExtra("isteklistesindekikisiler");
        isteklistesindekikisiler(isteklistesindekikisiler);
        isteklistesindekikisilerinprofilresimleri(isteklistesindekikisiler);
    }

    //Burada gonderilen kisinin id sine gore veritabanindan profil bilgilerindeki degerleri cekiyoruz
    public void isteklistesindekikisiler(String isteklistesindekikisiler){
        firebaseFirestore.collection("Profiller").document(isteklistesindekikisiler).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String advesoyad = documentSnapshot.getString("advesoyad");
                    String yas = documentSnapshot.getString("yas");
                    String cinsiyet = documentSnapshot.getString("cinsiyet");
                    String sehir = documentSnapshot.getString("sehir");
                    String email = documentSnapshot.getString("email");
                    String telefon = documentSnapshot.getString("telefon");
                    String diger = documentSnapshot.getString("diger");

                    adsoyadtextview.setText(advesoyad);
                    yastextview.setText(yas);
                    cinsiyettextview.setText(cinsiyet);
                    sehirtextview.setText(sehir);
                    emailtextview.setText(email);
                    telefontextview.setText(telefon);
                    digertextview.setText(diger);

                    if (advesoyad==null || advesoyad.matches("")) {
                        adsoyadtextview.setText("İsim Girilmedi!");
                    }if (yas == null || yas.matches("")) {
                        yastextview.setText("Yas Girilmedi !");
                    }if (cinsiyet == null || cinsiyet.matches("")) {
                        cinsiyettextview.setText("Cinsiyet Girilmedi !");
                    }if (sehir == null || sehir.matches("")) {
                        sehirtextview.setText("Sehir Girilmedi !");
                    }if (email == null || email.matches("")) {
                        emailtextview.setText("E Mail Girilmedi !");
                    }if (telefon == null || telefon.matches("")) {
                        telefontextview.setText("Telefon Girilmedi !");
                    }if (diger == null || diger.matches("")) {
                        digertextview.setText("Diger Girilmedi !");
                    }
                }
            }
        });
    }

    //gonderilen id ye gore o kisinin profilini cekerek imageview a ayarlanmasi saglanir
    public void isteklistesindekikisilerinprofilresimleri(String isteklistesindekikisiler){
        StorageReference newreferences=storageReference.child("images/"+isteklistesindekikisiler+".jpg");
        newreferences.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadurl=uri.toString();
                Picasso.get().load(downloadurl).into(isteklistesindekikisiprofilresmi);
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