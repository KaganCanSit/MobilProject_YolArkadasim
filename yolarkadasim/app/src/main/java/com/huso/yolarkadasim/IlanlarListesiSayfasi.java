package com.huso.yolarkadasim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IlanlarListesiSayfasi extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView isteklistesirecyclerview;
    recyclerview_isteklistesi_adapter recyclerview_isteklistesi_adapter;
    private TextView istekkutusubosyazisi;
    ArrayList<isteklistesidizisi> isteklistesiidlist;
    internetdegistirlistener internetdegistirlistener=new internetdegistirlistener();
    SwipeRefreshLayout isteklistesireflesh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istek_listesi_sayfasi);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        isteklistesirecyclerview=findViewById(R.id.recyclerView_isteklistesi);

        isteklistesiidlist=new ArrayList<>();

        istekkutusubosyazisi=findViewById(R.id.istekkutusubos_textview);


        isteklistesireflesh=findViewById(R.id.isteklistesi_reflesh);
        isteklistesireflesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isteklistesireflesh.setRefreshing(false);
                isteklistesi();
            }
        });

        isteklistesi();
    }

    public void isteklistesi(){//Veritabanindaki istek listesindeki bilgileri cekiyoruz
        String sayfadakikisi=firebaseUser.getUid();
        System.out.println(sayfadakikisi);
        firebaseFirestore.collection("isteklistesi").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                isteklistesiidlist.clear();//her sayfa acildiginda silinip ciftli uretilmesi engellenmis olur
                if (querySnapshot!=null) {
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                        String yolculukilaniniverenkisi=(String) snapshot.getString("yolculukilaniniverenkisi");
                        String basvurbutonunabasankisi=(String) snapshot.getString("basvurbutonunabasankisi");
                        String basvurulanyolculukilaniid=(String) snapshot.getString("basvurulanyolculukilani");
                        String isteklistesiidleri=snapshot.getId();

                        if (sayfadakikisi.matches(yolculukilaniniverenkisi)){//sayfadaki kisi isilanini veren kisi ise onun sayfasinda istek listesi olusturuluyor
                            istekgonderenkisininprofili(basvurbutonunabasankisi,isteklistesiidleri,basvurulanyolculukilaniid);
                        }
                    }
                }
            }
        });
    }
    public void istekgonderenkisininprofili(String basvurbutonunabasankisi,String isteklistesiidleri,String basvurulanisilaniid){
        //Burada istek gonderen kisinin bilgilerini dizelere atarak recyclerview a aktarilmasi saglanir
        Date date = Calendar.getInstance().getTime();//anlik tarihi alma islemini gerceklestirir
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String tarih = dateFormat.format(date);

        //basvur butonuna basan kisinin profil bilgilerini cekmeye yarar
        firebaseFirestore.collection("Profiller").document(basvurbutonunabasankisi).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot!=null) {
                    String adsoyad = documentSnapshot.getString("advesoyad");
                    String sehir=documentSnapshot.getString("sehir");

                    firebaseFirestore.collection("yolculukilanlari").document(basvurulanisilaniid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot1, @Nullable FirebaseFirestoreException error) {
                            if (documentSnapshot1!=null) {
                                String yolculukadi = documentSnapshot1.getString("yolculukadi");
                                String varisadi= documentSnapshot1.getString("varisadresi");

                                //Alinan bilgileri bir istek listesi dizisine aktarmasi islemlerini gerceklestirir
                                isteklistesidizisi isteklistesidizisi=new isteklistesidizisi(isteklistesiidleri,adsoyad,basvurbutonunabasankisi,basvurulanisilaniid,sehir,tarih,yolculukadi,varisadi);
                                isteklistesiidlist.add(isteklistesidizisi);


                                //Bu bilgilerin recyclerview adaptera aktarip recyclerviewdaki ayarlanma islemleri gerceklestirilir
                                isteklistesirecyclerview.setLayoutManager(new LinearLayoutManager(IlanlarListesiSayfasi.this));//recyclerview ın kaydırılması
                                recyclerview_isteklistesi_adapter = new recyclerview_isteklistesi_adapter(isteklistesiidlist,firebaseFirestore, firebaseUser);//messag gonderen kisi ve mesagın adını adaptera gonderiyoruz
                                isteklistesirecyclerview.setAdapter(recyclerview_isteklistesi_adapter);//burada recyclerview ile adaptera bağlıyoruz
                                recyclerview_isteklistesi_adapter.notifyDataSetChanged();

                                //Sayfadaki istek sayisinin kontrolleri yapilarak recyclerview ve istekkutusu bos yazisinin konrolu yapilir
                                int isteksayisi = recyclerview_isteklistesi_adapter.getItemCount();
                                if (isteksayisi < 1) {
                                    isteklistesirecyclerview.setVisibility(View.INVISIBLE);
                                    istekkutusubosyazisi.setVisibility(View.VISIBLE);
                                } else if (isteksayisi >= 1) {
                                    isteklistesirecyclerview.setVisibility(View.VISIBLE);
                                    istekkutusubosyazisi.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    });
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