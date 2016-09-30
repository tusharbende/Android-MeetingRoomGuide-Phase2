package com.example.synerzip.meetingRoomGuide;

import android.app.Activity;
import android.app.ListActivity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity{
    boolean currentEventFoundFlag = false;
    String previousTitle = "";
    String startTimeForReporting = "";
    String endTimeForReporting = "";
    String titleForReporting = "";
    String mailBodyText = "";
    String meetingRoomName = "";
    String organizer = "";
    Button button;
    int i,j,p;
    private static String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> buttonText,emp_names,emp1;
    String displayName,buttonName;
    String meetingroomID;
    String beginTime,endTime,URL,first_name,last_name;
    ArrayList<String> data;

    public class CalendarList {
        String calendarName;
        String title;
        Date start;
        Date end;
        String organizer;
    }

    View mainRelativeLayout;
    //    CalendarListAdapter calendarListAdapter;
    List<CalendarList> calendarEventList;
    List<CalendarList> calendarData;
    private static final String DATE_TIME_FORMAT = "h:mm a";
    Drawable newDrawable;
    Map<String, String> calendarResources = new HashMap<String, String>();
    Map<String, String> meetingRoomNames = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /********** Display all calendar names in drop down START ****************/

        String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };
        buttonText = new ArrayList<>();
        final ArrayList<String> calendarNames = new ArrayList<String>();
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursorThirdFloor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                EVENT_PROJECTION, "calendar_displayName LIKE '%3F%'", null, "name ASC");
        Cursor cursorFourthFloor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                EVENT_PROJECTION, "calendar_displayName LIKE '%4F%'", null, "name ASC");

        if (cursorThirdFloor != null) {
            while (cursorThirdFloor.moveToNext()) {
                String displayNameOld = cursorThirdFloor.getString(2);
                displayName = displayNameOld.replace("3F", "");// Needs to update this logic for some other pattern
                buttonName = displayName;
                String[] separate = buttonName.split("-");
                buttonText.add("3F - "+separate[0]);
                displayName = "3F -" + displayName;

                meetingRoomNames.put(displayName, displayNameOld);
                String id = cursorThirdFloor.getString(0);
                String ACCOUNT_NAME = cursorThirdFloor.getString(1);
                String OWNER_ACCOUNT = cursorThirdFloor.getString(3);

                calendarResources.put(displayName, OWNER_ACCOUNT);
                System.out.println("calendar name = " + displayName + " id = " + id + " ACCOUNT_NAME = " + ACCOUNT_NAME + " OWNER_ACCOUNT = " + OWNER_ACCOUNT);
                calendarNames.add(displayName);

            }
            cursorThirdFloor.close();

            // To sort with case sensitive uncomment below
//            Collections.sort(calendarNames, CALENDAR_NAME_ORDER);
        }
            System.out.println("buttontext  = "+buttonText);

        if (cursorFourthFloor != null) {
            while (cursorFourthFloor.moveToNext()) {
                String displayNameOld = cursorFourthFloor.getString(2);
                displayName = displayNameOld.replace("4F", "");// Needs to update this logic for some other pattern
                displayName = "4F -" + displayName;
                meetingRoomNames.put(displayName, displayNameOld);
                String id = cursorFourthFloor.getString(0);
                String ACCOUNT_NAME = cursorFourthFloor.getString(1);
                String OWNER_ACCOUNT = cursorFourthFloor.getString(3);

                calendarResources.put(displayName, OWNER_ACCOUNT);

                System.out.println("calendar name = " + displayName + " id = " + id + " ACCOUNT_NAME = " + ACCOUNT_NAME + " OWNER_ACCOUNT = " + OWNER_ACCOUNT);

            }
            cursorFourthFloor.close();


            // To sort with case sensitive uncomment below
//            Collections.sort(calendarNames, CALENDAR_NAME_ORDER);
        }

        System.out.println("****************************"+displayName);
        // drop down adapter
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, calendarNames) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(20);

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(20);

                return v;
            }
        };

        ///////
        int length = buttonText.size();
        int count = 0;
        TableLayout table = (TableLayout)findViewById(R.id.rooms);
        // for modifying data of Buttons runtime ***
        for( i = 0; i < table.getChildCount() && length != 0; i++) {
            View row = table.getChildAt(i);
            if (row instanceof TableRow) {
                // then, you can remove the the row you want...
                // for instance...
                final TableRow tableRow = (TableRow) row;
                for( j = 0; j < tableRow.getChildCount() && length != 0; j++) {
                    final View view1 = tableRow.getChildAt(j);
                    button = (Button) view1;
                    button.setTextSize(20);

                    // Set original Meeting Room Name in Button text //Commenting for time being
                    // button.setText(calendarNames.get(count));


                    final String roomName = calendarNames.get(count);
                    System.out.println("roomname"+roomName);

                    boolean isProjectorAvailable = false;

//                  String shortMeetingRoomName = getShortMeetingRoomName(meetingRoomName);
                    if (isProjectorAvailable(roomName)) {
                        isProjectorAvailable = true;
                    }

//                  String shortMeetingRoomName = getShortMeetingRoomName(roomName);

                    String size = giveMeetingRoomSize(roomName);
                    String name = buttonText.get(count);
                    button.setText(name + "\n");

                    if (!size.isEmpty())
                        button.setText(button.getText() + "(" + size + ")");

                    if (isProjectorAvailable)
                        button.setText(button.getText() + "      (Projector)");

                    calendarEventList = getDataForListView(MainActivity.this, meetingRoomNames.get(calendarNames.get(count)).toString());
                    boolean isongoing = checkOngoingMeeting(calendarEventList);
                    System.out.println("calendarData=   "+calendarData);

//                  Button button = (Button) findViewById(R.id.room1);
                    if(isongoing)
                        button.setBackgroundColor(Color.parseColor("#b30000"));
                    else
                        button.setBackgroundColor(Color.parseColor("#80ff80"));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedRoomName = roomName;
                            meetingRoomName = selectedRoomName;
                            meetingroomID = calendarResources.get(meetingRoomName.toString());
                            System.out.println("Meetingroomname = "+meetingRoomName);
                            calendarEventList = readCalendar(MainActivity.this,meetingRoomNames.get(meetingRoomName));
                            //  Toast.makeText(getBaseContext(),"****"+meetingRoomName,Toast.LENGTH_SHORT).show();
                            System.out.println("calendarEventList = "+calendarEventList);
                            data = new ArrayList<>();
                            for (i = 0; i < calendarData.size(); i++){


                                beginTime = new SimpleDateFormat(DATE_TIME_FORMAT).format(calendarData.get(i).start);
                                System.out.println("begintime = "+beginTime);
                                endTime = new SimpleDateFormat(DATE_TIME_FORMAT).format(calendarData.get(i).end);
                                System.out.println("endtime = "+endTime);
                                data.add(calendarData.get(i).title);
                                data.add(beginTime);
                                data.add(endTime);

                            }

                        //    System.out.println("*******"+data);
                            Intent schedule = new Intent(getBaseContext(), ScheduleActivity.class);
                            schedule.putExtra("message",data);
                            schedule.putExtra("roomName",meetingRoomName);
                            schedule.putExtra(Intent.EXTRA_EMAIL,meetingroomID);
                            startActivity(schedule);

                        }
                    });

                    length--;
                    count++;
                }

            }
        }

