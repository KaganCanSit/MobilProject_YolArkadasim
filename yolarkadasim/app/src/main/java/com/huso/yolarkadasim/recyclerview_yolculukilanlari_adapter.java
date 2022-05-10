package com.huso.yolarkadasim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class recyclerview_yolculukilanlari_adapter extends RecyclerView.Adapter<recyclerview_yolculukilanlari_adapter.Postyolculukilanlari> {
    ArrayList<yolculukilanlaridizisi> yolculukilaniidlist;
    ArrayList<yolculukilanlaridizisi> getYolculukilaniidlist;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    //Gonderilen degerler cekilerek recyclerview ayarlanmasi saglaniyor
    public recyclerview_yolculukilanlari_adapter(ArrayList<yolculukilanlaridizisi> yolculukilaniidlist, FirebaseFirestore firebaseFirestore, FirebaseUser firebaseUser) {
        this.yolculukilaniidlist = yolculukilaniidlist;
        this.getYolculukilaniidlist = yolculukilaniidlist;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseUser = firebaseUser;

    }

    @NonNull
    @Override
    public Postyolculukilanlari onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recyclerview_yolculukilanlari, parent, false);
        return new Postyolculukilanlari(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postyolculukilanlari holder, int position) {
        //buradaki degerler recyclerview daki textlere ayarlanma islemi yapiliyor
        Context context = holder.itemView.getContext();
        yolculukilanlaridizisi temp = yolculukilaniidlist.get(holder.getAdapterPosition());
        holder.yolculukadi.setText(" " + yolculukilaniidlist.get(holder.getAdapterPosition()).getYolculukadi() + "-" + yolculukilaniidlist.get(holder.getAdapterPosition()).getvarisIsinadi());
        holder.yolculukilanitarihi.setText(yolculukilaniidlist.get(holder.getAdapterPosition()).getYolculuktarihi());
        //isinadina,turkiyeyazisina,turkiyeyazisiimageviewa,ekiparkadasi yazisindan herhangi birine tiklanmasinda ayni islemi yapip o is ilani bilgilerine ulasiyor
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IlanDetaySayfasi.class);
                intent.putExtra("yolculukilaniid", temp.getYolculukid());
                context.startActivity(intent);
            }
        });

        holder.yolculukilanisil.setVisibility(View.INVISIBLE);
        if (yolculukilaniidlist.get(holder.getAdapterPosition()).getYolculukilaniniverenkullanici().matches(firebaseUser.getUid())) {//is ilani veren kullanici kendi sayfasinda silme butonunun acik olacagini gosteriyor
            holder.yolculukilanisil.setVisibility(View.VISIBLE);//is ilanı sil butonunun acilmasi
            holder.yolculukilanisil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Yolculuk ilanını sil")
                            .setMessage("Yolculuk İlanını Listeden Kaldırmak İstermisiniz?")
                            .setNegativeButton("Hayır", null)
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //tiklanilan is ilani id sine gore silme islemi gerceklestiriliyor
                                    firebaseFirestore.collection("yolculukilanlari").document(yolculukilaniidlist.get(holder.getAdapterPosition()).getYolculukid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "ilan basarı ile silindi", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return yolculukilaniidlist.size();
    }

    public Filter getFilter() {//is ilanlarini girilen isimlere gore arama islemini gerceklestirir filtreleme islemlerini yapar
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence == null | charSequence.length() == 0) {
                filterResults.count = getYolculukilaniidlist.size();
                filterResults.values = getYolculukilaniidlist;
            } else {
                String searchar = charSequence.toString().toLowerCase();
                List<yolculukilanlaridizisi> data = new ArrayList<>();

                for (yolculukilanlaridizisi yolculukilanlaridizisi : getYolculukilaniidlist) {
                    if (yolculukilanlaridizisi.getYolculukadi().toLowerCase().contains(searchar)) {
                        data.add(yolculukilanlaridizisi);
                    }
                }
                filterResults.count = data.size();
                filterResults.values = data;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            yolculukilaniidlist = (ArrayList<yolculukilanlaridizisi>) filterResults.values;
            notifyDataSetChanged();
        }
    };


    class Postyolculukilanlari extends RecyclerView.ViewHolder {
        TextView yolculukadi, yolculukilanitarihi, turkiyeyazisi, ekiparkadasiyazisi;
        ImageView yolculukilanisil, turkiyeyazisiimageview;

        public Postyolculukilanlari(@NonNull View itemView) {
            super(itemView);
            yolculukadi = itemView.findViewById(R.id.yolculukadi_textview);
            yolculukilanisil = itemView.findViewById(R.id.yolculukilanisil_imageview);
            yolculukilanitarihi = itemView.findViewById(R.id.yolculukilani_tarih_textview);
            turkiyeyazisi = itemView.findViewById(R.id.Turkiye_yazisi_textview);
            ekiparkadasiyazisi = itemView.findViewById(R.id.Ekiparkadasi_yazisi_textview);
            turkiyeyazisiimageview = itemView.findViewById(R.id.Turkiye_yazisi_imageview);
        }
    }
}
