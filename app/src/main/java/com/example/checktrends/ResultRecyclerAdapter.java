package com.example.checktrends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder>{
    Context context;
    private List<?> list;
    Object object;

    RecyclerViewOnClick recyclerViewOnClick;

    public ResultRecyclerAdapter(Context context,List<?> list,RecyclerViewOnClick recyclerViewOnClick){
        this.context = context;
        this.list = list;
        this.recyclerViewOnClick = recyclerViewOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                object = list.get(viewHolder.getAdapterPosition());
                recyclerViewOnClick.onClick(object);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                object = list.get(viewHolder.getAdapterPosition());
                if(object instanceof String){
                    recyclerViewOnClick.onLongClick(object);
                }
                return true;
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.textRank.setText((position + 1) + "．");

        object = list.get(position);
        if(object instanceof com.example.checktrends.yahoonews.News){
            viewHolder.textTitle.setText(((com.example.checktrends.yahoonews.News) object).getTitle());
        }else if(object instanceof String){
            viewHolder.textTitle.setText((String)object);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textRank;
        TextView textTitle;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            textRank = view.findViewById(R.id.text_rank);
            textTitle = view.findViewById(R.id.text_title);
        }
    }

}
