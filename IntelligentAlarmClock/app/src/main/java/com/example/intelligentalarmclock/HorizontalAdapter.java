package com.example.intelligentalarmclock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {
    private List<HourlyInfo> mlist;//保存所有布局的子项

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView time,weatherStatus,temp;

        public ViewHolder(View view){
            super(view);
            LogInfo.d("ViewHolder constraction start.Thread="+Thread.currentThread().getId());
            time=view.findViewById(R.id.hour_time);
            weatherStatus=view.findViewById(R.id.hour_weather);
            temp=view.findViewById(R.id.hour_temp);
        }
    }

    public HorizontalAdapter(List<HourlyInfo> list){
        //LogInfo.d("HorizontalAdapter start.ThreadID="+Thread.currentThread().getId());
        mlist=list;
    }

    //创建ViewHolder，要为每个子项创建一个ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogInfo.d("onCreateViewHolder start");
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hour_info_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    //获取子项数量
    @Override
    public int getItemCount() {
        return mlist.size();
    }

    //把ViewHolder与list中的数据绑定
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogInfo.d("onBindViewHolder1 start.Thread="+Thread.currentThread().getId());
        HourlyInfo hourlyInfo=mlist.get(position);
        holder.time.setText(hourlyInfo.getTime());
        holder.weatherStatus.setText(hourlyInfo.getWeatherInfo());
        holder.temp.setText(hourlyInfo.getTemp());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        LogInfo.d("onBindViewHolder2 start");
    }

    public void refreshAdapterList(List<HourlyInfo> hourlyInfoList){
        mlist=hourlyInfoList;
    }
}
