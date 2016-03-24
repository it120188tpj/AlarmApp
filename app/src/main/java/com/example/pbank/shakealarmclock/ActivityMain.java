package com.example.pbank.shakealarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ActivityMain extends Activity implements OnClickListener {

    private Button btnAddItem;
    private ListView listViewItem;
    private ArrayList<ListItem> arrayListItem;
    private SharedPreferences sharedPreferences;

    private MyAdapterListItem customAdapter;
    private static final String TAG = "MyLogs";
    final String SAVED_LIST = "saved_list";

    private AlarmManager alarmManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayListItem = new ArrayList<ListItem>();

        btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(this);

        listViewItem = (ListView) findViewById(R.id.listViewItem);
        customAdapter = new MyAdapterListItem(this,arrayListItem);

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

        saveAlarmClockList();

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
                saveAlarmClockList();
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
        saveAlarmClockList();
        finish();
    }



    //метод который сохраняет список будильников в память
    private void saveAlarmClockList(){

        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.clear();

        for (int i = 0; i < arrayListItem.size(); i++) {
            ed.putString(SAVED_LIST+i, arrayListItem.get(i).toJSON());
        }

        ed.putInt("list_size", arrayListItem.size());
        ed.commit();

        //запускаем класс и методы чтобы запланировать все будильники на сегодня
        SchedulerAlarmClock schedulerAlarmClock = new SchedulerAlarmClock(arrayListItem,alarmManager);
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

                    //
                    saveAlarmClockList();
                }

            }
        }else if (resultCode == 0) {
            Toast.makeText(getApplicationContext(),"Системная ошибка",Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                ListItem listItem = null;
                if (b != null) {
                    listItem = (ListItem) b.getSerializable("item");

                    arrayListItem.set(listItem.getIndex(),listItem);

                    //говорим нашему адаптеру ОБНОВИСЬ!
                    customAdapter.notifyDataSetChanged();

                    saveAlarmClockList();
                }

            }
        }
    }


}

