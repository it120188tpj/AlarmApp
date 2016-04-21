package com.example.pbank.shakealarmclock;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by pbank on 10.12.14.
 */
public class ListItem implements Serializable{

    private static final long serialVersionUID = 1L;
    private boolean onOff;
    private String name;
    private Calendar calendar;
    private List<Integer> days;
    private boolean vibroOnOff;
    private int index = 99;
    private String rington;
    private Uri uriRington;


    public ListItem(){
    }


    public ListItem(String name, Calendar calendar, List<Integer> days, boolean vibroOnOff, String rington, Uri uriRington){
        this.name = name;
        this.calendar = calendar;
        this.days = days;
        this.vibroOnOff = vibroOnOff;
        this.rington = rington;
        this.onOff = true;
        //this.uriRington = uriRington;
    }

    public String getName() {
        return name;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public List<Integer> getDays() {
        return days;
    }

    public boolean getVibroOnOff(){
        return vibroOnOff;
    }

//    public void setCalendar(Calendar calendar) {
//        this.calendar = calendar;
//    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setOnOff(boolean onOff){
        this.onOff = onOff;
    }

    public boolean getOnOff(){
        return this.onOff;
    }

    public int getIndex() {
        return index;
    }

    public String getRingtone(){
        return rington;
    }

    public Uri getUriRington() {
        return uriRington;
    }

    public void setUriRington(Uri uriRington) {
        this.uriRington = uriRington;
    }

    public String toJSON (){

        JSONObject jsonObject = new JSONObject();

        try {
            //записываем имя будильника
            jsonObject.put("name", getName());

            //записываем дату
            JSONObject jsonCalendarObject = new JSONObject();
            jsonCalendarObject.put("year",getCalendar().get(Calendar.YEAR));
            jsonCalendarObject.put("month",getCalendar().get(Calendar.MONTH));
            jsonCalendarObject.put("day",getCalendar().get(Calendar.DATE));
            jsonCalendarObject.put("hour",getCalendar().get(Calendar.HOUR_OF_DAY));
            jsonCalendarObject.put("minute",getCalendar().get(Calendar.MINUTE));

            jsonObject.put("calendar", jsonCalendarObject);

            //перебираем коллекцию с днями недель и записываем ее в JSON array
            JSONArray jsonArrDays = new JSONArray();

            for (int i = 0; i < days.size(); i++) {
                JSONObject pnObj = new JSONObject();
                pnObj.put("days", days.get(i));
                jsonArrDays.put(pnObj);
            }

            jsonObject.put("arrayDays",jsonArrDays);

            //записываем boolean флаг вибро
            jsonObject.put("vibro",getVibroOnOff());

            //записываем рингтон
            jsonObject.put("rington",getRingtone());

            //зaписываем объект
            jsonObject.put("uriRington",getUriRington());

            //записываем ВКЛ/ВЫКЛ
            jsonObject.put("onOff",getOnOff());

            Log.d("myLogs",jsonObject.toString());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public void fromJSON(String data) throws JSONException {

        JSONObject jObj = new JSONObject(data);
        Log.d("myLogs",data);

        //сэтим имя будильника
        this.name = jObj.getString("name");

        //сэтим дату и время
        JSONObject subObj = jObj.getJSONObject("calendar");
        int year = Integer.valueOf(subObj.getString("year"));
        int month = Integer.valueOf(subObj.getString("month"));
        int day = Integer.valueOf(subObj.getString("day"));
        int hour = Integer.valueOf(subObj.getString("hour"));
        int minute = Integer.valueOf(subObj.getString("minute"));

        this.calendar = Calendar.getInstance();
        this.calendar.set(Calendar.YEAR,year);
        this.calendar.set(Calendar.MONTH,month);
        this.calendar.set(Calendar.DATE,day);
        this.calendar.set(Calendar.HOUR_OF_DAY,hour);
        this.calendar.set(Calendar.MINUTE,minute);

        //сэтим дни недели
        this.days = new ArrayList<Integer>();
        JSONArray jArrDays = jObj.getJSONArray("arrayDays");
        for (int i=0; i < jArrDays.length(); i++) {
            JSONObject obj = jArrDays.getJSONObject(i);
            this.days.add(obj.getInt("days"));
        }

        //сэтим вибро вкл или выкл
        this.vibroOnOff = jObj.getBoolean("vibro");

        //сэтим рингтон
        this.rington = jObj.getString("rington");

        //сэтим объект uri рингтон
        this.uriRington = (Uri) jObj.get("uriRington");

        //сэтим ВКЛ/ВЫКЛ
        this.onOff = jObj.getBoolean("onOff");
    }



}
