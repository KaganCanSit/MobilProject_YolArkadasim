package com.huso.yolarkadasim;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfilSayfasi extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView adsoyadtextview,yastextview,cinsiyettextview,sehirtextview,emailtextview,telefontextview,digertextview;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<String> permisionLauncher;
    private Bitmap secilenfoto;
    private ImageView profilimage;
    private Uri imagedata;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv_sayfasi);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        profilimage=findViewById(R.id.profil_image);

        adsoyadtextview=findViewById(R.id.adsoyad_cvsayfasi_textview);
        yastextview=findViewById(R.id.yas_cvsayfasi_textview);
        cinsiyettextview=findViewById(R.id.cinsiyet_cvsayfasi_textview);
        sehirtextview=findViewById(R.id.sehir_cvsayfasi_textview);
        emailtextview=findViewById(R.id.email_cvsayfasi_textview);
        telefontextview=findViewById(R.id.telefon_cvsayfasi_textview);
        digertextview=findViewById(R.id.diger_cvsayfasi_textview);


        cvlerim();
        registerLauncher();
        resmicvdeayarla();
    }

    public void cvlerim(){//kullanicinin id sine gore veritabanindaki profil bilgilerini cekerek textviewlara ayarlar
        String sayfadakikullanici=firebaseUser.getUid();
        firebaseFirestore.collection("Profiller").document(sayfadakikullanici).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
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
                        adsoyadtextview.setText("İsim Girilmedi !");
                    }if (yas==null || yas.matches("")) {
                        yastextview.setText("Yas Girilmedi !");
                    }if (cinsiyet==null || cinsiyet.matches("")) {
                        cinsiyettextview.setText("Cinsiyet Girilmedi !");
                    }if (sehir==null || sehir.matches("")) {
                        sehirtextview.setText("Sehir Girilmedi !");
                    }if (email==null || email.matches("")) {
                        emailtextview.setText("E Mail Girilmedi !");
                    }if (telefon==null || telefon.matches("")) {
                        telefontextview.setText("Telefon Girilmedi !");
                    }if (diger==null || diger.matches("")) {
                        digertextview.setText("Diger Girilmedi !");
                    }
                }
            }
        });
    }
    public void profilduzenlebutonu(View view){
        Intent intent=new Intent(ProfilSayfasi.this, ProfilGuncelleSayfasi.class);
        startActivity(intent);
    }
    public void fotorafimageview(View view){//fotorafa tiklaninca izin verildiyse galeriye yonlendirir izin verilmediyse izin fonksiyonu cagrilir
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galerinize gidip fotoraf secmek icin izin veriyormusunuz?",Snackbar.LENGTH_INDEFINITE).setAction("İzin veriyorum", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else{
                permisionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else {
            Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intenttogallery);
        }
    }

    private void registerLauncher(){
        //secilen fotorafin imageview a ayarlanmasi saglanir
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if (intentFromResult !=null){
                        imagedata=intentFromResult.getData();

                        try {
                            if (Build.VERSION.SDK_INT >=28) {//versiyonlarin cesitligine gore islem gerceklestirir
                                ImageDecoder.Source source = ImageDecoder.createSource(ProfilSayfasi.this.getContentResolver(), imagedata);
                                secilenfoto = ImageDecoder.decodeBitmap(source);
                                profilimage.setImageBitmap(secilenfoto);
                                resmistrogeaktarma();

                            }else {
                                secilenfoto= MediaStore.Images.Media.getBitmap(ProfilSayfasi.this.getContentResolver(),imagedata);
                                profilimage.setImageBitmap(secilenfoto);
                                resmistrogeaktarma();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //izin islemlerinin gerceklestirmesi yapilir
        permisionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intenttogallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intenttogallery);
                }else {
                    Toast.makeText(ProfilSayfasi.this,"İzine İhtiyac Var!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void resmistrogeaktarma(){//Girilen fotorafi veritabanindaki storage aktarilir
        String sayfadakikisi=firebaseUser.getUid();
        if(imagedata!=null) {
            storageReference.child("images/" + sayfadakikisi + ".jpg").putFile(imagedata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getLocalizedMessage());
                    Toast.makeText(ProfilSayfasi.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void resmicvdeayarla(){//Veritanindaki fotorafi cekip imageview a aktarilmasi saglanir
        String sayfadakikisi=firebaseUser.getUid();
        StorageReference newreferences=storageReference.child("images/"+sayfadakikisi+".jpg");
        newreferences.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadurl=uri.toString();
                Picasso.get().load(downloadurl).into(profilimage);//3 parti paketler
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