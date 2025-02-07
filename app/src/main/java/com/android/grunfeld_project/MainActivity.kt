package com.android.grunfeld_project

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.android.grunfeld_project.activities.UserAuth.AuthActivity
import com.android.grunfeld_project.fragments.DevPostsFragment
import com.android.grunfeld_project.fragments.LeaderBoardFragment
import com.android.grunfeld_project.fragments.ScheduleFragment
import com.android.grunfeld_project.network.SupabaseClient.supabaseClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.black)
        window.navigationBarColor = getColor(R.color.black)
        val loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else {
            bottomNavBar()
        }
    }


    fun bottomNavBar() {
        // Your implementation for bottom navigation bar.
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        tabLayout.setSelectedTabIndicatorHeight(0)


        val fragmentContainerView = findViewById<FrameLayout>(R.id.fragment_container_view)

        var leaderBoardTab = tabLayout.newTab()
        val leaderBoardTabView: View = layoutInflater.inflate(R.layout.navigation_tablayout_item, null)
        leaderBoardTab.customView = leaderBoardTabView
        val leaderBoardTabIcon = leaderBoardTabView.findViewById<ImageView>(R.id.tab_icon)
        leaderBoardTabIcon.setImageResource(R.drawable.leaderboard)
        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.gray)
        val leaderBoardTextView = leaderBoardTabView.findViewById<TextView>(R.id.tab_text)
        leaderBoardTextView.text = "Leader Board"
        tabLayout.addTab(leaderBoardTab)

        var scheduleTab = tabLayout.newTab()
        val scheduleTabView: View = layoutInflater.inflate(R.layout.navigation_tablayout_item, null)
        scheduleTab.customView = scheduleTabView
        val scheduleTabIcon = scheduleTabView.findViewById<ImageView>(R.id.tab_icon)
        scheduleTabIcon.setImageResource(R.drawable.schedule)
        scheduleTabIcon.imageTintList = getColorStateList(R.color.gray)
        val scheduleTextView = scheduleTabView.findViewById<TextView>(R.id.tab_text)
        scheduleTextView.text = "Schedule"
        tabLayout.addTab(scheduleTab)

        var profileTab = tabLayout.newTab()
        val profileTabView: View = layoutInflater.inflate(R.layout.navigation_tablayout_item, null)
        profileTab.customView = profileTabView
        val profileTabIcon = profileTabView.findViewById<ImageView>(R.id.tab_icon)
        profileTabIcon.setImageResource(R.drawable.posts)
        profileTabIcon.imageTintList = getColorStateList(R.color.gray)
        val profileTextView = profileTabView.findViewById<TextView>(R.id.tab_text)
        profileTextView.text = "Dev. Posts"
        tabLayout.addTab(profileTab)


        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            @SuppressLint("ResourceAsColor")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val leaderBoardFragment = LeaderBoardFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, leaderBoardFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.blue)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.gray)
                        profileTabIcon.imageTintList = getColorStateList(R.color.gray)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.white))
                        scheduleTextView.setTextColor(getColorStateList(R.color.gray))
                        profileTextView.setTextColor(getColorStateList(R.color.gray))
                    }
                    1 -> {
                        val scheduleFragment = ScheduleFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, scheduleFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.gray)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.blue)
                        profileTabIcon.imageTintList = getColorStateList(R.color.gray)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.gray))
                        scheduleTextView.setTextColor(getColorStateList(R.color.white))
                        profileTextView.setTextColor(getColorStateList(R.color.gray))
                    }
                    2 -> {
                        val devPostsFragment = DevPostsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, devPostsFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.gray)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.gray)
                        profileTabIcon.imageTintList = getColorStateList(R.color.blue)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.gray))
                        scheduleTextView.setTextColor(getColorStateList(R.color.gray))
                        profileTextView.setTextColor(getColorStateList(R.color.white))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val leaderBoardFragment = LeaderBoardFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, leaderBoardFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.blue)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.gray)
                        profileTabIcon.imageTintList = getColorStateList(R.color.gray)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.white))
                        scheduleTextView.setTextColor(getColorStateList(R.color.gray))
                        profileTextView.setTextColor(getColorStateList(R.color.gray))
                    }
                    1 -> {
                        val scheduleFragment = ScheduleFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, scheduleFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.gray)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.blue)
                        profileTabIcon.imageTintList = getColorStateList(R.color.gray)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.gray))
                        scheduleTextView.setTextColor(getColorStateList(R.color.white))
                        profileTextView.setTextColor(getColorStateList(R.color.gray))
                    }
                    2 -> {
                        val devPostsFragment = DevPostsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, devPostsFragment)
                            .commit()

                        leaderBoardTabIcon.imageTintList = getColorStateList(R.color.gray)
                        scheduleTabIcon.imageTintList = getColorStateList(R.color.gray)
                        profileTabIcon.imageTintList = getColorStateList(R.color.blue)
                        leaderBoardTextView.setTextColor(getColorStateList(R.color.gray))
                        scheduleTextView.setTextColor(getColorStateList(R.color.gray))
                        profileTextView.setTextColor(getColorStateList(R.color.white))
                    }
                }
            }
        })
        scheduleTab.select()

    }
}
