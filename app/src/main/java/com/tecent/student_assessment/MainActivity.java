package com.tecent.student_assessment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    ImageView nav_header_imageView;
    Toolbar toolbar;
    TextView nav_header_textView_name, nav_header_textView_email;
    SharedPreferences sharedPreferences, sharedPreferencesLike;
    RequestQueue requestQueue;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        requestQueue = Volley.newRequestQueue(this);
        fab=findViewById(R.id.fab_post);
        sharedPreferences = this.getSharedPreferences("studentAssessment", Context.MODE_PRIVATE);
        sharedPreferencesLike = this.getSharedPreferences("postLikes", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String userdp = sharedPreferences.getString("userdp", "");
        String userid=sharedPreferences.getString("userid","");

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Home");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        nav_header_imageView = headerView.findViewById(R.id.nav_header_imageView);
        nav_header_textView_name =headerView.findViewById(R.id.nav_header_textView_name);
        nav_header_textView_email =headerView.findViewById(R.id.nav_header_textView_email);
        nav_header_textView_name.setText(name.toUpperCase());
        nav_header_textView_email.setText(email);
        requestQueue.add(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, nav_header_imageView));
        //Floating Action Button Listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fIntent=new Intent(MainActivity.this, CreatePostHome.class);
                startActivity(fIntent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            fab.show();
                            selectedFragment = new HomeFragment();
                            toolbar.setTitle("Home");
                            break;
                        case R.id.nav_dashboard:
                            fab.hide();
                            selectedFragment = new DashboardFragment();
                            toolbar.setTitle("Dashboard");
                            break;
                        case R.id.nav_test_series:
                            fab.hide();
                            selectedFragment = new Test_Series_Fragment();
                            toolbar.setTitle("Test Series");
                            break;
                        case R.id.nav_doubt:
                            fab.hide();
                            selectedFragment = new DoubtsFragment();
                            toolbar.setTitle("Doubts");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent intProfile=new Intent(this,Profile.class);
                intProfile.putExtra("profile","MyProfile");
                startActivity(intProfile);
                break;
            case R.id.nav_practice:
                Intent intSp=new Intent(this,SinglePostsActivity.class);
                intSp.putExtra("postid","36");
                startActivity(intSp);
                break;
            case R.id.nav_performance:
                Toast.makeText(this, "Performance", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_notes:
                Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_appSettings:
                Intent intentSettings=new Intent(MainActivity.this,Settings.class);
                startActivity(intentSettings);
                break;
            case R.id.nav_helpAndFaqs:
                Intent intentHelp = new Intent();
                intentHelp.setAction(Intent.ACTION_VIEW);
                intentHelp.setData(Uri.parse("https://www.sas.a3creators.co.in/helpAndFaqs.php"));
                startActivity(intentHelp);
                break;
            case R.id.nav_contactUs:
                Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_shareApp:
                Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_rateApp:
                Toast.makeText(this, "Rate App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_checkForUpdates:
                Toast.makeText(this, "Check For Updates", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_visitWebsite:
                Intent intentWeb = new Intent();
                intentWeb.setAction(Intent.ACTION_VIEW);
                intentWeb.setData(Uri.parse("https://www.sas.a3creators.co.in"));
                startActivity(intentWeb);
                break;
            case R.id.nav_privacyPolicy:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.a3creators.co.in/privacypolicy.html"));
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
