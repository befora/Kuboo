package com.sethchhim.kuboo_client.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import butterknife.ButterKnife
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.sethchhim.kuboo_client.Extensions.getVisibleFragment
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.service.OnClearFromRecentService
import com.sethchhim.kuboo_client.ui.main.browser.*
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragment
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragment
import com.sethchhim.kuboo_client.ui.main.login.edit.LoginEditFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragment
import com.sethchhim.kuboo_client.ui.main.settings.advanced.SettingsAdvancedFragment


open class MainActivity : MainActivityImpl2_Selection(), BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout_base)
        ButterKnife.bind(this)
        setTitle(R.string.title_home)
        setSupportActionBar(toolbar)

        bottomNav.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        bottomNav.setOnNavigationItemSelectedListener(this)
        bottomNav.setOnNavigationItemReselectedListener(this)

        if (systemUtil.isFirstRunOfThisVersion()) showChangeLog()

        supportFragmentManager.addOnBackStackChangedListener { setTitleByCurrentFragment() }

        loginLiveData.removeAllObservers(this)
        loginLiveData = viewModel.activeLoginLiveData.apply {
            observe(this@MainActivity, Observer { result -> onActiveLoginChanged(result) })
        }

        startService(Intent(this, OnClearFromRecentService::class.java))
        showToastDebug()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setStateLoading()
        setActiveLogin()
        handleIntentRequest(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.clearPathList()
        handleIntentRequest(intent)
    }

    override fun onResume() {
        super.onResume()
        forceOrientationSetting()
    }

    override fun onBackPressed() {
        when (getCurrentFragment()) {
            is BrowserLatestFragment -> selectHome()
            is BrowserRecentFragment -> selectHome()
            is BrowserRemoteFragment -> onBackPressedBrowserRemote()
            is BrowserSearchFragment -> selectHome()
            is BrowserSeriesFragment -> selectHome()
            is LoginEditFragment -> showFragmentLoginBrowser()
            is LoginBrowserFragment -> showFragmentSettings()
            is SettingsAdvancedFragment -> showFragmentSettings()
            else -> when (bottomNav.selectedItemId) {
                R.id.navigation_home -> systemUtil.requestExitApplication()
                R.id.navigation_browse -> selectHome()
                R.id.navigation_settings -> selectHome()
                R.id.navigation_downloads -> selectHome()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        browserLayoutMenuItem = menu.findItem(R.id.main_action_browser_layout)
        downloadMenuItem = menu.findItem(R.id.main_action_download)
        httpsMenuItem = menu.findItem(R.id.main_action_https)
        markFinishedAddMenuItem = menu.findItem(R.id.main_action_mark_finished_add)
        markFinishedDeleteMenuItem = menu.findItem(R.id.main_action_mark_finish_delete)
        searchMenuItem = menu.findItem(R.id.main_action_search).apply {
            searchView = actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String) = true

                override fun onQueryTextSubmit(query: String): Boolean {
                    showFragmentBrowserSearch(query)
                    return true
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_overflow_about -> showActivityAbout()
            R.id.main_overflow_log -> showActivityLog()
            R.id.main_action_browser_layout -> toggleBrowserLayout()
            R.id.main_action_https -> showDialogHttps()
            R.id.main_action_download -> startSelectionDownload()
            R.id.main_action_mark_finished_add -> startSelectionAddFinished()
            R.id.main_action_mark_finish_delete -> startSelectionDeleteFinished()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setStateLoading()
        viewModel.cancelAllPing()

        when (item.itemId) {
            R.id.navigation_home -> {
                title = getString(R.string.title_home)
                pingShowHomeFragment()
            }
            R.id.navigation_browse -> {
                title = getString(R.string.title_browse)
                pingShowBrowserRemoteFragment()
            }
            R.id.navigation_downloads -> {
                title = getString(R.string.title_downloads)
                showFragmentDownloads()
            }
            R.id.navigation_settings -> {
                title = getString(R.string.title_settings)
                showFragmentSettings()
            }
        }
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        val currentFragment = supportFragmentManager.getVisibleFragment()

        when (item.itemId) {
            R.id.navigation_home -> {
                when (currentFragment) {
                    is HomeFragment -> homeFragment.resetHome()
                    else -> {
                        setStateLoading()
                        viewModel.cancelAllPing()
                        pingShowHomeFragment()
                    }
                }
            }
            R.id.navigation_browse -> {
                when (currentFragment) {
                    is BrowserRemoteFragment -> browserRemoteFragment.resetBrowser()
                    else -> {
                        setStateLoading()
                        viewModel.cancelAllPing()
                        pingShowBrowserRemoteFragment()
                    }
                }
            }
            R.id.navigation_downloads -> {
                when (currentFragment) {
                    is DownloadsFragment -> downloadFragment.scrollToTop()
                    else -> {
                        setStateLoading()
                        viewModel.cancelAllPing()
                        showFragmentDownloads()
                    }
                }
            }
            R.id.navigation_settings -> {
                when (currentFragment) {
                    is SettingsFragment -> settingsFragment.scrollToTop()
                    else -> {
                        setStateLoading()
                        viewModel.cancelAllPing()
                        showFragmentSettings()
                    }
                }
            }
        }
    }

}