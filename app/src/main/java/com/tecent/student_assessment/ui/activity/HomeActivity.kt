package com.tecent.student_assessment.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.tecent.student_assessment.ui.fragments.DashboardFragment
import com.tecent.student_assessment.ui.fragments.DoubtsFragment
import com.tecent.student_assessment.ui.fragments.HomeFragment
import com.tecent.student_assessment.R.id
import com.tecent.student_assessment.R.layout
import com.tecent.student_assessment.R.string
import com.tecent.student_assessment.ui.fragments.TestSeriesFragment
import com.tecent.student_assessment.utils.ExtraFunctions
import java.io.ByteArrayOutputStream
import java.util.Locale

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private var drawerLayout: DrawerLayout? = null
  lateinit internal var nav_header_imageView: ImageView
  lateinit internal var userProfileImage: ImageView
  lateinit internal var menuBtn: ImageView
  lateinit internal var etSearch: TextView
  lateinit internal var nav_header_textView_name: TextView
  lateinit internal var nav_header_textView_email: TextView
  lateinit internal var sharedPreferences: SharedPreferences
  lateinit internal var sharedPreferencesLike: SharedPreferences
  lateinit internal var requestQueue: RequestQueue
  lateinit internal var fab: FloatingActionButton
  var doubleBackToExitPressedOnce = false

  private val navigationItemSelectedListener =
    BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
      var selectedFragment: Fragment? = null
      when (menuItem.itemId) {
        id.nav_home -> {
          fab.show()
          selectedFragment =
            HomeFragment()
        }
        id.nav_dashboard -> {
          fab.hide()
          selectedFragment =
            DashboardFragment()
        }
        id.nav_test_series -> {
          fab.hide()
          selectedFragment =
            TestSeriesFragment()
        }
        id.nav_doubt -> {
          fab.hide()
          selectedFragment =
            DoubtsFragment()
        }
      }
      supportFragmentManager.beginTransaction()
          .replace(id.fragment_container, selectedFragment!!)
          .commit()
      true
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_home)
    requestQueue = Volley.newRequestQueue(this)
    fab = findViewById(id.fab_post)
    sharedPreferences = this.getSharedPreferences(
        ExtraFunctions.sharedPreferencesId, Context.MODE_PRIVATE
    )
    sharedPreferencesLike = this.getSharedPreferences("postLikes", Context.MODE_PRIVATE)
    val name = sharedPreferences.getString("name", "")
    val email = sharedPreferences.getString("email", "")
    val userdp = sharedPreferences.getString("userdp", "")
    sharedPreferences.getString("userid", "")
    userProfileImage = findViewById(id.userDp)
    menuBtn = findViewById(id.menuBtn)
    etSearch = findViewById(id.etSearch)
    menuBtn.setOnClickListener { drawerLayout!!.openDrawer(Gravity.START) }
    requestQueue.add(
        ExtraFunctions.createImageRequestFromUrl(
            ExtraFunctions.serverurl + "userdp/" + userdp, userProfileImage
        )
    )

    val bottomNavigationView = findViewById<BottomNavigationView>(id.bottom_navigation_view)
    bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    supportFragmentManager.beginTransaction()
        .replace(id.fragment_container,
            HomeFragment()
        )
        .commit()

    drawerLayout = findViewById(id.drawer_layout)
    val navigationView = findViewById<NavigationView>(id.nav_view)
    navigationView.setNavigationItemSelectedListener(this)
    val actionBarDrawerToggle = ActionBarDrawerToggle(
        this, drawerLayout, string.navigation_drawer_open,
        string.navigation_drawer_close
    )
    drawerLayout!!.addDrawerListener(actionBarDrawerToggle)
    actionBarDrawerToggle.syncState()
    val headerView = navigationView.getHeaderView(0)
    nav_header_imageView = headerView.findViewById(id.nav_header_imageView)
    nav_header_textView_name = headerView.findViewById(id.nav_header_textView_name)
    nav_header_textView_email = headerView.findViewById(id.nav_header_textView_email)
    nav_header_textView_name.text = name!!.toUpperCase(Locale.ROOT)
    nav_header_textView_email.text = email
    requestQueue.add(
        ExtraFunctions.createImageRequestFromUrl(
            ExtraFunctions.serverurl + "userdp/" + userdp, nav_header_imageView
        )
    )
    //Floating Action Button Listener
    fab.setOnClickListener {
      val fIntent = Intent(this@HomeActivity, CreatePostHomeActivity::class.java)
      startActivity(fIntent)
    }
    nav_header_imageView.setOnClickListener {
      animateIntent(nav_header_imageView)
    }
    userProfileImage.setOnClickListener {
      animateIntent(userProfileImage)
    }
    Handler().postDelayed({
      etSearch.text = resources.getString(string.android)
    }, 10000)

  }

  private fun animateIntent(view: ImageView) {
    val intent = Intent(this, ImageViewerActivity::class.java)
    intent.putExtra("intentType", "byteArray")
    intent.putExtra(
        "imageByteArray",
        getFileDataFromDrawable(view.drawable)
    )
    val transitionName = getString(string.transition_string)

    val options =
      ActivityOptionsCompat.makeSceneTransitionAnimation(
          this,
          view, // Starting view
          transitionName    // The String
      )
    ActivityCompat.startActivity(this, intent, options.toBundle())
  }

  fun getFileDataFromDrawable(drawable: Drawable): ByteArray {
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

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      id.nav_profile -> {
        val intProfile = Intent(this, ProfileActivity::class.java)
        intProfile.putExtra("profile", "MyProfile")
        startActivity(intProfile)
      }
      id.nav_practice -> {
        val intDashBoardQuizes = Intent(this, DashBoardMenuWebViewActivity::class.java)
        intDashBoardQuizes.putExtra("title", "Practice")
        startActivity(intDashBoardQuizes)
      }
      id.nav_performance -> {
        val intSubResult = Intent(this, ShowResultFromDashboardActivity::class.java)
        startActivity(intSubResult)
      }
      id.nav_notes -> Toast.makeText(this, "Notes", Toast.LENGTH_SHORT)
          .show()
      id.nav_discussion_forum -> {
        val intentDiscussionForum = Intent(this, DiscussionForumActivity::class.java)
        startActivity(intentDiscussionForum)
      }
      id.nav_appSettings -> {
        val intentSettings = Intent(this@HomeActivity, SettingsActivity::class.java)
        startActivity(intentSettings)
      }
      id.nav_helpAndFaqs -> {
        val intentHelp = Intent()
        intentHelp.action = Intent.ACTION_VIEW
        intentHelp.data = Uri.parse("https://www.sas.a3creators.co.in/helpAndFaqs.php")
        startActivity(intentHelp)
      }
      id.nav_contactUs -> {
        val intentContact = Intent(this@HomeActivity, ContactUsActivity::class.java)
        startActivity(intentContact)
      }
      id.nav_shareApp -> Toast.makeText(this, "Share App", Toast.LENGTH_SHORT)
          .show()
      id.nav_rateApp -> Toast.makeText(this, "Rate App", Toast.LENGTH_SHORT)
          .show()
      id.nav_checkForUpdates -> Toast.makeText(this, "Check For Updates", Toast.LENGTH_SHORT)
          .show()
      id.nav_visitWebsite -> {
        val intentWeb = Intent()
        intentWeb.action = Intent.ACTION_VIEW
        intentWeb.data = Uri.parse("https://www.sas.a3creators.co.in")
        startActivity(intentWeb)
      }
      id.nav_privacyPolicy -> {
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
    val transitionName = getString(string.transition_string)

    val options =
      ActivityOptionsCompat.makeSceneTransitionAnimation(
          this,
          view, // Starting view
          transitionName    // The String
      )
    ActivityCompat.startActivity(this, intent, options.toBundle())
  }

  override fun onBackPressed() {
    if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
      drawerLayout!!.closeDrawer(GravityCompat.START)
    } else {
      if (doubleBackToExitPressedOnce) {
        super.onBackPressed()
        return
      }
      this.doubleBackToExitPressedOnce = true
      Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT)
          .show()
      Handler().postDelayed({
        doubleBackToExitPressedOnce = false

      }, 2000)
    }
  }

  override fun onResume() {
    super.onResume()
    if (sharedPreferences.getBoolean("dataChange", false)) {
      sharedPreferences.edit()
          .putBoolean("dataChange", false)
          .apply()
      recreate()
    }
  }
}