// Approch 1: This Is how we can diactivate remaining buttons on screen (deactivate= do not show on screen.)
        int numberOfColumns = 4, numberOfRows =3;
        int r = count / numberOfColumns;
        int c = count % numberOfColumns;

        for (int h = r; h <= numberOfRows; h++) {
//        // This is How we remove the table row
            View rowFromWhichCellNeedsToBeDeleted = table.getChildAt(h);
//		table.removeView(row);

            TableRow tableRowFromWhichCellNeedsToBeDeleted = (TableRow) rowFromWhichCellNeedsToBeDeleted;
            for (int i = c; i < numberOfColumns; i++) {
                if(tableRowFromWhichCellNeedsToBeDeleted!=null) {
                    View view1 = tableRowFromWhichCellNeedsToBeDeleted.getChildAt(c);
                    tableRowFromWhichCellNeedsToBeDeleted.removeView(view1);
                }
            }
        }

    }

    public boolean isProjectorAvailable(String meetingRoomName) {
        if (meetingRoomName.toLowerCase().contains("projector")) {
            return true;
        } else {
            return false;
        }
    }
    public String giveMeetingRoomSize(String meetingRoomName) {

        int index = meetingRoomName.toLowerCase().indexOf("(");
        int indexE = meetingRoomName.toLowerCase().indexOf(" seats");
        if (indexE == -1 || index == -1) {
            return "";
        }
        String size = meetingRoomName.toLowerCase().substring(index + 1, indexE);
        return size;
    }

    // Newly Added Start: Newly added Function for highlighting meeting rooms Red/Green

    public boolean checkOngoingMeeting(List<CalendarList> calendarEventList) {


        if (calendarEventList.size() == 0)
        {
            return false;
        }
        CalendarList chapter = calendarEventList.get(0);


        Calendar calInstanceCurrent = Calendar.getInstance(); // creates calendar
        calInstanceCurrent.setTime(new Date(new Date().getTime())); // sets calendar time/date

        Calendar calInstanceMeetingEventStart = Calendar.getInstance();
        calInstanceMeetingEventStart.setTime(new Date(chapter.start.getTime()));

        Calendar calInstanceMeetingEventEnd = Calendar.getInstance();
        calInstanceMeetingEventEnd.setTime(new Date(chapter.end.getTime()));

        if (!DateUtils.isToday(calInstanceMeetingEventStart.getTimeInMillis())) {
            calInstanceMeetingEventStart.set(calInstanceCurrent.get(Calendar.YEAR), calInstanceCurrent.get(Calendar.MONTH), calInstanceCurrent.get(Calendar.DAY_OF_MONTH));
            calInstanceMeetingEventEnd.set(calInstanceCurrent.get(Calendar.YEAR), calInstanceCurrent.get(Calendar.MONTH), calInstanceCurrent.get(Calendar.DAY_OF_MONTH));
        }
        long meetingStartTimeConvertedToTodayInMillis = calInstanceMeetingEventStart.getTimeInMillis();
        long meetingEndTimeConvertedToTodayInMillis = calInstanceMeetingEventEnd.getTimeInMillis();

        // Set current meeting attributes (bg colour etc).
        // As It is a Current Ongoing meeting Enable the Complaint Button.
        if (meetingStartTimeConvertedToTodayInMillis < calInstanceCurrent.getTimeInMillis() && meetingEndTimeConvertedToTodayInMillis > calInstanceCurrent.getTimeInMillis()) {

            return true;

        }

        long compare = Integer.valueOf(calInstanceMeetingEventStart.get(Calendar.HOUR_OF_DAY)).compareTo(calInstanceCurrent.get(Calendar.HOUR_OF_DAY));

        if (compare == 0)//Means Both meeting time in hours is same hence compare minutes
        {
            compare = Integer.valueOf(calInstanceMeetingEventStart.get(Calendar.MINUTE)).compareTo(calInstanceCurrent.get(Calendar.MINUTE));
        }

        if ((compare == 1 && currentEventFoundFlag == false) || (currentEventFoundFlag == true && previousTitle == chapter.title))// no meeting event is highlighted
        {

        }

        return false;
    }

    // Newly Added end: Newly added Function for highlighting meeting rooms Red/Green

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view_with_simple_adapter, menu);
        return true;
    }
