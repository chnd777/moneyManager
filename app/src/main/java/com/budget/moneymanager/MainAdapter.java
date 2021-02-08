package com.budget.moneymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class MainAdapter extends FirebaseRecyclerAdapter<ListData, MainAdapter.ViewHolder> {


    public MainAdapter(
            @NonNull FirebaseRecyclerOptions<ListData> options)
    {
        super(options);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent,false);
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
        }
    }


}
