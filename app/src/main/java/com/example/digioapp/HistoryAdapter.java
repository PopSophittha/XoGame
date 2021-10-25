package com.example.digioapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> mList;
    private View.OnClickListener onClickListener;

    public HistoryAdapter(List<HistoryModel> mList) {
        this.mList = mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no, txt_won;
        ImageButton btn_replay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_no = itemView.findViewById(R.id.txt_no);
            txt_won = itemView.findViewById(R.id.txt_won);

            btn_replay = itemView.findViewById(R.id.btn_replay);
            btn_replay.setTag(this);
            btn_replay.setOnClickListener(onClickListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.adapter_history, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel obj = mList.get(position);

        holder.txt_no.setText(String.valueOf(obj.getId()));

        String str_won = "";
        if (obj.getIsWon() == 2) {
            str_won = "Draw";
        } else if (obj.getUser() == 0 && obj.getIsWon() == 1) {
            str_won = "You Won";
        } else {
            str_won = "You Lose";
        }

        holder.txt_won.setText(str_won);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
