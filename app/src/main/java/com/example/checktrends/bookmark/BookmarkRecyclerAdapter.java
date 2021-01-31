package com.example.checktrends.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;

import java.util.List;

public class BookmarkRecyclerAdapter extends RecyclerView.Adapter<BookmarkRecyclerAdapter.ViewHolder>{
    Context context;
    private List<Bookmark> list;

    public BookmarkRecyclerAdapter(Context context, List<Bookmark> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_bookmark, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Bookmark bookmark = list.get(position);
        viewHolder.textTitle.setText(bookmark.getTitle());
        viewHolder.textAccessTime.setText(bookmark.getAccessTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textAccessTime;

        public ViewHolder(View view) {
            super(view);
            textTitle = view.findViewById(R.id.text_title);
            textAccessTime = view.findViewById(R.id.text_access_time);
        }
    }
}
