package com.example.checktrends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultListAdapter extends BaseAdapter {
    private List<?> list;
    Object object;
    LayoutInflater inflater;

    private class ViewHolder{
        TextView textRank;
        TextView textTitle;
    }

    public ResultListAdapter(Context context, List<?>list){
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_result, parent, false);

            holder = new ViewHolder();
            holder.textRank = convertView.findViewById(R.id.text_rank);
            holder.textTitle = convertView.findViewById(R.id.text_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        object = list.get(position);
        holder.textRank.setText((position + 1) + "．");
        if(object instanceof News){
            holder.textTitle.setText(((News) object).getTitle());
        }else if(object instanceof String){
            holder.textTitle.setText((String)object);
        }

        return convertView;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }
}