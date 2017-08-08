package com.ohmnismart.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
 
 
public class ActivityMain extends AppCompatActivity {
	
	private static final int RESULT_SETTINGS = 1;

	ViewPager viewPager;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        /*
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        */
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Status"));
        tabLayout.addTab(tabLayout.newTab().setText("Load"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
 
        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
        
        
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
 
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
 
            }
 
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
 
            }
        });
    }
 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent i;
    	String code;
        switch (item.getItemId()) {
        case R.id.action_refresh:
			FragmentStatus fragmentStatus = (FragmentStatus) getSupportFragmentManager()
					.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
			if (fragmentStatus != null) {
				fragmentStatus.updateView();
			}
			return true;
        case R.id.action_status:
        	// Check GoSAKTO status @ SMS
			code = "*143*1*7" + Uri.encode("#");
			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
        	startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_balance:
        	// Check your balance @ USSD
			code = "*143*2*1*1" + Uri.encode("#");
        	// Check other's balance @ USSD
			//String code = "*143*2*1*2*" + "9XXXXXXXXX" + Uri.encode("#");
			i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + code));
        	startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_accessibility:
        	i = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        	startActivityForResult(i, RESULT_SETTINGS);
			return true;
        case R.id.action_settings:
			i = new Intent(this, ActivitySettings.class);
			startActivityForResult(i, RESULT_SETTINGS);
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}