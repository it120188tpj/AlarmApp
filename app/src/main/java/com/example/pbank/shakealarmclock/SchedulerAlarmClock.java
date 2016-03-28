package com.example.pbank.shakealarmclock;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Класс который отвечает за сохранение в память будильников
 */
public class SchedulerAlarmClock extends Activity {

    private final static String SAVED_LIST = "saved_list";
    private List<ListItem> arrayListItem;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;
    private Context context;

    public SchedulerAlarmClock(){
    }

    public void setArrayListItem(List<ListItem> arrayListItem) {
        this.arrayListItem = arrayListItem;
    }

    public void setAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Метод который сохраняет данные в память (Preferences)
     */
    private void saveDataToMemory() {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.clear();

        for (int i = 0; i < arrayListItem.size(); i++) {
            ed.putString(SAVED_LIST + i, arrayListItem.get(i).toJSON());
            ed.putString(SAVED_LIST + i, arrayListItem.get(i).toJSON());
        }

        ed.putInt("list_size", arrayListItem.size());
        ed.commit();
    }

    //метод когда изменяется список будильников и нужно проверить и если что запланировать будильники на сегодня
    public void saveAlarmTodayToSheduler() {

        saveDataToMemory();

        //дата и время текущий момент
        Calendar dateTimeNow = Calendar.getInstance();
        dateTimeNow.setTime(new Date(System.currentTimeMillis()));
        //номер дня недели сегодня
        int dayInWeekNow = dateTimeNow.get(Calendar.DAY_OF_WEEK);
        //время в милисекундах в текущий момент
        long dateTimeNowInMilSec = dateTimeNow.getTimeInMillis();
        //перебираем список будильников и находит тут который еще должен сегодня сработать
        for (int i = 0; i < arrayListItem.size(); i++) {
            //получаем флаг включен будильник или нет
            boolean onOff = arrayListItem.get(i).getOnOff();
            //получаем список дней недели
            List<Integer> days = arrayListItem.get(i).getDays();
            //если будильник включен(true), то планируем его
            if(onOff){
                //ищем день недели в списке который совпадает с сегодня
                for (int j = 0; j < days.size(); j++) {
                    //если нашли совпадающий день, то создаем будильник на сегодня
                    if (days.get(j).equals(dayInWeekNow)) {
                        int hour = arrayListItem.get(i).getCalendar().get(Calendar.HOUR_OF_DAY);
                        int minute = arrayListItem.get(i).getCalendar().get(Calendar.MINUTE);
                        Calendar alarmToday = Calendar.getInstance();
                        alarmToday.setTime(new Date(System.currentTimeMillis()));
                        alarmToday.set(Calendar.HOUR_OF_DAY, hour);
                        alarmToday.set(Calendar.MINUTE, minute);
                        alarmToday.set(Calendar.SECOND, 00);
                        long dateTimeFutureInMilSec = alarmToday.getTimeInMillis();

                        //если есть запланированные будильники на сегодня, то сейчас мы это сделаем
                        if(dateTimeFutureInMilSec > dateTimeNowInMilSec) {

                            Intent intentAlarm = new Intent(context, MyReceiver.class);
                            intentAlarm.putExtra("startClass","ActivityAlarm");
                            intentAlarm.putExtra("name", arrayListItem.get(i).getName().toString());
                            intentAlarm.putExtra("time", hour + ":" + minute);
                            intentAlarm.putExtra("rington", arrayListItem.get(i).getRingtone().toString());

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeFutureInMilSec, pendingIntent);

                        }
                    }
                }
            }
        }
    }


    //метод который преобразует список будильников в планировщик заданий для будильника
//    public void saveListAlarmToSheduler(){
//
//        //дата и время текущий момент
//        Calendar dateTimeNow = Calendar.getInstance();
//        dateTimeNow.setTime(new Date(System.currentTimeMillis()));
//        int dayInWeekNow = dateTimeNow.get(Calendar.DAY_OF_WEEK);
//        long dateTimeNowInMilSec = dateTimeNow.getTimeInMillis();
//
//        //формируем задание формирования будильников на завтра
////        intentAlarm = new Intent(this, MyReceiver.class);
////        intentAlarm.putExtra("startClass","ActivityAlarm");
////        pendingIntent = PendingIntent.getBroadcast(this, 999, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
////        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTommorow.getTimeInMillis(), pendingIntent);
//
//        //перебираем список будильников
//        for (int i = 0; i < list.size(); i++) {
//
//            //получаем список дней недели
//            List<Integer> days = list.get(i).getDays();
//
//            //ищем день недели в списке который на день больше чем сегодня
//            for (int j = 0; j < days.size(); j++) {
//                //если нашли, то создаем будильник
//                if(days.get(j).equals(dayInWeekNow+1)){
//
//                    int hour = list.get(i).getCalendar().get(Calendar.HOUR_OF_DAY);
//                    int minute = list.get(i).getCalendar().get(Calendar.MINUTE);
//
//                    Calendar alarmTommorow = list.get(i).getCalendar();
//                    alarmTommorow.add(Calendar.DATE, 1);
//                    alarmTommorow.set(Calendar.HOUR_OF_DAY, hour);
//                    alarmTommorow.set(Calendar.MINUTE, minute);
//
//                    //intentAlarm = new Intent(this, MyReceiver.class);
//
//                    //intentAlarm.putExtra("startClass","ActivityAlarm");
//                    intentAlarm.putExtra("name",list.get(i).getName().toString());
//                    intentAlarm.putExtra("time",hour + ":" + minute);
//                    intentAlarm.putExtra("rington",list.get(i).getRingtone().toString());
//
//                    pendingIntent = PendingIntent.getBroadcast(this, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTommorow.getTimeInMillis(), pendingIntent);
//
//                }
//            }
//
//        }
//
//    }

}
