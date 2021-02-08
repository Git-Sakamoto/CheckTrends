package com.example.checktrends.yahoonews;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;

import java.util.List;

class YahooBookmarkRecyclerAdapter extends RecyclerView.Adapter<YahooBookmarkRecyclerAdapter.ViewHolder>{
    Fragment fragment;
    Context context;
    private List<Bookmark> list;

    YahooBookmarkRecyclerAdapter(Fragment fragment, List<Bookmark> list){
        this.fragment = fragment;
        this.context = fragment.getActivity();
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_yahoo_bookmark, parent, false);

        YahooBookmarkRecyclerAdapter.ViewHolder viewHolder = new YahooBookmarkRecyclerAdapter.ViewHolder(view);

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                onItemClick(position);
            }
        });

        viewHolder.linearLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                fragment.getActivity().getMenuInflater().inflate(R.menu.context_delete, contextMenu);

                contextMenu.findItem(R.id.menu_delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int position = viewHolder.getAdapterPosition();
                        selectDeleteBookmark(position);
                        return true;
                    }
                });
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = list.get(position);
        holder.textTitle.setText(bookmark.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textTitle;

        public ViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.linearLayout);
            textTitle = view.findViewById(R.id.text_title);
        }
    }

    void onItemClick(int position){}

    void selectDeleteBookmark(int position){}
}
