package com.example.pbank.shakealarmclock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import butterknife.Bind;
import butterknife.ButterKnife;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ActivityAddAlarmClock extends Activity implements OnClickListener{

    private int DIALOG_TIME = 1;
    private int myHour,myMinute;

    @Bind(R.id.tv_timeView)
    TextView tvTime;

    @Bind(R.id.btn_setTimeView)
    Button btn_setTime;

    @Bind(R.id.btn_saveItem)
    Button btn_saveItem;

    @Bind(R.id.btn_cancelItem)
    Button btn_cancelItem;

    private EditText et_nameViewItem;
    private Calendar calendarTimePicker;
    private Calendar calendarItem;
    private ListItem itemNew;
    private ListItem itemEdit;
    private ToggleButton btn_monday,btn_tuesday,btn_wednesday,btn_thursday,btn_friday,btn_saturday,btn_sunday;
    private List<Integer> days;
    private CheckBox checkbox_vibro;
    private LinearLayout ll_media;
    private TextView tv_media_name_track;
    private String ringtone;
    private Uri ringtoneUri;


    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm_clock);
        ButterKnife.bind(this);

        //получаем интент с данными для редактирования с основого активити
        Intent intent = getIntent();
        itemEdit = (ListItem) intent.getSerializableExtra("itemEdit");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        btn_setTime.setOnClickListener(this);
        btn_saveItem.setOnClickListener(this);
        btn_cancelItem.setOnClickListener(this);

        et_nameViewItem = (EditText) findViewById(R.id.et_nameViewItem);

        calendarItem = Calendar.getInstance();

        btn_monday = (ToggleButton) findViewById(R.id.btn_monday);
        btn_tuesday = (ToggleButton) findViewById(R.id.btn_tuesday);
        btn_wednesday = (ToggleButton) findViewById(R.id.btn_wednesday);
        btn_thursday = (ToggleButton) findViewById(R.id.btn_thursday);
        btn_friday = (ToggleButton) findViewById(R.id.btn_friday);
        btn_saturday = (ToggleButton) findViewById(R.id.btn_saturday);
        btn_sunday = (ToggleButton) findViewById(R.id.btn_sunday);

        checkbox_vibro = (CheckBox) findViewById(R.id.checkbox_vibro);

        tv_media_name_track = (TextView) findViewById(R.id.tv_media_name_track);
        ll_media = (LinearLayout) findViewById(R.id.ll_media);
        ll_media.setOnClickListener(this);

        //если наш интент с данными не НУЛ то сэтим данные с него, иначе пустой активити
        if(itemEdit != null) {
            et_nameViewItem.setText(itemEdit.getName());

            //отбираем ВРЕМЯ пункта
            int hh_itemView = itemEdit.getCalendar().get(Calendar.HOUR_OF_DAY);
            int mm_itemView = itemEdit.getCalendar().get(Calendar.MINUTE);
            String hh_itemView_s = (hh_itemView < 10 ? "0" + hh_itemView : String.valueOf(hh_itemView));
            String mm_itemView_s = (mm_itemView < 10 ? "0" + mm_itemView : String.valueOf(mm_itemView));

            //сэтим ВРЕМЯ пункта
            tvTime.setText(hh_itemView_s + ":" + mm_itemView_s);

            //сэтим КНОПКИ дней недели
            setListItemToToggleBtn((ArrayList<Integer>) itemEdit.getDays());

            //сэтим ФЛАГ вибро
            checkbox_vibro.setChecked(itemEdit.getVibroOnOff());

            //сэтим название рингтона
            if(itemEdit.getRingtone().equals("")){
                tv_media_name_track.setText("");
                ringtone = "";
            }else if (itemEdit.getRingtone().equals("content://settings/system/ringtone")) {
                tv_media_name_track.setText("Мелодия по умолчанию");
                ringtone = itemEdit.getRingtone();
            }else{
                Uri rington_uri = Uri.parse(itemEdit.getRingtone());
                tv_media_name_track.setText(getRingtonNameFromURI(rington_uri));
                ringtone = itemEdit.getRingtone();
            }
        }else{
            tvTime.setText(sdf.format(new Date(System.currentTimeMillis())));
            ringtone = "";
        }

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btn_setTimeView:
                showDialog(DIALOG_TIME);
                break;
            case R.id.btn_saveItem:

                days = new ArrayList<>();
                setToggleBtnToList();
                String name = et_nameViewItem.getText().toString();
                boolean checkBoxChecked = checkbox_vibro.isChecked();
                Intent intent = new Intent();

                itemNew = new ListItem(name,
                                       calendarItem,
                                       days,
                                       checkBoxChecked,
                                       ringtone,
                                       ringtoneUri);
                if(itemEdit != null) {
                    itemNew.setIndex(itemEdit.getIndex());
                }
                intent.putExtra("item", itemNew);
                setResult(RESULT_OK, intent);

                finish();
                break;
            case R.id.btn_cancelItem:
                finish();
                break;
            case R.id.ll_media:

                Uri urie = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                Intent intentMedia = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intentMedia.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intentMedia.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выбери рингтон");
                intentMedia.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);

                startActivityForResult(intentMedia, 1);

                break;
            default:
                break;
        }
    }

    public void setToggleBtnToList() {

        if(btn_monday.isChecked() == true) {
            days.add(2);
        }

        if(btn_tuesday.isChecked() == true) {
            days.add(3);
        }

        if(btn_wednesday.isChecked() == true) {
            days.add(4);
        }

        if(btn_thursday.isChecked() == true) {
            days.add(5);
        }

        if(btn_friday.isChecked() == true) {
            days.add(6);
        }

        if(btn_saturday.isChecked() == true) {
            days.add(7);
        }

        if(btn_sunday.isChecked() == true) {
            days.add(1);
        }
    }

    public void setListItemToToggleBtn(ArrayList<Integer> array) {

        for (int i = 0; i < array.size(); i++) {
            if(array.get(i).equals(2)) {
                btn_monday.setChecked(true);
            }

            if(array.get(i).equals(3)) {
                btn_tuesday.setChecked(true);
            }

            if(array.get(i).equals(4)) {
                btn_wednesday.setChecked(true);
            }

            if(array.get(i).equals(5)) {
                btn_thursday.setChecked(true);
            }

            if(array.get(i).equals(6)) {
                btn_friday.setChecked(true);
            }

            if(array.get(i).equals(7)) {
                btn_saturday.setChecked(true);
            }

            if(array.get(i).equals(1)) {
                btn_sunday.setChecked(true);
            }
        }

    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            calendarTimePicker = Calendar.getInstance();
            myHour = calendarTimePicker.get(Calendar.HOUR);
            myMinute = calendarTimePicker.get(Calendar.MINUTE);
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    OnTimeSetListener myCallBack = new OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myHour = hourOfDay;
            myMinute = minute;

            String hh;
            String mm;
            if(myHour<10){
                hh = "0" + myHour;
            }else{
                hh = String.valueOf(myHour);
            }

            if(myMinute<10){
                mm = "0" + myMinute;
            }else{
                mm = String.valueOf(myMinute);
            }

            tvTime.setText(hh + ":" + mm);

            calendarItem = Calendar.getInstance();
            calendarItem.set(Calendar.HOUR_OF_DAY,myHour);
            calendarItem.set(Calendar.MINUTE,myMinute);

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if(ringtoneUri != null) {
                        ringtone = ringtoneUri.toString();
                        if (ringtone.equals("content://settings/system/ringtone")) {
                            tv_media_name_track.setText("Мелодия по умолчанию");
                        }else {
                            tv_media_name_track.setText(getRingtonNameFromURI(ringtoneUri));
                        }
                    } else {
                        tv_media_name_track.setText("");
                    }
                    break;

                default:
                    break;
            }
        }
    }


    //метод который возращает название рингтона
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private String getRingtonNameFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        int indexResultValue = 0;

        for (int i = 0; i < result.length(); i++) {
            String str = result.substring(i, i + 1);
            if (str.equals("/")) {
                indexResultValue = i;
            }
        }

        String resultFinal = result.substring(indexResultValue + 1);

        return resultFinal;

    }

}
