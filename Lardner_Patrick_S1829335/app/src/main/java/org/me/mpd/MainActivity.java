//Patrick Lardner S1829335

package org.me.mpd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle barToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isFragment = false;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        barToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(barToggle);
        barToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isFragment) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            barToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            barToggle.setDrawerIndicatorEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.displayFragment, new HomeFragment()).commit();
        }

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_incidents:
                        fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/currentincidents.aspx", "incidents");
                        break;
                    case R.id.nav_planned_roadworks:
                        fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx", "roadworks");
                        break;
                    case R.id.nav_roadworks:
                        fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/roadworks.aspx", "planned");
                        break;
                    default:
                        break;
                }

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.displayFragment, fragment);
                    transaction.commit();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                return false;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        getSupportFragmentManager().popBackStackImmediate();
        if (barToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}