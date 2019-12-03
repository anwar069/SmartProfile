package com.anwar069.smartprofile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.anwar069.smartprofile.model.Alarm;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private TextView emptyView;
    private SQLiteDatabase db;
    boolean onLoad = false;
    int total_images[] = {R.mipmap.ic_general, R.mipmap.ic_vibrate, R.mipmap.ic_silent};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpViews();

        db = SmartProfile.db;
        fab.setOnClickListener(onClickListener);


        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);


        emptyView.setOnClickListener(onClickListener);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.MyAppbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
                    isShow = true;
                }
                 else if (isShow) {
                                    setCollapsibletoolbar();
                                }
            }
        });

//
//        lvProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openContextMenu(view);
//            }
//        });
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent iAddProf = new Intent(getBaseContext(), AddProfileActivity.class);
            startActivity(iAddProf);
        }
    };
    private void setUpViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        emptyView = (TextView) findViewById(R.id.empty_view);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


    private void diplayList() {
        Cursor cursor = createCursor();
        mRecyclerView.setAdapter(new CardAdapter(cursor, MainActivity.this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent iSettings = new Intent(this, SettingsActivity.class);
//            startActivity(iSettings);
//        }

        return super.onOptionsItemSelected(item);
    }

    private Cursor createCursor() {
        Cursor c = SmartProfile.dbHelper.listNotifications(db);
        startManagingCursor(c);
        return c;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCollapsibletoolbar();
        diplayList();

    }

    private void setCollapsibletoolbar() {
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.icons ));
        if(hour>19 || (hour>=0 && hour<6)) {
            imageView.setImageResource(R.drawable.night);
            collapsingToolbar.setTitle("Good Night :-)");
        }else if(hour>=6 && hour<9){
            imageView.setImageResource(R.drawable.sunset);
            collapsingToolbar.setTitle("Good Morning !!");
        }else if(hour>=9 && hour<12){
            imageView.setImageResource(R.drawable.day);
            collapsingToolbar.setTitle("Good Morning :-)");
        }else if(hour>=12 && hour<16){
            imageView.setImageResource(R.drawable.work);
            collapsingToolbar.setTitle("Good Afternoon ");
        }else if(hour>=16 && hour<18){
            imageView.setImageResource(R.drawable.snacks);
            collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            collapsingToolbar.setTitle("It's Afternoon");
        }else if(hour>=18 && hour<21){
            imageView.setImageResource(R.drawable.evening);
            collapsingToolbar.setTitle("Evening :-)");
        }else {
            collapsingToolbar.setTitle("Good Night |-)");
            imageView.setImageResource(R.drawable.night);
        }
    }


    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.AlarmViewHolder> {

        Cursor dataCursor;
        Context context;
        int total_images[] = {R.mipmap.ic_general, R.mipmap.ic_vibrate, R.mipmap.ic_silent};
        private int position;
        private int lastPosition = -1;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public CardAdapter(Cursor cursor, Context context) {
            super();
            dataCursor = cursor;
            this.context = context;
        }

        @Override
        public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_view_card_item, viewGroup, false);
            return new AlarmViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AlarmViewHolder alarmViewHolder, int position) {
            dataCursor.moveToPosition(position);
            // Load data from dataCursor and return it...
            alarmViewHolder.profID = dataCursor.getString(1);
            alarmViewHolder.tvProfName.setText(dataCursor.getString(0));
            alarmViewHolder.tvProfRepeat.setText("ON " + dataCursor.getString(2));
            alarmViewHolder.tvProfTime.setText("AT " + dataCursor.getString(3));
            String profType = dataCursor.getString(5);
            String status = dataCursor.getString(4);
            onLoad = true;
            alarmViewHolder.sActive.setChecked(status.equals(Alarm.ACTIVE));
            onLoad = false;
            alarmViewHolder.profType = profType;
            alarmViewHolder.imgProf.setImageResource(total_images[profType.equals("General") ? 0 : profType.equals("Vibrate") ? 1 : 2]);

        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemCount() {
            int count;
            if(dataCursor.getCount() == 0)
            {
                mRecyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                count = 0;
            }else {
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                count = dataCursor.getCount();
            }
            return count;
        }

        class AlarmViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgProf;
            public TextView tvProfName;
            public TextView tvProfRepeat;
            public TextView tvProfTime;
            public ImageView ivDelete;
            public ImageView ivEdit;
            public SwitchCompat sActive;
            public String profID;
            public String profType;

            public AlarmViewHolder(final View itemView) {
                super(itemView);
                imgProf = (ImageView) itemView.findViewById(R.id.prof_icon);
                tvProfName = (TextView) itemView.findViewById(R.id.prof_name);
                tvProfRepeat = (TextView) itemView.findViewById(R.id.repeat);
                tvProfTime = (TextView) itemView.findViewById(R.id.at);
                sActive = (SwitchCompat) itemView.findViewById(R.id.s_active);
                ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
                ivEdit = (ImageView) itemView.findViewById(R.id.iv_edit);

                sActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!onLoad) {
                            if (!isChecked) {
                                SmartProfile.dbHelper.disableNotification(db, Long.parseLong(profID));
                                Intent disableThis = new Intent(MainActivity.this, AlarmService.class);
                                disableThis.putExtra(Alarm.COL_ID, String.valueOf(Long.parseLong(profID)));
                                disableThis.setAction(AlarmService.CANCEL);
                                startService(disableThis);
                                Snackbar.make(coordinatorLayout, tvProfName.getText().toString() + " Disabled!", Snackbar.LENGTH_LONG).show();
                            } else {
                                SmartProfile.dbHelper.enableNotification(db, Long.parseLong(profID));
                                Intent disableThis = new Intent(MainActivity.this, AlarmService.class);
                                disableThis.putExtra(Alarm.COL_ID, String.valueOf(Long.parseLong(profID)));
                                disableThis.setAction(AlarmService.CREATE);
                                startService(disableThis);

                            }
                         refreshDataSet();
//                            Toast.makeText(getApplicationContext(), profID + " is " + isChecked, Toast.LENGTH_LONG).show();
                        }
                        onLoad = false;
                    }
                });

                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iUpdateProf = new Intent(getBaseContext(), AddProfileActivity.class);
                        iUpdateProf.putExtra("AlarmID", profID);
                        iUpdateProf.putExtra("profName",tvProfName.getText().toString());
                        iUpdateProf.putExtra("profTime",tvProfTime.getText().toString().replace("AT ", ""));
                        iUpdateProf.putExtra("ProfRepeat",tvProfRepeat.getText().toString().replace("ON ", "")+",");
                        iUpdateProf.putExtra("profMode",profType.equals("General") ? 0 : profType.equals("Vibrate") ? 1 : 2);
                        startActivity(iUpdateProf);
                    }
                });

                ivDelete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

                                                    // Setting Dialog Title
                                                    alertDialog.setTitle("Confirm Delete...");

                                                    // Setting Dialog Message
                                                    alertDialog.setMessage("Are you sure you want delete this profile?");

                                                    // Setting Icon to Dialog
                                                    alertDialog.setIcon(R.mipmap.ic_alert_delete);

                                                    // Setting Positive "Yes" Button
                                                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {



                                                            Intent cancelThis = new Intent(MainActivity.this, AlarmService.class);
                                                            cancelThis.putExtra(Alarm.COL_ID, String.valueOf(profID));
                                                            cancelThis.setAction(AlarmService.CANCEL);
                                                            startService(cancelThis);

                                                            // Write your code here to invoke YES event
                                                            SmartProfile.dbHelper.cancelNotification(db, Long.parseLong(profID));
                                                            refreshDataSet();

                                                            Snackbar snackbar = Snackbar
                                                                    .make(coordinatorLayout,tvProfName.getText().toString() + " is Deleted!", Snackbar.LENGTH_LONG)
                                                                    .setAction("Create New", onClickListener);

                                                            // Changing message text color
                                                            snackbar.setActionTextColor(Color.GREEN);
                                                            // Changing action button text color
                                                            View sbView = snackbar.getView();
                                                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                                            textView.setTextColor(Color.RED);
                                                            snackbar.show();
                                                        }
                                                    });

                                                    // Setting Negative "NO" Button
                                                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Write your code here to invoke NO event
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    // Showing Alert Message
                                                    alertDialog.show();

                                                }
                                            }

                );
            }
        }

        private void refreshDataSet() {
            CardAdapter adapter = (CardAdapter) mRecyclerView.getAdapter();
            adapter.dataCursor.requery();
            adapter.notifyDataSetChanged();
        }
    }
}
