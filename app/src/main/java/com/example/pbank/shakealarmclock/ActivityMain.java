package com.example.pbank.shakealarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.*;


public class ActivityMain extends Activity implements OnClickListener {

    private static final String TAG = "MyLogs";
    private static final String SAVED_LIST = "saved_list";
    private Button btnAddItem;
    private ListView listViewItem;
    private ArrayList<ListItem> arrayListItem;
    private SharedPreferences sharedPreferences;
    private MyAdapterListItem customAdapter;
    private AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayListItem = new ArrayList<>();

        btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);

        listViewItem = (ListView) findViewById(R.id.listViewItem);
        customAdapter = new MyAdapterListItem(this, arrayListItem);

        listViewItem.setAdapter(customAdapter);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        try {
            loadAlarmClockList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Вызывается перед выходом из "полноценного" состояния.
    @Override
    public void onDestroy(){
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д.
        super.onDestroy();

        saveAlarmToSheduler();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.action_exit:
                saveAlarmToSheduler();
                finish();
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //метод для нажатия на кнопку Back
    @Override
    public void onBackPressed() {
        saveAlarmToSheduler();
        finish();
    }

    private void saveAlarmToSheduler() {

        // стягиваем данные о листе будильник с Preferences в памяти
        sharedPreferences = getPreferences(MODE_PRIVATE);

        SchedulerAlarmClock schedulerAlarmClock = new SchedulerAlarmClock();
        schedulerAlarmClock.setContext(this.getApplicationContext());
        schedulerAlarmClock.setAlarmManager(alarmManager);
        schedulerAlarmClock.setArrayListItem(arrayListItem);
        schedulerAlarmClock.setSharedPreferences(sharedPreferences);

        schedulerAlarmClock.saveAlarmTodayToSheduler();

    }

    //метод который восстанавливает список будильников в приложение
    private void loadAlarmClockList() throws JSONException {

        sharedPreferences = getPreferences(MODE_PRIVATE);
        if(sharedPreferences.contains("list_size")) {
            int list_size = sharedPreferences.getInt("list_size", 0);

            for (int i = 0; i < list_size; i++) {
                String savedText = sharedPreferences.getString(SAVED_LIST + i, "");
                ListItem listItem = new ListItem();
                listItem.fromJSON(savedText);
                arrayListItem.add(listItem);
            }

            customAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //кнопка ПЛЮС нажата
            case R.id.btnAddItem:
                Intent intent = new Intent(this, ActivityAddAlarmClock.class);
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                ListItem listItem = null;
                if (b != null) {
                    listItem = (ListItem) b.getSerializable("item");

                    //записываем новый пункт меню будильник в общий СПИСОК
                    arrayListItem.add(listItem);

                    //говорим нашему адаптеру ОБНОВИСЬ!
                    customAdapter.notifyDataSetChanged();

                    saveAlarmToSheduler();
                }

            }
        } else if (resultCode == 0) {
            Toast.makeText(getApplicationContext(),"Системная ошибка",Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                ListItem listItem = null;
                if (b != null) {
                    listItem = (ListItem) b.getSerializable("item");

                    arrayListItem.set(listItem.getIndex(), listItem);

                    //говорим нашему адаптеру ОБНОВИСЬ!
                    customAdapter.notifyDataSetChanged();

                    saveAlarmToSheduler();
                }

            }
        }
    }

    public void saveAlarmTodayToSheduler() {

        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.clear();

        for (int i = 0; i < arrayListItem.size(); i++) {
            ed.putString(SAVED_LIST+i, arrayListItem.get(i).toJSON());
            ed.putString(SAVED_LIST+i, arrayListItem.get(i).toJSON());
        }

        ed.putInt("list_size", arrayListItem.size());
        ed.commit();

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

                            Intent intentAlarm = new Intent(ActivityMain.this, MyReceiver.class);

                            intentAlarm.putExtra("startClass","ActivityAlarm");
                            intentAlarm.putExtra("name", arrayListItem.get(i).getName().toString());
                            intentAlarm.putExtra("time", hour + ":" + minute);
                            intentAlarm.putExtra("nameRington", arrayListItem.get(i).getRingtone().toString());
                            intentAlarm.putExtra("uriRington", arrayListItem.get(i).getUriRington());

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeFutureInMilSec, pendingIntent);

                        }
                    }
                }
            }
        }
    }


}

