package com.budget.moneymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList<ListData> myList;
    int mLastPosition = 0;
    public MainAdapter(ArrayList<ListData> myList){
        this.myList= myList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ListData data= myList.get(position);

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
    @Override
    public int getItemCount() {
        return myList.size();
    }

}
