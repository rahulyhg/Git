package com.techno.jay.codingcontests;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techno.jay.codingcontests.Firebase.DataObject_postFirebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DB.DatabaseHelper;

public class ResultContestLink extends AppCompatActivity {

    Date date_Start,date_End,date_Now;
    private DatabaseReference databaseReference;
    ProgressBar p_bar;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    String queryget;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private boolean loading = true;
    private int visibleThreshold = 5;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    ArrayList results;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_contest_link);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/ProductSans-Regular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //==2) for fragment hoy to====
        //== fontChanger.replaceFonts((ViewGroup) this.getView());
        //===3) for adepterview and handlerview na use mate====
        //==this = inflater.inflate(R.layout.listitem, null);
        //==fontChanger.replaceFonts((ViewGroup)this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        p_bar=(ProgressBar)this.findViewById(R.id.p_bar);


        results = new ArrayList<DataObject_post>();
        mAdapter = new MyRecyclerAdapter_contestview(results);

        // Initialize recycler view
        mRecyclerView = (RecyclerView)this.findViewById(R.id.post_recycler_view);

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


         queryget=getIntent().getStringExtra("query");

        new FirebaseDataLoad().execute();

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();

//=========================================================================================================
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    //onHide();


                    controlsVisible = false;
                    scrolledDistance = 0;
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    //onShow();
                   controlsVisible = true;
                    scrolledDistance = 0;

                   /* if(textView_loadmore.getVisibility()==View.VISIBLE){
                        textView_loadmore.setVisibility(View.GONE);
                    }*/

                }

                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy;
                }

                //======================================================================================================

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                          /*  textView_loadmore.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                            textView_loadmore.setVisibility(View.VISIBLE);*/



                        }
                    }


                    if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems + visibleThreshold)) {
                        // End has been reached

                       /* textView_loadmore.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                        textView_loadmore.setVisibility(View.VISIBLE);*/

                        loading = true;

                    }


                }

                //=======================================================================================================
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


        });
    }


    class FirebaseDataLoad extends AsyncTask<Object, Void, String> {
        @Override
        protected void onPreExecute()//execute thaya pela
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... parametros) {
            // System.out.println("On do in back ground----done-------");
            //Log.d("post execute", "Executando doInBackground   ingredients");

            try {
                databaseReference = FirebaseDatabase.getInstance().getReference("Contest").child("objects");
                databaseReference.keepSynced(true);
                databaseReference.orderByChild("objects").limitToFirst(1000).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mRecyclerView.setAdapter(mAdapter);
                        results.clear();
                        mAdapter.notifyDataSetChanged();

                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            //Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                            //Log.e("objects =====" ,""+dataSnapshot.toString());

                            String DURATION = singleSnapshot.child("duration").getValue().toString();
                            String END = singleSnapshot.child("end").getValue().toString();
                            String START = singleSnapshot.child("start").getValue().toString();
                            String EVENT = singleSnapshot.child("event").getValue().toString();
                            String HREF = singleSnapshot.child("href").getValue().toString();
                            long CONTEST_ID = Long.parseLong(singleSnapshot.child("id").getValue().toString());
                            long RESOURCE_ID = Long.parseLong(singleSnapshot.child("resource").child("id").getValue().toString());
                            String RESOURCE_NAME = singleSnapshot.child("resource").child("name").getValue().toString();
                            // String REMINDER_STATUS = singleSnapshot.child(Integer.toString(i)).child("").getValue().toString();


                            try {
                                SimpleDateFormat format_diffrent = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date now = new Date();
                                String strDate = format_diffrent.format(now);

                                date_Start = format_diffrent.parse(START);
                                date_End = format_diffrent.parse(END);
                                date_Now= format_diffrent.parse(strDate);

                                if(queryget.equalsIgnoreCase(RESOURCE_NAME)
                                        || EVENT.contains(queryget)
                                        || queryget.equalsIgnoreCase(EVENT)
                                        ||queryget.equalsIgnoreCase(HREF)){
                                    DataObject_postFirebase obj12 = new DataObject_postFirebase(DURATION, END, START, EVENT, HREF, CONTEST_ID, RESOURCE_ID, RESOURCE_NAME,"0");
                                    //Log.i("DURATION",DURATION);
                                    results.add(obj12);
                                }
                            }catch (Exception ecxe){
                                ecxe.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        p_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ResultContestLink.this,Home.class));

        finish();
    }
}
