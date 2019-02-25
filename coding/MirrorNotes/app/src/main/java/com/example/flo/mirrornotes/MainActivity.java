package com.example.flo.mirrornotes;

import android.Manifest;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidParameterException;

public class MainActivity extends AppCompatActivity implements  IReceptionNotifier {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static EditText todoText;
    private static EditText shoppingText;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static private Communication communicater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For permission to store screenshot
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 00);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton syncbtn = (FloatingActionButton) findViewById(R.id.btn_sync);
        syncbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicater.synchronize();
            }
        });

        FloatingActionButton uploadbtn = (FloatingActionButton) findViewById(R.id.btn_upload);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] todoStr = todoText.getText().toString().split("\n");
                String[] shoppingStr = shoppingText.getText().toString().split("\n");

                StringWriter sw = new StringWriter();
                for(String s : todoStr)
                {
                    sw.append("todo,");
                    if(s.isEmpty())
                    {
                        sw.append("Write something");
                    }
                    else {
                        sw.append(s);
                    }
                    sw.append("\n");
                }
                for(String s : shoppingStr)
                {
                    sw.append("shopping,");
                    if(s.isEmpty())
                    {
                        sw.append("Write something");
                    }
                    else {
                        sw.append(s);
                    }
                    sw.append("\n");
                }

                communicater.sendData(sw.toString());
            }
        });

        FloatingActionButton savebtn = (FloatingActionButton) findViewById(R.id.btn_save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureScreen();
            }
        });

        communicater = new Communication(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //TODO: could be removed completely
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receptionCallback(String res)
    {
        String[] str = res.split("\n");

        StringWriter swTodo = new StringWriter();
        StringWriter swShopping = new StringWriter();

        for(String s : str)
        {
            if(s.contains("todo"))
            {
                String[] f = s.split(",");
                swTodo.append(f[1]);
                swTodo.append("\n");
            }
            else if(s.contains("shopping"))
            {
                String[] f = s.split(",");
                swShopping.append(f[1]);
                swShopping.append("\n");
            }
        }

        todoText.setText(swTodo.toString());
        shoppingText.setText(swShopping.toString());
    }

    private void captureScreen() {
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        try {
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "SCREEN"
                    + System.currentTimeMillis() + ".png"));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                textView.setText("TODOs");
                todoText = (EditText) rootView.findViewById(R.id.section_data);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                textView.setText("Shopping");
                shoppingText = (EditText) rootView.findViewById(R.id.section_data);
            }
            else {
                throw new InvalidParameterException();
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
