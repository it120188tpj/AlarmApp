package com.example.pbank.shakealarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class SchedulerAlarmClock extends Activity {

    private List<ListItem> list;
    private PendingIntent pendingIntent;
    private Intent intentAlarm;
    private AlarmManager alarmManager;

    public SchedulerAlarmClock(List<ListItem> list, AlarmManager alarmManager){
        Log.d("myLogs", "0");
        this.list = list;
        this.alarmManager = alarmManager;
        Log.d("myLogs", "00");
        //alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //intentAlarm = new Intent(this, MyReceiver.class);

    }



    //метод когда изменяется список будильников и нужно проверить и если что запланировать будильники на сегодня
    public void saveAlarmTodayToSheduler() {

        //дата и время текущий момент
        Calendar dateTimeNow = Calendar.getInstance();
        dateTimeNow.setTime(new Date(System.currentTimeMillis()));
        //номер дня недели сегодня
        int dayInWeekNow = dateTimeNow.get(Calendar.DAY_OF_WEEK);
        //время в милисекундах в текущий момент
        long dateTimeNowInMilSec = dateTimeNow.getTimeInMillis();
        //перебираем список будильников и находит тут который еще должен сегодня сработать
        for (int i = 0; i < list.size(); i++) {
            //получаем флаг включен будильник или нет
            boolean onOff = list.get(i).getOnOff();
            //получаем список дней недели
            List<Integer> days = list.get(i).getDays();
            //если будильник включен(true), то планируем его
            if(onOff){
                //ищем день недели в списке который совпадает с сегодня
                for (int j = 0; j < days.size(); j++) {
                    //если нашли совпадающий день, то создаем будильник на сегодня
                    if (days.get(j).equals(dayInWeekNow)) {
                        int hour = list.get(i).getCalendar().get(Calendar.HOUR_OF_DAY);
                        int minute = list.get(i).getCalendar().get(Calendar.MINUTE);
                        Calendar alarmToday = Calendar.getInstance();
                        alarmToday.setTime(new Date(System.currentTimeMillis()));
                        alarmToday.set(Calendar.HOUR_OF_DAY, hour);
                        alarmToday.set(Calendar.MINUTE, minute);
                        long dateTimeFutureInMilSec = alarmToday.getTimeInMillis();

                        //если есть запланированные будильники на сегодня, то сейчас мы это сделаем
                        if(dateTimeFutureInMilSec > dateTimeNowInMilSec) {

                            intentAlarm = new Intent(this, MyReceiver.class);

                            intentAlarm.putExtra("startClass","ActivityAlarm");
                            intentAlarm.putExtra("name", list.get(i).getName().toString());
                            intentAlarm.putExtra("time", hour + ":" + minute);
                            intentAlarm.putExtra("rington", list.get(i).getRingtone().toString());

                            //ВОТ ТУТ ОТВАЛИВАЕТСЯ!!!
                            pendingIntent = PendingIntent.getBroadcast(this, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dateTimeFutureInMilSec, pendingIntent);

                        }
                    }
                }
            }
        }
    }

    //метод который преобразует список будильников в планировщик заданий для будильника
    public void saveListAlarmToSheduler(){

        //дата и время текущий момент
        Calendar dateTimeNow = Calendar.getInstance();
        dateTimeNow.setTime(new Date(System.currentTimeMillis()));
        int dayInWeekNow = dateTimeNow.get(Calendar.DAY_OF_WEEK);
        long dateTimeNowInMilSec = dateTimeNow.getTimeInMillis();

        //формируем задание формирования будильников на завтра
//        intentAlarm = new Intent(this, MyReceiver.class);
//        intentAlarm.putExtra("startClass","ActivityAlarm");
//        pendingIntent = PendingIntent.getBroadcast(this, 999, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTommorow.getTimeInMillis(), pendingIntent);

        //перебираем список будильников
        for (int i = 0; i < list.size(); i++) {

            //получаем список дней недели
            List<Integer> days = list.get(i).getDays();

            //ищем день недели в списке который на день больше чем сегодня
            for (int j = 0; j < days.size(); j++) {
                //если нашли, то создаем будильник
                if(days.get(j).equals(dayInWeekNow+1)){

                    int hour = list.get(i).getCalendar().get(Calendar.HOUR_OF_DAY);
                    int minute = list.get(i).getCalendar().get(Calendar.MINUTE);

                    Calendar alarmTommorow = list.get(i).getCalendar();
                    alarmTommorow.add(Calendar.DATE, 1);
                    alarmTommorow.set(Calendar.HOUR_OF_DAY, hour);
                    alarmTommorow.set(Calendar.MINUTE, minute);

                    //intentAlarm = new Intent(this, MyReceiver.class);

                    //intentAlarm.putExtra("startClass","ActivityAlarm");
                    intentAlarm.putExtra("name",list.get(i).getName().toString());
                    intentAlarm.putExtra("time",hour + ":" + minute);
                    intentAlarm.putExtra("rington",list.get(i).getRingtone().toString());

                    pendingIntent = PendingIntent.getBroadcast(this, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTommorow.getTimeInMillis(), pendingIntent);

                }
            }

        }

    }

}
