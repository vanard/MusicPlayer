package com.vanard.learnmusicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.vanard.learnmusicplayer.ui.main.AlbumsFragment
import com.vanard.learnmusicplayer.ui.main.SongsFragment
import com.vanard.learnmusicplayer.ui.main.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)

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

        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}