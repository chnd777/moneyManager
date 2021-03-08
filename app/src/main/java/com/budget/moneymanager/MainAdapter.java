package com.budget.moneymanager;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainAdapter extends FirebaseRecyclerAdapter<ListData, MainAdapter.ViewHolder> {
    private RecyclerInterface recyclerInterface;

    public MainAdapter(@NonNull FirebaseRecyclerOptions<ListData> options, RecyclerInterface recyclerInterface)
    {
        super(options);
        this.recyclerInterface=recyclerInterface;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent,false);
        //ViewHolder viewHolder = new ViewHolder(null);

        return new MainAdapter.ViewHolder(view);
    }
    @Override
    protected void
    onBindViewHolder(@NonNull ViewHolder holder,
                     int position, @NonNull ListData data)
    {
        holder.tv1.setText(data.getlAmt());
        holder.tv2.setText(data.getlKeyword());
        holder.tv3.setText(data.getlDate());
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv1;
        private final TextView tv2;
        private final TextView tv3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1= itemView.findViewById(R.id.listAmount);
            tv2= itemView.findViewById(R.id.listReason);
            tv3= itemView.findViewById(R.id.listDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerInterface.onClick((getAdapterPosition()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recyclerInterface.onLongClick(getAdapterPosition());
                    return true;
                }
            });
        }
    }

}