*/
    public List<CalendarList> getDataForListView(MainActivity context, String calendarName) {
        List calendarList = readCalendar(context, calendarName);
        return calendarList;
    }

    public List readCalendar(Context context, String calendarName) {
        organizer = "";
        ContentResolver contentResolver = context.getContentResolver();
        calendarData = new ArrayList<CalendarList>();
        Cursor eventCursor = null;
        Cursor cursorThirdFloor = null;
        // while loading every calender we need to set Complaint button as Disabled as initial state.
//        ImageButton sendBtn = (ImageButton) findViewById(R.id.sendEmail);
//        sendBtn.setVisibility(View.GONE);

        try {
            // Fetch all events of selected calendar
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            long now = new Date().getTime();


            Calendar calInstanceE = Calendar.getInstance(); // creates calendar
            calInstanceE.setTime(new Date(now));
            long currentHourOfDay = calInstanceE.get(Calendar.HOUR_OF_DAY);

            ContentUris.appendId(builder, now);
            ContentUris.appendId(builder, (now + (24 - currentHourOfDay) * DateUtils.HOUR_IN_MILLIS));

            final String[] projection = new String[]
                    {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART,
                            CalendarContract.Events.DTEND, CalendarContract.Events.ACCOUNT_NAME,
                            CalendarContract.Events.DURATION, CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ORGANIZER,
                            CalendarContract.Events.SELF_ATTENDEE_STATUS};

            eventCursor = contentResolver.query(
                    builder.build(), projection, CalendarContract.Instances.CALENDAR_DISPLAY_NAME + " = ?",
                    new String[]{"" + calendarName}, "DTSTART ASC");

            eventCursor.moveToFirst();
            do {
                CalendarList record = new CalendarList();

                String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                Date end = new Date(eventCursor.getLong(2));
                final String accountName = eventCursor.getString(3);
                final String duration = eventCursor.getString(4);
                final Long accessLevel = eventCursor.getLong(5);

                final String selfAttendeeStatus = eventCursor.getString(7);

                // Check to hide declined events by Meeting room
                if (selfAttendeeStatus.equals("2")) {
                    continue;
                }

//                if(organizer.isEmpty())
//                {
                organizer = eventCursor.getString(6);
//                }

                if (duration != null) {
                    // Calculation Logic for Meeting end time
                    int durationInSeconds = Integer.parseInt(duration.substring(1, duration.length() - 1));
                    Calendar calInstance = Calendar.getInstance(); // creates calendar
                    calInstance.setTime(new Date(eventCursor.getLong(1))); // sets calendar time/date
                    calInstance.add(Calendar.SECOND, durationInSeconds); // Add Seconds from Duration
                    end = calInstance.getTime();
                }

                if (accessLevel == 2) //Means private Meeting
                {
                    title = "Busy";
                }

                if (title.isEmpty()) // Untitled event
                {
                    title = "Untitled event";
                }

                System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
                        " accountName: " + accountName + " accessLevel: " + accessLevel);

                record.title = title;
                record.start = begin;
                record.end = end;
                record.calendarName = accountName;
                record.organizer = organizer;

                calendarData.add(record);
            } while (eventCursor.moveToNext());

            Collections.sort(calendarData, SENIORITY_ORDER);

        } catch (Exception ex) {

        } finally {
            try {
                if (eventCursor != null && !eventCursor.isClosed()) {
                    eventCursor.close();
                }

                if (cursorThirdFloor != null && !cursorThirdFloor.isClosed()) {
                    cursorThirdFloor.close();
                }

            } catch (Exception ex) {
            }
        }

        return calendarData;
    }

    static final Comparator<CalendarList> SENIORITY_ORDER = new Comparator<CalendarList>() {
        public int compare(CalendarList e1, CalendarList e2) {
            int compare = e1.start.compareTo(e2.start);
            if (compare == 0) {//Means both events start time is different
                return compare;
            } else {
                Calendar calInstanceE1 = Calendar.getInstance(); // creates calendar
                calInstanceE1.setTime(new Date(e1.start.getTime())); // sets calendar time/date

                Calendar calInstanceE2 = Calendar.getInstance(); // creates calendar
                calInstanceE2.setTime(new Date(e2.start.getTime())); // sets calendar time/date

                compare = Integer.valueOf(calInstanceE1.get(Calendar.HOUR_OF_DAY)).compareTo(calInstanceE2.get(Calendar.HOUR_OF_DAY));
                if (compare == 0)//Means Both meeting time in hours is same hence compare minutes
                {
                    compare = Integer.valueOf(calInstanceE1.get(Calendar.MINUTE)).compareTo(calInstanceE2.get(Calendar.MINUTE));
                }
            }

            return compare;
        }
    };

    // sort calendar names alphabetically
    static final Comparator<String> CALENDAR_NAME_ORDER = new Comparator<String>() {
        public int compare(String e1, String e2) {
            return e1.compareToIgnoreCase(e2);
        }
    };

}


