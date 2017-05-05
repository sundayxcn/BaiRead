package sunday.app.bairead.bookcase;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import butterknife.ButterKnife;
import sunday.app.bairead.R;
import sunday.app.bairead.base.BaseActivity;
import sunday.app.bairead.base.DisclaimerActivity;
import sunday.app.bairead.bookSearch.BookSearchActivity;
import sunday.app.bairead.data.BookRepository;
import sunday.app.bairead.utils.ActivityUtils;
import sunday.app.bairead.utils.FileManager;
import sunday.app.bairead.utils.PreferenceKey;
import sunday.app.bairead.utils.PreferenceSetting;

public class BookcaseActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private BookcasePresenter mBookcasePresenter;
    private BookcaseFragment mBookcaseFragment;
    private PreferenceSetting mPreferenceSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPreferenceSetting = PreferenceSetting.getInstance(getApplicationContext());

        mBookcaseFragment = new BookcaseFragment();
        ActivityUtils.addFragmentToActivity(getFragmentManager(),mBookcaseFragment,R.id.contentFrame);
        mBookcasePresenter = new BookcasePresenter(
                BookRepository.getInstance(getApplicationContext()),
                PreferenceSetting.getInstance(getApplicationContext()),
                mBookcaseFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mBookcaseFragment.onBackPressed()) {

        } else {
            doubleClickBack();
        }
    }
    private int click = 0;
    public void doubleClickBack() {
        if (click >= 1) {
            super.onBackPressed();
        } else {
            click++;
            showToast(R.string.double_click_cancel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    click = 0;
                }
            }, 3000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent();
            intent.setClass(this, BookSearchActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_order_chapter_count){
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER, PreferenceKey.ORDER_CHAPTER_COUNT);
            mBookcaseFragment.reOrder();
        }else if(id == R.id.action_order_author){
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER, PreferenceKey.ORDER_AUTHOR);
            mBookcaseFragment.reOrder();
        }else if(id == R.id.action_order_update_time){
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER, PreferenceKey.ORDER_UPDATE_TIME);
            mBookcaseFragment.reOrder();
        } else if(id == R.id.action_order_add_book){
            mPreferenceSetting.putIntValue(PreferenceSetting.KEY_CASE_LIST_ORDER, PreferenceKey.ORDER_DEFAULT);
            mBookcaseFragment.reOrder();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_clear_cache) {
            showConfirmDialog(R.string.delete_all_book, new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    FileManager.getInstance().deleteAllCahce();
                }
            });
        } else if (id == R.id.nav_restore_config) {
            showConfirmDialog(R.string.restore_config, new DialogListenerIm() {
                @Override
                public void onConfirmAsync() {
                    super.onConfirmAsync();
                    PreferenceSetting.getInstance(BookcaseActivity.this).clear();
                    PreferenceSetting.getInstance(BookcaseActivity.this).setFirstRunFalse();
                }
            });
        } else if (id == R.id.nav_suggest_report) {
            FeedbackAPI.openFeedbackActivity();
        } else if (id == R.id.nav_inflater_book) {
            mBookcaseFragment.inflateBook();
        } else if (id == R.id.nav_version) {
            Intent intent = new Intent();
            intent.setClass(this, DisclaimerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
