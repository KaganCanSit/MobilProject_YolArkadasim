package com.huso.yolarkadasim;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.huso.yolarkadasim.databinding.ActivityIlanvermapBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class IlanVermeSayfasi extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityIlanvermapBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;
    ActivityResultLauncher<String> permissionLauncher;
    SharedPreferences sharedPreferences;
    Boolean info;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityIlanvermapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerLauncher();

        sharedPreferences=this.getSharedPreferences("com.huso.yolarkadasim",MODE_PRIVATE);
        info=false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                info=sharedPreferences.getBoolean("info",false);

                if (!info){
                    LatLng kullaniciadresi=new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(kullaniciadresi));
                    mMap.addMarker(new MarkerOptions().position(kullaniciadresi));
                    sharedPreferences.edit().putBoolean("info",true).apply();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"Harita için izine ihtiyaç var!",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            }
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastlocation!=null){
                LatLng lastuserlocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lastuserlocation));
            }
        }
    }

    private void registerLauncher(){
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    if(ContextCompat.checkSelfPermission(IlanVermeSayfasi.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                        Location lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastlocation!=null){
                            LatLng lastuserlocation=new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,15));
                        }
                    }
                }else {
                    Toast.makeText(IlanVermeSayfasi.this,"İzin verildi",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        String yolculukadi=binding.yolculukadiEdittext.getText().toString();
        String varisadresi=binding.varisisinadiEdittext.getText().toString();
        String aciklama=binding.aciklamaEdittext.getText().toString();
        String yolculukilaniniverenkisi=firebaseUser.getUid();

        Date date = Calendar.getInstance().getTime();//anlik tarihi almamimizi sagliyor
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String tarih = dateFormat.format(date);//tarih degerini stringe ceviriyor

        if (yolculukadi.matches("") || aciklama.matches("") || varisadresi.matches("")){//isin adi ve aciklamasi bos kontrolu yapiliyor
            Toast.makeText(IlanVermeSayfasi.this,"Bos Alan Mevcut",Toast.LENGTH_LONG).show();
        }else {
            binding.yolculukilaniverbuton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(IlanVermeSayfasi.this,"Bos Alan Mevcut",Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(IlanVermeSayfasi.this)
                            .setTitle("Yolculuk ilanını ver")
                            .setMessage("Yolculuk İlanını Vermek İstermisiniz?")
                            .setNegativeButton("Hayır", null)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HashMap<String,Object> yolculukilanlaridata=new HashMap<>();
                                    yolculukilanlaridata.put("yolculukadi",yolculukadi);
                                    yolculukilanlaridata.put("varisadresi",varisadresi);
                                    yolculukilanlaridata.put("aciklama",aciklama);
                                    yolculukilanlaridata.put("yolculukilaniniverenkisi",yolculukilaniniverenkisi);
                                    yolculukilanlaridata.put("date", FieldValue.serverTimestamp());
                                    yolculukilanlaridata.put("tarih",tarih);
                                    yolculukilanlaridata.put("latitude",latLng.latitude);
                                    yolculukilanlaridata.put("longitude",latLng.longitude);
                                    firebaseFirestore.collection("yolculukilanlari").add(yolculukilanlaridata).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(IlanVermeSayfasi.this,"ilan basariyla verildi...",Toast.LENGTH_LONG).show();
                                            binding.yolculukadiEdittext.setText("");
                                            binding.aciklamaEdittext.setText("");
                                            binding.varisisinadiEdittext.setText("");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(IlanVermeSayfasi.this,"ilan veremiyoruz daha sonra tekrar deneyiniz!",Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }).show();
                }
            });
        }
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