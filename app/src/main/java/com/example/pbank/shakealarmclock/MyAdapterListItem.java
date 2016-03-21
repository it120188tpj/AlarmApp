package com.example.pbank.shakealarmclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyAdapterListItem extends ArrayAdapter {

    private static final String TAG = MyAdapterListItem.class.getSimpleName();

    private ArrayList<ListItem> listArray;
    private Context context;

    public MyAdapterListItem(Context context, ArrayList<ListItem> arrayList){
        super(context, R.layout.alarm_clock_item,  arrayList);
        this.listArray = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listArray.size();
    }

    @Override
    public Object getItem(int position) {
        return listArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int index, View convertView, final ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.alarm_clock_item, parent, false);
        }

        final ListItem listItem = listArray.get(index);

        //выбираем ИМЯ пункта
        String name_itemView = listItem.getName().toString();

        //отбираем ВРЕМЯ пункта
        int hh_itemView = listItem.getCalendar().get(Calendar.HOUR_OF_DAY);
        int mm_itemView = listItem.getCalendar().get(Calendar.MINUTE);
        String hh_itemView_s = (hh_itemView < 10 ? "0" + hh_itemView : String.valueOf(hh_itemView));
        String mm_itemView_s = (mm_itemView < 10 ? "0" + mm_itemView : String.valueOf(mm_itemView));

        //сэтим ВРЕМЯ пункта
        TextView tv_hh_itemView = (TextView) view.findViewById(R.id.item_alarmClock);
        tv_hh_itemView.setText(hh_itemView_s + ":" + mm_itemView_s);

        //сэтим ИМЯ пункта
        TextView tv_name_itemView = (TextView) view.findViewById(R.id.item_name);
        tv_name_itemView.setText(name_itemView);

        //сэтим ДНИ НЕДЕЛИ пункта
        TextView tv_item_alarmDays = (TextView) view.findViewById(R.id.item_alarmDays);
        List<Integer> arrayDays = listItem.getDays();

        String days = "";
        for (int i = 0; i < arrayDays.size(); i++) {
            if(days.equals("")) {
                days = getDayForNumberInWeek(arrayDays.get(i));
            }else{
                days = days + " " + getDayForNumberInWeek(arrayDays.get(i));
            }
        }

        tv_item_alarmDays.setText(days);


        //метод для кнопки УДАЛИТЬ пункт
        Button buttonDelete = (Button) view.findViewById(R.id.btn_trash);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //создаем диалоговое окно для подтверждения удаления
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.delete_item);
                //сэтим возможность закрытия окна по кпонке Back
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listArray.remove(index); //удаляем их списка пункт
                        notifyDataSetChanged(); //говорим нашему списку ОБНОВИСЬ!
                        dialog.dismiss(); // Отпускает диалоговое окно
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


        //этот метод для нажатия для этого пункта меню
        //будет использоваться для редактирования самого пункта и значения в нем
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ActivityAddAlarmClock.class);
                ListItem item = listArray.get(index);
                item.setIndex(index);
                intent.putExtra("itemEdit",item);
                ((Activity)context).startActivityForResult(intent,2);
            }
        });


        //метод для кнопки ВКЛ/ВЫКЛ пункт
        final ToggleButton buttonOnOff = (ToggleButton) view.findViewById(R.id.btn_OnOffBtn);
        buttonOnOff.setChecked(listItem.getOnOff());

        buttonOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(buttonOnOff.isChecked()){
                    listArray.get(index).setOnOff(true); //включаем пункт будильника
                    notifyDataSetChanged();
                }else{
                    listArray.get(index).setOnOff(false); //выключаем пункт будильника
                    notifyDataSetChanged();
                }

            }
        });


        return view;
    }

    private String getDayForNumberInWeek(Integer number){

        String dayForNumberInWeek = "";

        if(number.equals(1)){
            dayForNumberInWeek = "Вс";
        }

        if(number.equals(2)){
            dayForNumberInWeek = "Пн";
        }

        if(number.equals(3)){
            dayForNumberInWeek = "Вт";
        }

        if(number.equals(4)){
            dayForNumberInWeek = "Ср";
        }

        if(number.equals(5)){
            dayForNumberInWeek = "Чт";
        }

        if(number.equals(6)){
            dayForNumberInWeek = "Пт";
        }

        if(number.equals(7)){
            dayForNumberInWeek = "Сб";
        }

        return dayForNumberInWeek;

    }

}