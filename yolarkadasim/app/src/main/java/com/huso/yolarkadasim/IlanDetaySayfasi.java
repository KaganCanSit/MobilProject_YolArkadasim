package com.huso.yolarkadasim;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.huso.yolarkadasim.databinding.ActivityIlanigoruntulemapBinding;

import java.util.HashMap;

public class IlanDetaySayfasi extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityIlanigoruntulemapBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIlanigoruntulemapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ilanigoruntulemap);
        mapFragment.getMapAsync(this);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent=getIntent();//yolculukilanlari adapterdaki gonderilen degeri burada aliyoruz
        String yolculukilaniid=intent.getStringExtra("yolculukilaniid");
        yolculukilaninigor(yolculukilaniid);
    }

    //Gonderilen id ye gore veritabanindan o bilgileri cekiyoruz
    public void yolculukilaninigor(String yolculukilaniid ){
        firebaseFirestore.collection("yolculukilanlari").document(yolculukilaniid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null){
                    String yolculukadi=documentSnapshot.getString("yolculukadi");
                    String varisisinadi=documentSnapshot.getString("varisadresi");
                    String aciklama=documentSnapshot.getString("aciklama");
                    String yolculukilaniniverenkullanici=documentSnapshot.getString("yolculukilaniniverenkisi");
                    Double latitude=documentSnapshot.getDouble("latitude");
                    Double longitude=documentSnapshot.getDouble("longitude");

                    LatLng sydney = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    binding.isinadiGorTextview.setText(yolculukadi+"-"+varisisinadi);
                    binding.aciklamaGorTextview.setText(aciklama);

                    String sayfadakikisi=firebaseUser.getUid();

                    //yolculuk ilanini veren kullanicinin bos olmamasinin kontrolu saglaniyor
                    if (yolculukilaniniverenkullanici!=null) {
                        if (sayfadakikisi.matches(yolculukilaniniverenkullanici)) {//Sayfadaki kisi eger yolculuuk ilanini veren kisi ise basvur butonunun gosterilip gosterilmemesinin kontrolu saglanir
                            binding.basvurGotButonu.setVisibility(View.INVISIBLE);
                        }
                        else {
                            binding.basvurGotButonu.setVisibility(View.VISIBLE);

                            binding.basvurGotButonu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(IlanDetaySayfasi.this);
                                    builder.setTitle("Basvuru Yap");
                                    builder.setMessage("Basvuru Yapmak İstermisiniz ?");
                                    builder.setNegativeButton("HAYIR",null);
                                    builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Basvur butonuna tiklaninca tiklayan kisinin bilgileri ve yolculuk ilani idleri ile veritabanina istek listesi olusturulur
                                            String basvurbutonunabasankisi = firebaseUser.getUid();
                                            HashMap<String, Object> istekdata = new HashMap<>();
                                            istekdata.put("yolculukilaniniverenkisi", yolculukilaniniverenkullanici);
                                            istekdata.put("basvurbutonunabasankisi", basvurbutonunabasankisi);
                                            istekdata.put("basvurulanyolculukilani", yolculukilaniid);
                                            firebaseFirestore.collection("isteklistesi").add(istekdata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(IlanDetaySayfasi.this, "İlana Basarili Bir Sekilde Basvuruldu...", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(IlanDetaySayfasi.this, "ilana şuan başvurulamıyor daha sonra tekrar deneyiniz!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                    builder.show();
                                }
                            });
                            basvurbutonunubasvurulduyap(yolculukilaniid);
                        }
                    }
                }
            }
        });
    }

    //Basvur butonunu basvuran kisi hangi ilana basvurduysa o ilani basvuruldu yapar.
    public void basvurbutonunubasvurulduyap(String yolculukilaniid){
        String sayfadakikisi=firebaseUser.getUid();
        firebaseFirestore.collection("isteklistesi").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        String basvurankisi=(String) snapshot.getString("basvurbutonunabasankisi");
                        String basvurulanilan=(String) snapshot.getString("basvurulanyolculukilani");
                        if (sayfadakikisi.matches(basvurankisi) && yolculukilaniid.matches(basvurulanilan)){//sayfadaki eger basvuran kisi ise ve ilanin idsi basvurulan id ise o ilandakini basvuruldu yapar
                            binding.basvurGotButonu.setText("BASVURULDU");
                            binding.basvurGotButonu.setEnabled(false);
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