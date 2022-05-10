package com.huso.yolarkadasim;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class recyclerview_isteklistesi_adapter extends RecyclerView.Adapter<recyclerview_isteklistesi_adapter.Postisteklistesi> {
    ArrayList<isteklistesidizisi> isteklistesiidlist;
    ArrayList<isteklistesidizisi> getisteklistesiidlist;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    //Diziye aktarilan degerleri ve isteklistesinde gonderilen degerleri cekmeye yarar
    public recyclerview_isteklistesi_adapter(ArrayList<isteklistesidizisi> isteklistesiidlist, FirebaseFirestore firebaseFirestore, FirebaseUser firebaseUser) {
        this.isteklistesiidlist = isteklistesiidlist;
        this.getisteklistesiidlist = isteklistesiidlist;
        this.firebaseFirestore = firebaseFirestore;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public Postisteklistesi onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recyclerview_isteklistesi, parent, false);
        return new recyclerview_isteklistesi_adapter.Postisteklistesi(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Postisteklistesi holder, int position) {
        Context context = holder.itemView.getContext();
        isteklistesidizisi temp = isteklistesiidlist.get(holder.getAdapterPosition());

        //Burada isilanindaki degerleri alip o id ye gore o bilgileri cekip diziden gonderilen degerlere gore recyclerviewdaki yerlere yazmaya yariyor
        holder.isteklistesitarih.setText(isteklistesiidlist.get(holder.getAdapterPosition()).getTarih());

        if (isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesiadvesoyad() == null || isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesiadvesoyad().matches("")) {
            holder.isteklistesiadvesoyad.setText("İsim Bilgisi Girilmedi!");

        } else {
            holder.isteklistesiadvesoyad.setText(isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesiadvesoyad());
        }
        if (isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesisehir() == null || isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesisehir().matches("")) {
            holder.isteklistesisehirtextview.setText("Türkiye");

        } else {
            holder.isteklistesisehirtextview.setText(isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesisehir());
        }
        if (isteklistesiidlist.get(holder.getAdapterPosition()).getYolculukadi() == null || isteklistesiidlist.get(holder.getAdapterPosition()).getYolculukadi().matches("")) {
            holder.isteklistesibasvurulanilan.setText(" ilanınız silinmiş");

        } else {
            holder.isteklistesibasvurulanilan.setText(isteklistesiidlist.get(holder.getAdapterPosition()).getYolculukadi() + "-" + isteklistesiidlist.get(holder.getAdapterPosition()).getVarisadi() + " ilanınıza başvuru yapıldı");

        }


        //Burada isteklistesindeki recyclerviewdaki degerlere tiklanarak idlerin karsiya atamalarini sagliyoruz
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, istekListesindekiKisileriGorSayfasi.class);
                intent.putExtra("isteklistesindekikisiler", temp.getBasvurankisi());
                intent.putExtra("isteklistesiidleri", temp.getIsteklistesiid());
                context.startActivity(intent);
            }
        });

        //Tiklanan kisinin id sine gore silme islemini gerceklestirir
        holder.kisiyisil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Kisiyi Sil")
                        .setMessage("Kisiyi Listeden Kaldırmak İstermisiniz?")
                        .setNegativeButton("Hayır", null)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesiid() != null) {
                                    firebaseFirestore.collection("isteklistesi").document(isteklistesiidlist.get(holder.getAdapterPosition()).getIsteklistesiid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Kisi Basari İle Silinmistir...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }
                        }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return isteklistesiidlist.size();
    }

    class Postisteklistesi extends RecyclerView.ViewHolder {
        TextView isteklistesiadvesoyad, isteklistesisehirtextview, isteklistesibasvurulanilan, isteklistesitarih;
        ImageView kisiyisil;
        CardView isteklistesicardview;

        public Postisteklistesi(@NonNull View itemView) {
            super(itemView);
            isteklistesiadvesoyad = itemView.findViewById(R.id.isteklistesiadsoyad_textview);
            kisiyisil = itemView.findViewById(R.id.kisiyisilme_imageview);
            isteklistesisehirtextview = itemView.findViewById(R.id.isteklistesi_sehir_textview);
            isteklistesibasvurulanilan = itemView.findViewById(R.id.isteklistesi_basvurulanilanadi_textview);
            isteklistesitarih = itemView.findViewById(R.id.isteklistesi_tarih_textview);
            isteklistesicardview = itemView.findViewById(R.id.isteklistesi_cardview);
        }
    }
}
