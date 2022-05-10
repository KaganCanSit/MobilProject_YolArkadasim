package com.huso.yolarkadasim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class IlanlarSayfasi extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView yolculukilanlarirecyclerview;
    private SearchView searchViewrecyclerview;
    recyclerview_yolculukilanlari_adapter recyclerview_yolculukilanlari_adapter;
    private ArrayList<yolculukilanlaridizisi> yolculukilaniidlist;//yolculuk ilanlari dizisindekileri arraye atiyor
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolculukilanlari_sayfasi);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        searchViewrecyclerview=findViewById(R.id.search_bar_arama);
        yolculukilanlarirecyclerview =findViewById(R.id.yolculukilanlari_recyclerView);

        yolculukilaniidlist =new ArrayList<>();
        yolculukilanlarirecyclerview.setVisibility(View.INVISIBLE);

        searchview();
        yolculukilanlari();
    }

    public void searchview(){//search view da arama islemlerinin gerceklestirilmesi saglaniyor
        searchViewrecyclerview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                yolculukilanlarirecyclerview.setVisibility(View.VISIBLE);
                if (recyclerview_yolculukilanlari_adapter !=null){//adapterin bos olup olmaması kontolu yapiliyor
                    recyclerview_yolculukilanlari_adapter.getFilter().filter(newText);//girilen degere gore arama islemleri gerceklestiriliyor
                }
                if (newText.matches("")){
                    yolculukilanlarirecyclerview.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

    }
    public void yolculukilanlari(){//veritabanindan yolculukilanlarinin verilerini cekiyor
        CollectionReference collectionReference=firebaseFirestore.collection("yolculukilanlari");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {//is ilanlarini tarihlerine gore siraliyor
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                yolculukilaniidlist.clear();
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        Map<String,Object> isilanidata=snapshot.getData();
                        String yolculukadi=(String) isilanidata.get("yolculukadi");
                        String varisisinadi=(String) isilanidata.get("varisadresi");
                        String yolculukilanlariid=snapshot.getId();
                        String yolculukilaniniverenkullanici=(String) isilanidata.get("yolculukilaniniverenkisi");
                        String yolculukilaninintarihi=(String) isilanidata.get("tarih");

                        yolculukilanlaridizisi yolculukilanlaridizisi =new yolculukilanlaridizisi(yolculukilanlariid,yolculukadi,varisisinadi,yolculukilaniniverenkullanici,yolculukilaninintarihi);
                        yolculukilaniidlist.add(yolculukilanlaridizisi);//cekilen yolculuk ilanlarini bir liste yapisi seklinde diziye atiyor


                        yolculukilanlarirecyclerview.setLayoutManager(new LinearLayoutManager(IlanlarSayfasi.this));//recyclerview ın kaydırılması
                        recyclerview_yolculukilanlari_adapter =new recyclerview_yolculukilanlari_adapter(yolculukilaniidlist,firebaseFirestore,firebaseUser);//yolculukilanindaki degerleri adaptera aktariyoruz
                        yolculukilanlarirecyclerview.setAdapter(recyclerview_yolculukilanlari_adapter);//burada recyclerview ile adaptera bağlıyoruz
                        recyclerview_yolculukilanlari_adapter.notifyDataSetChanged();
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