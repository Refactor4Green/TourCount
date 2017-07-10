/*
 * Copyright (c) 2016. Wilhelm Stein, Bonn, Germany.
 */

package com.wmstein.tourcount;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.wmstein.tourcount.database.Count;
import com.wmstein.tourcount.database.CountDataSource;
import com.wmstein.tourcount.database.Individuals;
import com.wmstein.tourcount.database.IndividualsDataSource;
import com.wmstein.tourcount.database.Temp;
import com.wmstein.tourcount.database.TempDataSource;
import com.wmstein.tourcount.widgets.EditIndividualWidget;

/**
 * Created by wmstein on 15.05.2016
 */

/***********************************************************************************************************************/
// EditIndividualActivity is called from CountingActivity 
public class EditIndividualActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String TAG = "TourCountEditIndividualActivity";
    private TourCountApplication tourCount;
    private SharedPreferences prefs;
    private Individuals individuals;
    private Temp temp;
    private Count counts;
    private LinearLayout individ_area;
    private EditIndividualWidget eiw;
    // the actual data
    private IndividualsDataSource individualsDataSource;
    private TempDataSource tempDataSource;
    private CountDataSource countDataSource;
    private Bitmap bMap;
    private BitmapDrawable bg;
    private boolean buttonSoundPref;
    private String buttonAlertSound;
    private boolean brightPref;
    
    private int count_id;
    private int i_id;
    private String specName;
    private double latitude, longitude, height;
    //private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_individual);

        TourCountApplication tourCount = (TourCountApplication) getApplication();
        prefs = TourCountApplication.getPrefs();
        prefs.registerOnSharedPreferenceChangeListener(this);
        getPrefs();

        // Set full brightness of screen
        if (brightPref)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = 1.0f;
            getWindow().setAttributes(params);
        }

        individ_area = (LinearLayout) findViewById(R.id.edit_individual);

        // get parameters from CountingActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            count_id = extras.getInt("count_id");
            i_id = extras.getInt("indiv_id");
            specName = extras.getString("SName");
            latitude = extras.getDouble("Latitude");
            longitude = extras.getDouble("Longitude");
            height = extras.getDouble("Height");
        }

        tourCount = (TourCountApplication) getApplication();

