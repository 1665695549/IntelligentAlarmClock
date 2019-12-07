package alarmclass;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.intelligentalarmclock.LogInfo;
import com.example.intelligentalarmclock.R;
import com.example.intelligentalarmclock.db.Alarm;
import org.litepal.LitePal;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> { //一个adapter里有所有item的Viewholder对象

    private List<Alarm> m_alarmList;//保存List

    static class ViewHolder extends  RecyclerView.ViewHolder{ //内部类，继承自 RecyclerView.ViewHolder
        TextView mitem_name,alarm_name,AP_item;    //name成员变量，存储自定义布局上的Text控件，每一个item会有一个ViewHolder对象
        Button deleteButton,editButton;    //button成员变量，存储存储自定义布局上的Button控件，每一个item会有一个ViewHolder对象
        Switch aSwitch;    //switch成员变量，存储存储自定义布局上的Switch控件，每一个item会有一个ViewHolder对象

        public ViewHolder(View view){ //构造函数，传入自定义布局
            super(view);
            LogInfo.d("AlarmAdapter","AlarmAdapter.ViewHolder structure");
            mitem_name=view.findViewById(R.id.item_name); //获取自定义布局上的控件
            alarm_name=view.findViewById(R.id.alarm_name);
            AP_item=view.findViewById(R.id.AP_item);
            deleteButton=view.findViewById(R.id.delete);
            editButton=view.findViewById(R.id.edit);
            aSwitch=view.findViewById(R.id.switch_county);
        }
    }

    public  AlarmAdapter(List<Alarm> alarmList){ //adapter的构造函数，需存入所有的itemList
        LogInfo.d("AlarmAdapter","AlarmAdapter construct start");
        this.m_alarmList=alarmList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) { //生成ViewHolder对象(就是每个item)的对象
        LogInfo.d("AlarmAdapter","AlarmAdapter.onCreateViewHolder");
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.arlam_item,viewGroup,false);//获取自定义的item布局
        ViewHolder viewHolder=new ViewHolder(view); //holder对象(item对象)放那一个布局
        return viewHolder;
    }
    @Override
    public int getItemCount() {
        //返回所有的item数（holder数）
        return m_alarmList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        LogInfo.d("AlarmAdapter","AlarmAdapter.onBindViewHolder,position="+position);
        final Alarm alarm=m_alarmList.get(position);
        String itemName = alarm.getHour()+":"+alarm.getMinute();  //holder里面存有list
        final String APTime=alarm.getAPm();
        final String alarmName;
        if (alarm.getRepeate().indexOf("永不")!=-1){
            alarmName=alarm.getTitle();
        }else{
            alarmName=alarm.getTitle()+"，"+alarm.getRepeate();
        }

        if (alarm.getVality()){
            LogInfo.d("alarmVality is true");
            holder.mitem_name.setTextColor(Color.argb(190,255,255,255));
            holder.AP_item.setTextColor(Color.argb(190,255,255,255));
            holder.alarm_name.setTextColor(Color.argb(190,255,255,255));
            holder.aSwitch.setChecked(true);
        }else{
            LogInfo.d("alarmVality is false");
            holder.mitem_name.setTextColor(Color.argb(50,255,255,255));
            holder.AP_item.setTextColor(Color.argb(50,255,255,255));
            holder.alarm_name.setTextColor(Color.argb(50,255,255,255));
            holder.aSwitch.setChecked(false);
        }
        holder.mitem_name.setText(itemName);        //显示item的名字
        holder.AP_item.setText(APTime);
        holder.alarm_name.setText(alarmName);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInfo.d("AlarmAdapter","deleteButton onClick position="+position);
                //AlarmActivity.setSelectedAlarmID(position+1);
                AlarmActivity alarmActivity=(AlarmActivity)v.getContext();
                alarmActivity.deleteAlarm(alarm.getAlarmID());
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogInfo.d("AlarmAdapter","editButton onClick position="+position);
                AlarmActivity.setSelectedAlarmID(alarm.getAlarmID());
                AlarmActivity alarmActivity=(AlarmActivity)view.getContext();
                alarmActivity.startEditAlarmActivity(alarm.getAlarmID());
            }
        });
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    LogInfo.d("set alarmID="+alarm.getAlarmID()+"valide");
                    List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(alarm.getAlarmID())).find(Alarm.class);
                    if (0 != alarmList.size()){
                        LogInfo.d("set alarm data status");
                        alarmList.get(0).setVality(true);
                        alarmList.get(0).save();
                        alarmList=LitePal.where("alarmID=?",String.valueOf(alarm.getAlarmID())).find(Alarm.class);
                        LogInfo.d("alarm vality="+alarmList.get(0).getVality());
                        holder.mitem_name.setTextColor(Color.argb(190,255,255,255));
                        holder.AP_item.setTextColor(Color.argb(190,255,255,255));
                        holder.alarm_name.setTextColor(Color.argb(190,255,255,255));
                        AlarmActivity alarmActivity=(AlarmActivity)compoundButton.getContext();
                        alarmActivity.createNotify(alarm.getAlarmID());
                    }else{
                        LogInfo.d("*********wrong alarmID nit exict");
                    }
                }else {
                    LogInfo.d("set alarmId="+alarm.getAlarmID()+" invalide");
                    List<Alarm> alarmList=LitePal.where("alarmID=?",String.valueOf(alarm.getAlarmID())).find(Alarm.class);
                    if (0 != alarmList.size()){
                        alarmList.get(0).setVality(false);
                        alarmList.get(0).save();
                        alarmList=LitePal.where("alarmID=?",String.valueOf(alarm.getAlarmID())).find(Alarm.class);
                        LogInfo.d("alarm vality="+alarmList.get(0).getVality());
                        holder.mitem_name.setTextColor(Color.argb(50,255,255,255));
                        holder.AP_item.setTextColor(Color.argb(50,255,255,255));
                        holder.alarm_name.setTextColor(Color.argb(50,255,255,255));
                        AlarmActivity alarmActivity=(AlarmActivity)compoundButton.getContext();
                        alarmActivity.cancelNotify(alarm.getAlarmID());
                    }else{
                        LogInfo.d("*********wrong alarmID not exict");
                    }
                }
            }
        });
    }
}
