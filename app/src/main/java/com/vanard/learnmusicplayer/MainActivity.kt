package com.vanard.learnmusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.vanard.learnmusicplayer.ui.main.AlbumsFragment
import com.vanard.learnmusicplayer.ui.main.SongsFragment
import com.vanard.learnmusicplayer.ui.main.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
    )

    private val WRITE_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)

        permission()
        setupViewPager()
    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf<Fragment>(
            SongsFragment(),
            AlbumsFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )

        viewPager.adapter = adapter

        TabLayoutMediator(tabs, viewPager,
            TabConfigurationStrategy { tab, position ->
                tab.text = this.resources.getString(TAB_TITLES[position])
            }).attach()
    }

    private fun permission() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            WRITE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                } else {
                    makeRequest()
                }

            }
        }
    }
}