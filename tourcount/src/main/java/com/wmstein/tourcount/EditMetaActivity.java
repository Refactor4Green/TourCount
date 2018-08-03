package com.wmstein.tourcount;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wmstein.tourcount.database.Head;
import com.wmstein.tourcount.database.HeadDataSource;
import com.wmstein.tourcount.database.Section;
import com.wmstein.tourcount.database.SectionDataSource;
import com.wmstein.tourcount.widgets.EditHeadWidget;
import com.wmstein.tourcount.widgets.EditMetaWidget;
import com.wmstein.tourcount.widgets.EditTitleWidget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**********************************************************
 * EditMetaActivity collects meta info for the current tour
 * Created by wmstein on 2016-04-19,
 * last edit on 2018-08-03
 */
public class EditMetaActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static String TAG = "tourcountEditMetaAct";
    private TourCountApplication tourCount;

    SharedPreferences prefs;
    private boolean screenOrientL; // option for screen orientation
    
    private Head head;
    private Section section;
    private HeadDataSource headDataSource;
    private SectionDataSource sectionDataSource;

    private Calendar pdate, ptime;

    private LinearLayout head_area;
    private TextView sDate, sTime, eTime;

    private EditTitleWidget ett;
    private EditTitleWidget enw;
    private EditHeadWidget ehw;
    private EditMetaWidget etw;
    
    private Bitmap bMap;
    private BitmapDrawable bg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        tourCount = (TourCountApplication) getApplication();
        prefs = TourCountApplication.getPrefs();
        prefs.registerOnSharedPreferenceChangeListener(this);
        boolean brightPref = prefs.getBoolean("pref_bright", true);
        screenOrientL = prefs.getBoolean("screen_Orientation", false);

        setContentView(R.layout.activity_edit_head);

        ScrollView editHead_screen = findViewById(R.id.editHeadScreen);

        if (screenOrientL)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Set full brightness of screen
        if (brightPref)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = 1.0f;
            getWindow().setAttributes(params);
        }

        bMap = tourCount.decodeBitmap(R.drawable.kbackground, tourCount.width, tourCount.height);
        bg = new BitmapDrawable(editHead_screen.getResources(), bMap);
        editHead_screen.setBackground(bg);

        head_area = findViewById(R.id.edit_head);

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(getString(R.string.editHeadTitle));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        screenOrientL = prefs.getBoolean("screen_Orientation", false);

        if (screenOrientL)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //clear existing view
        head_area.removeAllViews();

        //setup data sources
        headDataSource = new HeadDataSource(this);
        headDataSource.open();
        sectionDataSource = new SectionDataSource(this);
        sectionDataSource.open();

        //load head and meta data
        head = headDataSource.getHead();
        section = sectionDataSource.getSection();

        // display an editable list title
        ett = new EditTitleWidget(this, null);
        ett.setSectionName(section.name);
        ett.setWidgetTitle(getString(R.string.titleEdit));
        head_area.addView(ett);

        // display editable section notes; the same class
        enw = new EditTitleWidget(this, null);
        enw.setSectionName(section.notes);
        enw.setWidgetTitle(getString(R.string.notesHere));
        enw.setHint(getString(R.string.notesHint));
        head_area.addView(enw);

        // display the editable head data
        ehw = new EditHeadWidget(this, null);
        ehw.setWidgetCo1(getString(R.string.country));
        ehw.setWidgetCo2(section.country);
        ehw.setWidgetName1(getString(R.string.inspector));
        ehw.setWidgetName2(head.observer);
        head_area.addView(ehw);

        // display the editable meta data
        etw = new EditMetaWidget(this, null);
        etw.setWidgetTemp1(getString(R.string.temperature));
        etw.setWidgetTemp2(section.temp);
        etw.setWidgetWind1(getString(R.string.wind));
        etw.setWidgetWind2(section.wind);
        etw.setWidgetClouds1(getString(R.string.clouds));
        etw.setWidgetClouds2(section.clouds);
        etw.setWidgetPlz1(getString(R.string.plz));
        etw.setWidgetPlz2(section.plz);
        etw.setWidgetCity1(getString(R.string.city));
        etw.setWidgetCity2(section.city);
        etw.setWidgetPlace1(getString(R.string.place));
        etw.setWidgetPlace2(section.place);
        etw.setWidgetDate1(getString(R.string.date));
        etw.setWidgetDate2(section.date);
        etw.setWidgetStartTm1(getString(R.string.starttm));
        etw.setWidgetStartTm2(section.start_tm);
        etw.setWidgetEndTm1(getString(R.string.endtm));
        etw.setWidgetEndTm2(section.end_tm);
        head_area.addView(etw);

        pdate = Calendar.getInstance();
        ptime = Calendar.getInstance();

        sDate = this.findViewById(R.id.widgetDate2);
        sTime = this.findViewById(R.id.widgetStartTm2);
        eTime = this.findViewById(R.id.widgetEndTm2);

        // get current date by click
        sDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date date = new Date();
                sDate.setText(getformDate(date));
            }
        });

        // get date picker result
        final DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                pdate.set(Calendar.YEAR, year);
                pdate.set(Calendar.MONTH, monthOfYear);
                pdate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date date = pdate.getTime();
                sDate.setText(getformDate(date));
            }
        };

        // select date by long click
        sDate.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                new DatePickerDialog(EditMetaActivity.this, dpd,
                    pdate.get(Calendar.YEAR),
                    pdate.get(Calendar.MONTH),
                    pdate.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        // get current start time
        sTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date date = new Date();
                sTime.setText(getformTime(date));
            }
        });

        // get start time picker result
        final TimePickerDialog.OnTimeSetListener stpd = new TimePickerDialog.OnTimeSetListener()
        {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                ptime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                ptime.set(Calendar.MINUTE, minute);
                Date date = ptime.getTime();
                sTime.setText(getformTime(date));
            }
        };

        // select start time
        sTime.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                new TimePickerDialog(EditMetaActivity.this, stpd,
                    ptime.get(Calendar.HOUR_OF_DAY),
                    ptime.get(Calendar.MINUTE),
                    true).show();
                return true;
            }
        });

        // get current end time
        eTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Date date = new Date();
                eTime.setText(getformTime(date));
            }
        });

        // get start time picker result
        final TimePickerDialog.OnTimeSetListener etpd = new TimePickerDialog.OnTimeSetListener()
        {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                ptime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                ptime.set(Calendar.MINUTE, minute);
                Date date = ptime.getTime();
                eTime.setText(getformTime(date));
            }
        };

        // select end time
        eTime.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                new TimePickerDialog(EditMetaActivity.this, etpd,
                    ptime.get(Calendar.HOUR_OF_DAY),
                    ptime.get(Calendar.MINUTE),
                    true).show();
                return true;
            }
        });
    }

    // formatted date
    public String getformDate(Date date)
    {
        DateFormat dform;
        String lng = Locale.getDefault().toString().substring(0, 2);

        if (lng.equals("de"))
        {
            dform = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
        }
        else
        {
            dform = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }
        return dform.format(date);
    }

    // date for start_tm and end_tm
    public String getformTime(Date date)
    {
        DateFormat dform = new SimpleDateFormat("HH:mm", Locale.US);
        return dform.format(date);
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();

        // close the data sources
        headDataSource.close();
        sectionDataSource.close();
    }

    // triggered by save button in actionbar
    public void saveAndExit(View view)
    {
        if (saveData())
            super.finish();
    }

    private boolean saveData()
    {
        // Save head data
        head.observer = ehw.setWidgetName2();

        headDataSource.saveHead(head);

        // Save meta data
        section.name = ett.getSectionName();
        section.notes = enw.getSectionName();

        section.country = ehw.setWidgetCo2();
        section.temp = etw.getWidgetTemp2();
        if (section.temp > 50 || section.temp < 0)
        {
            Snackbar sB = Snackbar.make(etw, Html.fromHtml("<font color=\"#ff0000\"><b>" +  getString(R.string.valTemp) + "</font></b>"), Snackbar.LENGTH_LONG);
            TextView tv = sB.getView().findViewById(R.id.snackbar_text);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            sB.show();
            return false;
        }
        section.wind = etw.getWidgetWind2();
        if (section.wind > 4 || section.wind < 0)
        {
            Snackbar sB = Snackbar.make(etw, Html.fromHtml("<font color=\"#ff0000\"><b>" +  getString(R.string.valWind) + "</font></b>"), Snackbar.LENGTH_LONG);
            TextView tv = sB.getView().findViewById(R.id.snackbar_text);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            sB.show();
            return false;
        }
        section.clouds = etw.getWidgetClouds2();
        if (section.clouds > 100 || section.clouds < 0)
        {
            Snackbar sB = Snackbar.make(etw, Html.fromHtml("<font color=\"#ff0000\"><b>" +  getString(R.string.valClouds) + "</font></b>"), Snackbar.LENGTH_LONG);
            TextView tv = sB.getView().findViewById(R.id.snackbar_text);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            sB.show();
            return false;
        }
        section.plz = etw.getWidgetPlz2();
        section.city = etw.getWidgetCity2();
        section.place = etw.getWidgetPlace2();
        section.date = etw.getWidgetDate2();
        section.start_tm = etw.getWidgetStartTm2();
        section.end_tm = etw.getWidgetEndTm2();

        sectionDataSource.saveSection(section);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_meta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuSaveExit)
        {
            if (saveData())
                super.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // puts up function to back button
    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        ScrollView editHead_screen = findViewById(R.id.editHeadScreen);
        screenOrientL = prefs.getBoolean("screen_Orientation", false);
        if (screenOrientL)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        bMap = tourCount.decodeBitmap(R.drawable.kbackground, tourCount.width, tourCount.height);
        editHead_screen.setBackground(null);
        bg = new BitmapDrawable(editHead_screen.getResources(), bMap);
        editHead_screen.setBackground(bg);
    }

}