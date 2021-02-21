package com.example.checktrends.googletrends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checktrends.R;

import java.util.ArrayList;
import java.util.List;

class GoogleTrendsRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    List<Object> list;

    List<String>visible;

    private final int VIEW_TYPE_DATE = 1;
    private final int VIEW_TYPE_TREND = 0;

    GoogleTrendsRecyclerAdapter(Context context, List<Object>list){
        this.context = context;
        this.list = list;

        visible = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        if(viewType == VIEW_TYPE_DATE){
            //日付行
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_google_trends_date, parent, false);
            viewHolder = new DateViewHolder(view);
        }else{
            //データ行
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.google_trends_result, parent, false);
            viewHolder = new TrendViewHolder(view);

            ((TrendViewHolder) viewHolder).buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    clickSearchButton(((Trend)list.get(position)).getTrendTitle());
                }
            });

            ((TrendViewHolder) viewHolder).buttonExpansion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = viewHolder.getAdapterPosition();
                    if (((TrendViewHolder) viewHolder).layoutExpansion.getVisibility() == View.GONE) {
                        ((TrendViewHolder) viewHolder).buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_up_arrow);
                        ((TrendViewHolder) viewHolder).layoutExpansion.setVisibility(View.VISIBLE);

                        visible.add(String.valueOf(position));
                    } else {
                        ((TrendViewHolder) viewHolder).buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_down_arrow);
                        ((TrendViewHolder) viewHolder).layoutExpansion.setVisibility(View.GONE);

                        visible.remove(String.valueOf(position));
                    }
                }
            });
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object object = list.get(position);

        if(object instanceof String){
            ((DateViewHolder) holder).textDate.setText((String)object);
        }else{
            ((TrendViewHolder) holder).textRank.setText(((Trend)object).getRank());
            ((TrendViewHolder) holder).textTrendTitle.setText(((Trend)object).getTrendTitle());
            ((TrendViewHolder) holder).textNumberNews.setText(((Trend)object).getNewsList().size() + "件の");

            ((TrendViewHolder) holder).recyclerView.setAdapter(new NewsRecyclerAdapter(context,((Trend)object).getNewsList()));

            if(visible.contains(String.valueOf(position))){
                ((TrendViewHolder) holder).layoutExpansion.setVisibility(View.VISIBLE);
                ((TrendViewHolder) holder).buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_up_arrow);
            }else{
                ((TrendViewHolder) holder).layoutExpansion.setVisibility(View.GONE);
                ((TrendViewHolder) holder).buttonExpansion.setBackgroundResource(R.drawable.icon_tenkai_down_arrow);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position) instanceof String){
            return VIEW_TYPE_DATE;
        }else{
            return VIEW_TYPE_TREND;
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;

        public DateViewHolder(View view) {
            super(view);
            textDate = view.findViewById(R.id.text_date);
        }
    }

    public static class TrendViewHolder extends RecyclerView.ViewHolder {
        TextView textRank,textTrendTitle,textNumberNews;
        ImageButton buttonSearch,buttonExpansion;
        LinearLayout layoutExpansion;
        RecyclerView recyclerView;

        public TrendViewHolder(View view) {
            super(view);
            textRank = view.findViewById(R.id.text_rank);
            textTrendTitle = view.findViewById(R.id.text_title);
            textNumberNews = view.findViewById(R.id.text_number_news);
            buttonSearch = view.findViewById(R.id.button_search);
            buttonExpansion = view.findViewById(R.id.button_expansion);
            layoutExpansion = view.findViewById(R.id.layout_expansion);
            recyclerView = view.findViewById(R.id.recyclerView);

            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);

            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }
    }

    void clickSearchButton(String word){}

}