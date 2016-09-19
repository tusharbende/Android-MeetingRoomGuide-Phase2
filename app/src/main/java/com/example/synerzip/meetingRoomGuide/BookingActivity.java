package com.example.synerzip.meetingRoomGuide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by synerzip on 11/8/16.
 */
public class BookingActivity extends Activity {

    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR};
    Button button;
    String accountName = "developer@synerzip.com";
    private int REQUEST_AUTHORIZATION = 11;
    SimpleDateFormat format = new SimpleDateFormat("h:mm a");
    Button cancel,book;
    String timeSlot;
    Date selectedTime;
    AutoCompleteTextView empNames;
    TextView QuickBook;
    String roomName;
    RadioGroup durationGroup;
    RadioButton duration;


    String empNamesList[]= { "Tushar Bende","Vishakha Korade","Sachin Ghare","Nikhil Waykole","Sushil Shinde","Sujith Sudhakaran","Sneha Jagdale ",
            "Himanshu Phirke","Zubair Pathan","Tanvi Shah","Medha Gokhale","Kiran Bodakhe","Nagmani Prasad ","Avnish Kumar","Sandip Nirmal ","Shaila Pawar ",
            "Abhishek Bhattacharyya","Atul Moglewar","Sidharam Teli","Fameeda Tamboli","Dheeraj Koshti","Amit Joshi","Prasanna Barate","Amol Wagh",
            "Yogesh Mandhare","Kunjan Thakkar","Umesh Kadam","Upasana kumari","Sachin Avhad","Yuvraj Patel","Hussain Pithawala"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mCredential = GoogleAccountCredential.usingOAuth2(getBaseContext(), Arrays.asList(SCOPES));
        mCredential.setSelectedAccountName(accountName);

        QuickBook = (TextView) findViewById(R.id.textView);
        durationGroup = (RadioGroup) findViewById(R.id.durationGroup);
        cancel = (Button) findViewById(R.id.cancelButton);
        book = (Button) findViewById(R.id.bookButton);
        empNames = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        /* InputMethodManager imm=(InputMethodManager)getSystemService(BookingActivity.this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/

        Intent getData = getIntent();
        timeSlot = getData.getStringExtra("timeSlot");
        roomName = getData.getStringExtra("roomName");

        System.out.println("***"+timeSlot+"roomname ="+roomName);

        try {

            selectedTime = format.parse(timeSlot);

        } catch (ParseException e) {

            e.printStackTrace();
        }
      /*  Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedTime);
        calendar.add(Calendar.MINUTE,30);
        System.out.println("addition = "+calendar.getTime());*/

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1,empNamesList);
        empNames.setAdapter(adapter);
        empNames.setThreshold(1);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTaskRunner task = new AsyncTaskRunner();
                task.execute();

            }
        });
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            createEvent(mCredential);

            return null;
        }
    }

    public void createEvent(GoogleAccountCredential mCredential) {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("R_D_Location Calendar")
                .build();

        Event event = new Event()
                .setSummary("Event- MAY 2017")
                .setLocation("Dhaka")
                .setDescription("New test event 1");

        DateTime startDateTime = new DateTime("2016-09-17T18:10:00+06:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Dhaka");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2016-09-17T18:40:00+06:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Dhaka");
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail("kiran.bodakhe@synerzip.com"),
                // new EventAttendee().setEmail("asdasd@andlk.com"),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            event = service.events().insert(calendarId, event).execute();
        } catch (UserRecoverableAuthIOException e) {
            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Event created: %s\n", event.getHtmlLink());


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