//        prefs = TourCountApplication.getPrefs();
//        prefs.registerOnSharedPreferenceChangeListener(this);

        ScrollView individ_screen = (ScrollView) findViewById(R.id.editIndividualScreen);
        bMap = tourCount.decodeBitmap(R.drawable.kbackground, tourCount.width, tourCount.height);
        assert individ_screen != null;
        bg = new BitmapDrawable(individ_screen.getResources(), bMap);
        individ_screen.setBackground(bg);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    /*
     * So preferences can be loaded at the start, and also when a change is detected.
     */
    private void getPrefs()
    {
        buttonSoundPref = prefs.getBoolean("pref_button_sound", false);
        buttonAlertSound = prefs.getString("alert_button_sound", null);
        brightPref = prefs.getBoolean("pref_bright", true);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onResume()
    {
        super.onResume();

        // clear any existing views
        individ_area.removeAllViews();

        // setup the data sources
        individualsDataSource = new IndividualsDataSource(this);
        individualsDataSource.open();
        tempDataSource = new TempDataSource(this);
        tempDataSource.open();
        countDataSource = new CountDataSource(this);
        countDataSource.open();

        String[] stateArray = {
            getString(R.string.stadium_1),
            getString(R.string.stadium_2),
            getString(R.string.stadium_3),
            getString(R.string.stadium_4)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>
            (this, android.R.layout.simple_dropdown_item_1line, stateArray);

        // set title
        try
        {
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(specName);
        } catch (NullPointerException e)
        {
            Log.i(TAG, "NullPointerException: No species name!");
        }

        individuals = individualsDataSource.getIndividual(i_id);
        temp = tempDataSource.getTemp();
        counts = countDataSource.getCountById(count_id);

        // display the editable data
        eiw = new EditIndividualWidget(this, null);
        eiw.setWidgetLocality1(getString(R.string.locality));
        if (temp.temp_loc.equals(""))
        {
            eiw.setWidgetLocality2(individuals.locality);
        }
        else
        {
            eiw.setWidgetLocality2(temp.temp_loc);
        }

        eiw.setWidgetZCoord1(getString(R.string.zcoord));
        eiw.setWidgetZCoord2(Double.toString(height));

        eiw.setWidgetSex1(getString(R.string.sex1));
        eiw.setWidgetSex2(individuals.sex);

        eiw.setWidgetStadium1(getString(R.string.stadium1));
        AutoCompleteTextView acTextView = (AutoCompleteTextView) eiw.findViewById(R.id.widgetStadium2);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);

        eiw.setWidgetStadium2(getString(R.string.stadium_1));

        eiw.setWidgetState1(getString(R.string.state));
        eiw.setWidgetState2(individuals.state_1_6);

        eiw.setWidgetCount1(getString(R.string.count1)); // icount
        eiw.setWidgetCount2(1);

        eiw.setWidgetIndivNote1(getString(R.string.note));
        eiw.setWidgetIndivNote2(individuals.notes);

        eiw.setWidgetXCoord1(getString(R.string.xcoord));
        eiw.setWidgetXCoord2(Double.toString(latitude));

        eiw.setWidgetYCoord1(getString(R.string.ycoord));
        eiw.setWidgetYCoord2(Double.toString(longitude));

        individ_area.addView(eiw);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // close the data sources
        individualsDataSource.close();
        tempDataSource.close();
        countDataSource.close();
    }

    private boolean saveData()
    {
        buttonSound();
        // save individual data
        // Locality
        String newlocality = eiw.getWidgetLocality2();
        if (!newlocality.equals(""))
        {
            individuals.locality = newlocality;
            temp.temp_loc = newlocality;
        }

        // Sex
        String newsex = eiw.getWidgetSex2();
        if (newsex.equals("") || newsex.matches(" |m|f"))
        {
            individuals.sex = newsex;
        }
        else
        {
            Toast.makeText(this, getString(R.string.valSex), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Stadium
        individuals.stadium = eiw.getWidgetStadium2();

        // State_1-6
        int newstate = eiw.getWidgetState2();
        if (newstate >= 0 && newstate < 7)
        {
            individuals.state_1_6 = newstate;
        }
        else
        {
            Toast.makeText(this, getString(R.string.valState), Toast.LENGTH_SHORT).show();
            return false;
        }

        // number of individuals
        int newcount = eiw.getWidgetCount2();
        counts.count = counts.count + newcount - 1; // -1 as CountingActivity already added 1
        individuals.icount = newcount;
        temp.temp_cnt = newcount;

        // Notes
        String newnotes = eiw.getWidgetIndivNote2();
        if (!newnotes.equals(""))
        {
            individuals.notes = newnotes;
        }

        individualsDataSource.saveIndividual(individuals);
        tempDataSource.saveTemp(temp);
        countDataSource.saveCount(counts);

        return true;
    }

    private void buttonSound()
    {
        if (buttonSoundPref)
        {
            try
            {
                Uri notification;
                if (isNotBlank(buttonAlertSound) && buttonAlertSound != null)
                {
                    notification = Uri.parse(buttonAlertSound);
                }
                else
                {
                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_individual, menu);
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
            {
                super.finish();
                // close the data sources
                individualsDataSource.close();
                tempDataSource.close();
                countDataSource.close();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        ScrollView individ_screen = (ScrollView) findViewById(R.id.editIndividualScreen);
        assert individ_screen != null;
        individ_screen.setBackground(null);
        bMap = tourCount.decodeBitmap(R.drawable.kbackground, tourCount.width, tourCount.height);
        bg = new BitmapDrawable(individ_screen.getResources(), bMap);
        individ_screen.setBackground(bg);
        getPrefs();
    }

    /**
     * Checks if a CharSequence is whitespace, empty ("") or null
     * <p>
     * isBlank(null)      = true
     * isBlank("")        = true
     * isBlank(" ")       = true
     * isBlank("bob")     = false
     * isBlank("  bob  ") = false
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     */
    private static boolean isBlank(final CharSequence cs)
    {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0)
        {
            return true;
        }
        for (int i = 0; i < strLen; i++)
        {
            if (!Character.isWhitespace(cs.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     * <p>
     * isNotBlank(null)      = false
     * isNotBlank("")        = false
     * isNotBlank(" ")       = false
     * isNotBlank("bob")     = true
     * isNotBlank("  bob  ") = true
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     */
    private static boolean isNotBlank(final CharSequence cs)
    {
        return !isBlank(cs);
    }

}
