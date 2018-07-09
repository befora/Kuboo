package com.sethchhim.kuboo_client.ui.main

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import butterknife.ButterKnife
import com.sethchhim.kuboo_client.Constants
import com.sethchhim.kuboo_client.Extensions.disableShiftMode
import com.sethchhim.kuboo_client.Extensions.getVisibleFragment
import com.sethchhim.kuboo_client.Extensions.removeAllObservers
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.main.browser.*
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragment
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragment
import com.sethchhim.kuboo_client.ui.main.login.edit.LoginEditFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragment
import com.sethchhim.kuboo_remote.model.Book
import timber.log.Timber


class MainActivity : MainActivityImpl3_Service(), BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener, SearchView.OnQueryTextListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout_base)
        ButterKnife.bind(this)
        setTitle(R.string.title_home)
        setSupportActionBar(toolbar)

        bottomNav.setOnNavigationItemSelectedListener(this)
        bottomNav.setOnNavigationItemReselectedListener(this)
        bottomNav.disableShiftMode()

        showChangeLog()

        supportFragmentManager.addOnBackStackChangedListener {
            when (getCurrentFragment()) {
                is DownloadsFragment -> title = getString(R.string.title_downloads)
                is BrowserLatestFragment -> title = getString(R.string.main_latest_added)
                is BrowserRecentFragment -> title = getString(R.string.main_recently_viewed)
                is BrowserRemoteFragment -> title = getString(R.string.title_browse)
                is BrowserSeriesFragment -> title = getString(R.string.main_series)
                is HomeFragment -> title = getString(R.string.title_home)
                is LoginBrowserFragment -> title = getString(R.string.login_servers)
                is LoginEditFragment -> title = getString(R.string.login_edit_server)
                is SettingsFragment -> title = getString(R.string.title_settings)
            }
        }

        loginLiveData.removeAllObservers(this)
        loginLiveData = viewModel.activeLoginLiveData.apply {
            observe(this@MainActivity, Observer { result -> onActiveLoginChanged(result) })
        }


        setStateLoading()
        setDownloadTrackingService()
        setOnClearFromRecentService()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.clearPathList()

        isRequestDownloadFragment = intent.getBooleanExtra(Constants.ARG_REQUEST_DOWNLOAD_FRAGMENT, false)
        isRequestRemoteBrowserFragment = intent.getBooleanExtra(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT, false)
        when {
            isRequestDownloadFragment -> {
                Timber.i("Download fragment is requested from intent.")
                showFragmentDownloads()
            }
            isRequestRemoteBrowserFragment -> {
                Timber.i("Remote browser fragment is requested from intent.")
                val payload = intent.getParcelableExtra<Book>(Constants.ARG_REQUEST_REMOTE_BROWSER_FRAGMENT_PAYLOAD)
                when (payload != null) {
                    true -> showFragmentBrowserSeries(payload)
                    false -> showToastError()
                }
            }
        }
    }

    private fun showChangeLog() {
        if (systemUtil.isFirstRunOfThisVersion()) dialogUtil.getDialogChangeLog(this).apply {
            show()
            window.attributes = WindowManager.LayoutParams().apply {
                val systemWidth = systemUtil.getSystemWidth()
                val systemHeight = systemUtil.getSystemHeight()
                val newSize = (Math.min(systemWidth, systemHeight) * 0.9f).toInt()
                width = newSize
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setActiveLogin()
    }

    override fun onResume() {
        super.onResume()
        forceOrientationSetting()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        downloadMenuItem = menu.findItem(R.id.main_action_download)
        httpsMenuItem = menu.findItem(R.id.main_action_https)
        markFinishedAddMenuItem = menu.findItem(R.id.main_action_mark_finished_add)
        markFinishedDeleteMenuItem = menu.findItem(R.id.main_action_mark_finish_delete)
        searchMenuItem = menu.findItem(R.id.main_action_search).apply {
            searchView = actionView as SearchView
            searchView.setOnQueryTextListener(this@MainActivity)
        }
        return true
    }

    override fun onBackPressed() {
        when (getCurrentFragment()) {
            is BrowserLatestFragment -> supportFragmentManager.popBackStackImmediate()
            is BrowserRecentFragment -> supportFragmentManager.popBackStackImmediate()
            is BrowserRemoteFragment -> onBackPressedBrowserRemote()
            is BrowserSearchFragment -> supportFragmentManager.popBackStackImmediate()
            is BrowserSeriesFragment -> supportFragmentManager.popBackStackImmediate()
            is LoginEditFragment -> supportFragmentManager.popBackStackImmediate()
            is LoginBrowserFragment -> supportFragmentManager.popBackStackImmediate()
            else -> when (bottomNav.selectedItemId) {
                R.id.navigation_home -> systemUtil.requestExitApplication()
                R.id.navigation_browse -> selectHome()
                R.id.navigation_settings -> selectHome()
                R.id.navigation_downloads -> selectHome()
            }
        }
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
                    is HomeFragment -> homeFragment.scrollToFirstRecent()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_overflow_about -> showActivityAbout()
            R.id.main_action_https -> showDialogHttps()
            R.id.main_action_download -> startSelectionDownload()
            R.id.main_action_mark_finished_add -> startSelectionAddFinished()
            R.id.main_action_mark_finish_delete -> startSelectionDeleteFinished()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextChange(newText: String?) = true

    override fun onQueryTextSubmit(query: String): Boolean {
        showFragmentBrowserSearch(query)
        return true
    }

}