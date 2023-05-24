package com.example.facepaint.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facepaint.EditActivity;
import com.example.facepaint.MainActivity;
import com.example.facepaint.Models.Model;
import com.example.facepaint.PaintActivity;
import com.example.facepaint.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<Model> list;
    Context context;

    public Adapter(ArrayList<Model> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Model mdl=list.get(position);
        holder.image.setImageResource(mdl.getImage());
        holder.name.setText(mdl.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, EditActivity.class);
                intent.putExtra("image",mdl.getImage());
                intent.putExtra("type",1);
                context.startActivity(intent);
                Toast.makeText(context, "Paint is Clicked.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchBox(ArrayList<Model> li) {
        list=li;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=(ImageView) itemView.findViewById(R.id.img);
            name=(TextView) itemView.findViewById(R.id.name);
        }
    }
}
