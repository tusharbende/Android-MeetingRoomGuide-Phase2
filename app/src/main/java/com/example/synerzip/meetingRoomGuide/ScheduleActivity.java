package com.example.synerzip.meetingRoomGuide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by synerzip on 11/8/16.
 */
public class ScheduleActivity extends Activity {

    int i = 1,j =0,k = 0;
    ArrayList<String> list;
    int index,index1,index2,pastIndex;
    ArrayList<String> list1;
    ArrayList<String> timeSlots = new ArrayList<String>(Arrays.asList("10:00 AM","10:30 AM","11:00 AM","11:30 AM","12:00 PM","12:30 PM","1:00 PM","1:30 PM","2:00 PM","2:30 PM"
            ,"3:00 PM","3:30 PM","4:00 PM","4:30 PM","5:00 PM","5:30 PM","6:00 PM","6:30 PM","7:00 PM","7:30 PM","8:00 PM",
            "8:30 PM","9:00 PM","9:30 PM"));
    TableRow tr;
    Object tag;
    Date startDate,endDate;
    TextView room;
    String roomName;
    TextView tv;
    TextView tv1;
    private static final String DATE_TIME_FORMAT = "h:mm a";
    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
    ArrayList<MyClass> MeetingData = new ArrayList<MyClass>();
    TableLayout table;
    Date date = new Date();
   // String currentTime = new SimpleDateFormat(DATE_TIME_FORMAT).format(date);


    public class MyClass {

        String title;
        String startTime;
        String endTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        table = (TableLayout) findViewById(R.id.table);
        room = (TextView) findViewById(R.id.roomName);
        Intent getData = getIntent();
        list = getData.getStringArrayListExtra("message");
        System.out.println("message"+list);
        roomName = getData.getStringExtra("roomName");
        Typeface externalFont = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
        list1 = new ArrayList<String>(24);
        room.setText(roomName);
        //System.out.println("current time string"+currentTime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.add(Calendar.MINUTE, mod < 16 ? -mod : (30-mod));
        System.out.println("calendar"+calendar.getTime());
        String currentTime = new SimpleDateFormat(DATE_TIME_FORMAT).format(calendar.getTime());
        System.out.println("current time string"+currentTime);


        try {
            for (i = 1; i <= 25; i++) {

                if(i > (list.size()/3)){

                    list.add("");
                    list.add("");
                    list.add("");

                }
                MyClass record = new MyClass();

                record.title = list.get(j);
                j++;
                record.startTime = list.get(j);
                j++;
                record.endTime = list.get(j);
                j++;
                MeetingData.add(record);
                list1.add("Available - Click to book");

            }

            System.out.println("MeetingData = " + MeetingData);
            System.out.println("List1 = " + list1.size());
        }
        catch (ArrayIndexOutOfBoundsException e){

            System.out.println("ERROR"+e);
        }

        TableLayout.LayoutParams tableLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        tableLayout.setMargins(0,2,0,2);
        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        rowLayout.setMargins(1,1,2,1);

        for (i = 0; i < timeSlots.size(); i++) {

            try {
                tr = new TableRow(this);

                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tr.setLayoutParams(tableLayout);
                tr.setMinimumHeight(150);
                tr.setId(i);
                table.addView(tr);

                tv = new TextView(this);
                tv.setText(timeSlots.get(i));
                tv.setBackgroundColor(Color.parseColor("#b2b2b2"));
                tv.setMinWidth(400);
                tv.setMinimumHeight(150);
                tv.setLayoutParams(rowLayout);
                tv.setTypeface(externalFont);
                tv.setTextSize(20);
                tv.setGravity(Gravity.CENTER);
                tr.addView(tv);

                tv1 = new TextView(this);
                tv1.setLayoutParams(rowLayout);
                tv1.setMinWidth(2000);
                tv1.setTypeface(externalFont);
                tv1.setTextSize(20);
                tv1.setBackgroundColor(Color.parseColor("#80ff80"));
                tv1.setGravity(Gravity.CENTER);
                if(timeSlots.get(i).equals(currentTime)){


                    pastIndex = i ;
                }

                if(!MeetingData.get(i).startTime.equals("")){

                    for(j = 0; j < timeSlots.size(); j++){

                        if(timeSlots.get(j).equals(MeetingData.get(i).startTime)) {

                            list1.set(j, MeetingData.get(i).title);
                            index = j;
                            index2 = i;

                        }
                        if(timeSlots.get(j).equals(MeetingData.get(i).endTime)){

                            index1 = j;
                            for(k = index +1; k < index1; k++){

                                list1.set(index + 1,MeetingData.get(index2).title);
                            }
                        }
                    }
                }
                if(!list1.get(i).equals("Available - Click to book")){

                    tv1.setBackgroundColor(Color.parseColor("#b30000"));
                    tv.setBackgroundColor(Color.parseColor("#b30000"));
                }
                else {

                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            index = v.getId();
                            System.out.println("***"+timeSlots.get(index));
                            Intent goToBooking = new Intent(getBaseContext(),BookingActivity.class);
                            goToBooking.putExtra("timeSlot",timeSlots.get(index));
                            goToBooking.putExtra("roomName",roomName);
                            startActivity(goToBooking);
                        }
                    });
                }

                tv1.setText(list1.get(i));
                tr.addView(tv1);


            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Error"+e);

            }
        }


        for(i = 0; i < pastIndex; i++){

            table.removeViewAt(0);


        }



    }
}
