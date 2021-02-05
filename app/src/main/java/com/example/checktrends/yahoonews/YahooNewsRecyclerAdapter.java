package com.example.checktrends.yahoonews;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.checktrends.R;

import java.util.List;

public class YahooNewsRecyclerAdapter extends RecyclerView.Adapter<YahooNewsRecyclerAdapter.ViewHolder>{
    Fragment fragment;
    Context context;
    private List<News>list;

    YahooNewsRecyclerAdapter(Fragment fragment, List<News>list){
        this.fragment = fragment;
        this.context = fragment.getActivity();
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_yahoo_news, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                onItemClick(position);
            }
        });

        viewHolder.cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                fragment.getActivity().getMenuInflater().inflate(R.menu.context_yahoo_news, contextMenu);

                contextMenu.findItem(R.id.menu_bookmark).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int position = viewHolder.getAdapterPosition();
                        registerBookmark(position);
                        return true;
                    }
                });
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = list.get(position);

        holder.textRank.setText((position + 1) + "．");

        Glide.with(context)
                .load(news.getJpgUrl())
                .into(holder.imageView);

        holder.textNewsTitle.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textRank;
        ImageView imageView;
        TextView textNewsTitle;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardView);
            textRank = view.findViewById(R.id.text_rank);
            imageView = view.findViewById(R.id.image_news_photo);
            textNewsTitle = view.findViewById(R.id.text_news_title);
        }
    }

    void onItemClick(int position){}

    void registerBookmark(int position){}
}
