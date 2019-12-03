package com.anwar069.smartprofile;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

import com.anwar069.smartprofile.adapters.DaysGridAdapter;
import com.anwar069.smartprofile.adapters.SpinnerAdapter;
import com.anwar069.smartprofile.model.Alarm;
import com.anwar069.smartprofile.model.AlarmTime;
import com.anwar069.smartprofile.model.DbHelper;

import extensions.CheckableTextview;

public class AddProfileActivity extends AppCompatActivity {

    TextView tvTime;
    TextInputLayout inputLayoutName;
    ArrayAdapter<String> adapter;
    EditText etProfName;
    Spinner spProfiles;
    int schedMin = 0, schedHour = 0;
    private SQLiteDatabase db;
    private static final SimpleDateFormat sdf = new SimpleDateFormat();

    ArrayList<String> profNameList;
    // Variable for storing current date and time
    private int mHour, mMinute;
    String[] spinnerValues = {"General", "Vibrate", "Silent"};
    ArrayList<String> daysList= new ArrayList<>(Arrays.asList("SUN","MON","TUE","WED","THU","FRI","SAT"));
    GridView gvDays;

    Boolean update=false;
    String updateProfName="";
    String updateID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        setUpViews();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = SmartProfile.db;


        profNameList = SmartProfile.dbHelper.getAllNames(db);

        String[] days = getResources().getStringArray(R.array.day_arr);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, days);
        spProfiles.setAdapter(new SpinnerAdapter(this, spinnerValues));
        DaysGridAdapter daysGridAdapter;

        gvDays.invalidateViews();

        etProfName.addTextChangedListener(new MyTextWatcher(etProfName));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            update=true;

            updateID=extras.getString("AlarmID");
            updateProfName=extras.getString("profName");
            etProfName.setText(updateProfName);
            tvTime.setText(extras.getString("profTime"));
            spProfiles.setSelection(extras.getInt("profMode"));
            String profRepeat = extras.getString("ProfRepeat");
            daysGridAdapter = new DaysGridAdapter(this,profRepeat+ " ");
        }else {
            daysGridAdapter = new DaysGridAdapter(this," ");
        }

        gvDays.setAdapter(daysGridAdapter);

        findViewById(R.id.ibtnSelectTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(AddProfileActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String timeSet;
                                schedHour = hourOfDay;
                                schedMin = minute;
                                if (hourOfDay > 12) {
                                    hourOfDay -= 12;
                                    timeSet = "PM";
                                } else if (hourOfDay == 0) {
                                    hourOfDay += 12;
                                    timeSet = "AM";
                                } else if (hourOfDay == 12)
                                    timeSet = "PM";
                                else
                                    timeSet = "AM";
                                String minutes;
                                if (minute < 10)
                                    minutes = "0" + minute;
                                else
                                    minutes = String.valueOf(minute);

                                // Append in a StringBuilder
                                String aTime = new StringBuilder().append(hourOfDay).append(':')
                                        .append(minutes).append(" ").append(timeSet).toString();
                                tvTime.setText(aTime);

                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });

        findViewById(R.id.btnSaveProf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateName()) {
                    if(update){
                        Intent cancelThis = new Intent(AddProfileActivity.this, AlarmService.class);
                        cancelThis.putExtra(Alarm.COL_ID, String.valueOf(updateID));
                        cancelThis.setAction(AlarmService.CANCEL);
                        startService(cancelThis);
                        SmartProfile.dbHelper.cancelNotification(db, Long.parseLong(updateID));
                    }



                    String daysRepeat = "";
                    for (int i = 0; i < gvDays.getChildCount(); ++i) {
                        CheckableTextview nextChild = (CheckableTextview) gvDays.getChildAt(i);
                        if (nextChild.isChecked()) {
//                        daysRepeat += i + ",";
                            daysRepeat += nextChild.getText().toString() + ",";
//                        checkedDays.add(nextChild.getText().toString());
                        }
                    }
//                Toast.makeText(getBaseContext(),checkedDays.toString(),Toast.LENGTH_LONG).show();
                    if (!("".equals(daysRepeat)))
                        daysRepeat = daysRepeat.substring(0, daysRepeat.length() - 1);
                    String profName = etProfName.getText().toString();

                    if (profName.equals("")) {
                        Toast.makeText(getBaseContext(),
                                "Please Enter Profile Name", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Alarm alarm = new Alarm();
                        alarm.setName(profName.trim().toString());
                        AlarmTime alarmTime = new AlarmTime();
                        long alarmId;
                        Calendar c = Calendar.getInstance();

                        alarm.setFromDate(Util.toPersistentDate(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH), sdf));
                        alarm.setToDate(Util.toPersistentDate("2025-01-01", sdf));
                        alarm.setRepeat_on(daysRepeat);
                        alarm.setAt(tvTime.getText().toString());
                        alarm.setStatus(Alarm.ACTIVE);
                        alarm.setSound_mode(spinnerValues[spProfiles.getSelectedItemPosition()]);
                        alarmId = alarm.persist(db);

                        alarmTime.setAt(DbHelper.getTimeStr(schedHour, schedMin));
                        alarmTime.setAlarmId(alarmId);
                        alarmTime.persist(db);
                        Intent service = new Intent(AddProfileActivity.this, AlarmService.class);
                        service.putExtra(Alarm.COL_ID, String.valueOf(alarmId));
                        service.setAction(AlarmService.POPULATE);
                        startService(service);

//                        Toast.makeText(getApplicationContext(), alarm.getId() + " = " + alarm.getFromDate() + " " + alarmTime.getAt(), Toast.LENGTH_LONG).show();
                    }


                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please correct the above error !!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean validateName() {
        String text = etProfName.getText().toString();
        if (text.trim().isEmpty()) {
            inputLayoutName.setError("Enter profile name");
            requestFocus(etProfName);
            return false;
        } else if (profNameList.contains(text.trim())) {
            if (text.trim().equals(updateProfName) && update) {
                inputLayoutName.setErrorEnabled(false);
                return true;
            }

            inputLayoutName.setError("Profile with name '" + text.trim() + "' already exist, try another name");
            requestFocus(etProfName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }


        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setUpViews() {
        tvTime = (TextView) findViewById(R.id.tvtime);
        spProfiles = (Spinner) findViewById(R.id.spProfiles);
        gvDays = (GridView) findViewById(R.id.myGrid);
        etProfName = (EditText) findViewById(R.id.etProfName);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etProfName:
                    validateName();
                    break;

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
