package com.example.checktrends.wikipedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;

class WikipediaRecyclerAdapter extends RecyclerView.Adapter<WikipediaRecyclerAdapter.ViewHolder>{
    String[]array;

    WikipediaRecyclerAdapter(String[]array){
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_wikipedia, parent, false);
        WikipediaRecyclerAdapter.ViewHolder viewHolder = new WikipediaRecyclerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(array[position]);
    }

    @Override
    public int getItemCount() {
        return array.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }
}
