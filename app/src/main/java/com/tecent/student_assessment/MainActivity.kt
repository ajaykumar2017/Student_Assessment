package com.tecent.student_assessment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.support.v7.widget.Toolbar

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    lateinit internal var nav_header_imageView: ImageView
    lateinit internal var userProfileImage: ImageView
    lateinit internal var menuBtn: ImageView
    lateinit internal var nav_header_textView_name: TextView
    lateinit internal var nav_header_textView_email: TextView
    lateinit internal var sharedPreferences: SharedPreferences
    lateinit internal var sharedPreferencesLike: SharedPreferences
    lateinit internal var requestQueue: RequestQueue
    lateinit  internal var fab: FloatingActionButton

    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        var selectedFragment: Fragment? = null
        when (menuItem.itemId) {
            R.id.nav_home -> {
                fab.show()
                selectedFragment = HomeFragment()
            }
            R.id.nav_dashboard -> {
                fab.hide()
                selectedFragment = DashboardFragment()
            }
            R.id.nav_test_series -> {
                fab.hide()
                selectedFragment = Test_Series_Fragment()
            }
            R.id.nav_doubt -> {
                fab.hide()
                selectedFragment = DoubtsFragment()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment!!).commit()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestQueue = Volley.newRequestQueue(this)
        fab = findViewById(R.id.fab_post)
        sharedPreferences = this.getSharedPreferences(ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE)
        sharedPreferencesLike = this.getSharedPreferences("postLikes", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val userdp = sharedPreferences.getString("userdp", "")
        val userid = sharedPreferences.getString("userid", "")
        userProfileImage = findViewById(R.id.userDp)
        menuBtn = findViewById(R.id.menuBtn)
        menuBtn.setOnClickListener { drawerLayout!!.openDrawer(Gravity.LEFT) }
        requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, userProfileImage))

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        val headerView = navigationView.getHeaderView(0)
        nav_header_imageView = headerView.findViewById(R.id.nav_header_imageView)
        nav_header_textView_name = headerView.findViewById(R.id.nav_header_textView_name)
        nav_header_textView_email = headerView.findViewById(R.id.nav_header_textView_email)
        nav_header_textView_name.text = name!!.toUpperCase()
        nav_header_textView_email.text = email
        requestQueue.add<Bitmap>(ExtraFunctions.createImageRequestFromUrl(ExtraFunctions.serverurl + "userdp/" + userdp, nav_header_imageView))
        //Floating Action Button Listener
        fab.setOnClickListener {
            val fIntent = Intent(this@MainActivity, CreatePostHome::class.java)
            startActivity(fIntent)
        }
        nav_header_imageView.setOnClickListener {
            animateIntent(nav_header_imageView)
        }
        userProfileImage.setOnClickListener {
            animateIntent(userProfileImage)
        }

    }

    fun animateIntent(view: ImageView) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra("intentType", "byteArray")
        intent.putExtra(
                "imageByteArray",
                getFileDataFromDrawable(baseContext, view.drawable)
        )
        val transitionName = getString(R.string.transition_string)

        val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        view, // Starting view
                        transitionName    // The String
                )
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout!!.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intProfile = Intent(this, Profile::class.java)
                intProfile.putExtra("profile", "MyProfile")
                startActivity(intProfile)
            }
            R.id.nav_practice -> {
                val intDashBoardQuizes = Intent(this, DashBoardMenuWebView::class.java)
                intDashBoardQuizes.putExtra("title", "Practice")
                startActivity(intDashBoardQuizes)
            }
            R.id.nav_performance -> {
                val intSubResult = Intent(this, ShowResultFromDashboard::class.java)
                startActivity(intSubResult)
            }
            R.id.nav_notes -> Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show()
            R.id.nav_appSettings -> {
                val intentSettings = Intent(this@MainActivity, Settings::class.java)
                startActivity(intentSettings)
            }
            R.id.nav_helpAndFaqs -> {
                val intentHelp = Intent()
                intentHelp.action = Intent.ACTION_VIEW
                intentHelp.data = Uri.parse("https://www.sas.a3creators.co.in/helpAndFaqs.php")
                startActivity(intentHelp)
            }
            R.id.nav_contactUs -> Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show()
            R.id.nav_shareApp -> Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show()
            R.id.nav_rateApp -> Toast.makeText(this, "Rate App", Toast.LENGTH_SHORT).show()
            R.id.nav_checkForUpdates -> Toast.makeText(this, "Check For Updates", Toast.LENGTH_SHORT).show()
            R.id.nav_visitWebsite -> {
                val intentWeb = Intent()
                intentWeb.action = Intent.ACTION_VIEW
                intentWeb.data = Uri.parse("https://www.sas.a3creators.co.in")
                startActivity(intentWeb)
            }
            R.id.nav_privacyPolicy -> {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://www.a3creators.co.in/privacypolicy.html")
                startActivity(intent)
            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }

        fun goToSearch(view: View) {
            val intent = Intent(this, SearchActivity::class.java)
            val transitionName = getString(R.string.transition_string)

            val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this,
                            view, // Starting view
                            transitionName    // The String
                    )
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }
}
