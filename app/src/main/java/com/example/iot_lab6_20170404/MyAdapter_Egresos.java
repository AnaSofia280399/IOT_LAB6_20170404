package com.example.iot_lab6_20170404;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab6_20170404.dto.Egreso;

import java.util.List;

public class MyAdapter_Egresos extends RecyclerView.Adapter<MyViewHolder_egresos> {

    private Context context;
    private List<Egreso> dataList;

    public void setSearchList(List<Egreso> dataSearchList){
        this.dataList = dataSearchList;
        notifyDataSetChanged();
    }

    public MyAdapter_Egresos(Context context, List<Egreso> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder_egresos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder_egresos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_egresos holder, int position) {

        holder.rec_title.setText(dataList.get(position).getTitulo());

        holder.rec_fecha.setText(dataList.get(position).getFecha());
        holder.rec_monto.setText(String.format("%.2f", dataList.get(position).getMonto()));

        //aqui le asigna los valores que aparecen en el recycle view


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //manda estos valores a perfil supervisor

                Intent intent = new Intent(context, Egresos_DetailActivity.class);
                // si sigue logueado no necesito mandar ningun extra
                // si necesito mandar extras je

                intent.putExtra("id", dataList.get(holder.getAdapterPosition()).getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

}

class MyViewHolder_egresos extends RecyclerView.ViewHolder{

    TextView rec_title, rec_fecha, rec_monto;
    CardView recCard;
    public MyViewHolder_egresos(@NonNull View itemView){
        super(itemView);

        rec_title = itemView.findViewById(R.id.recTitle);
        rec_monto = itemView.findViewById(R.id.recMonto);
        rec_fecha = itemView.findViewById(R.id.recFecha);

        recCard = itemView.findViewById(R.id.RecCard);


    }
}
