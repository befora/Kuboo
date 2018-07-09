package com.sethchhim.kuboo_client.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import com.sethchhim.kuboo_client.Extensions.show
import com.sethchhim.kuboo_client.R
import com.sethchhim.kuboo_client.ui.about.AboutActivity
import com.sethchhim.kuboo_client.ui.base.BaseActivity
import com.sethchhim.kuboo_client.ui.main.browser.*
import com.sethchhim.kuboo_client.ui.main.downloads.DownloadsFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragment
import com.sethchhim.kuboo_client.ui.main.home.HomeFragmentImpl1_Content
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragment
import com.sethchhim.kuboo_client.ui.main.login.browser.LoginBrowserFragmentImpl0_View
import com.sethchhim.kuboo_client.ui.main.login.edit.LoginEditFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragment
import com.sethchhim.kuboo_client.ui.main.settings.SettingsFragmentImp0_View
import com.sethchhim.kuboo_client.ui.state.FailFragment
import com.sethchhim.kuboo_client.ui.state.LoadingFragment
import com.sethchhim.kuboo_client.ui.state.WelcomeFragment
import com.sethchhim.kuboo_remote.model.Book
import com.sethchhim.kuboo_remote.model.Login
import com.sethchhim.kuboo_remote.model.Response
import javax.inject.Inject

@SuppressLint("Registered")
open class MainActivityImpl0_View : BaseActivity() {

    @Inject lateinit var browserLatestFragment: BrowserLatestFragment
    @Inject lateinit var browserRecentFragment: BrowserRecentFragment
    @Inject lateinit var browserRemoteFragment: BrowserRemoteFragment
    @Inject lateinit var downloadFragment: DownloadsFragment
    @Inject lateinit var homeFragment: HomeFragment
    @Inject lateinit var settingsFragment: SettingsFragment

    @Inject lateinit var failFragment: FailFragment
    @Inject lateinit var loadingFragment: LoadingFragment
    @Inject lateinit var welcomeFragment: WelcomeFragment

    @BindView(R.id.main_layout_base_bottomNavigationView) lateinit var bottomNav: BottomNavigationView
    @BindView(R.id.main_layout_base_toolBar) lateinit var toolbar: Toolbar
    @BindView(R.id.main_layout_base_frameLayout) lateinit var frameLayout: FrameLayout

    internal lateinit var downloadMenuItem: MenuItem
    internal lateinit var httpsMenuItem: MenuItem
    internal lateinit var markFinishedAddMenuItem: MenuItem
    internal lateinit var markFinishedDeleteMenuItem: MenuItem
    internal lateinit var searchMenuItem: MenuItem

    internal lateinit var searchView: SearchView

    internal fun isMenuStateSelected() = downloadMenuItem.isVisible

    internal fun getCurrentFragment() = supportFragmentManager.findFragmentById(R.id.main_layout_base_frameLayout)

    internal fun selectHome() {
        bottomNav.selectedItemId = R.id.navigation_home
    }

    internal fun setStateConnected() {
        showMenuItemSearch()
        toggleMenuItemHttps()
    }

    internal fun setStateDisconnected(response: Response?) {
        hideMenuItemSearch()
        hideMenuItemHttps()
        showFragmentFail(response)
    }

    internal fun setStateLoading() {
        hideMenuItemSearch()
        hideMenuItemHttps()
        showFragmentLoading()
    }

    internal fun showFragmentBrowserLatest() = supportFragmentManager.show(browserLatestFragment, R.id.main_layout_base_frameLayout)

    internal fun showFragmentBrowserRecent() = supportFragmentManager.show(browserRecentFragment, R.id.main_layout_base_frameLayout)

    internal fun showFragmentBrowserSeries(book: Book) = supportFragmentManager.show(BrowserSeriesFragment.newInstance(book), R.id.main_layout_base_frameLayout)

    internal fun showFragmentLoginBrowser() = supportFragmentManager.show(LoginBrowserFragment(), R.id.main_layout_base_frameLayout)

    internal fun showFragmentLoginEdit(login: Login?) = supportFragmentManager.show(LoginEditFragment.newInstance(login), R.id.main_layout_base_frameLayout)

    internal fun showFragmentSettings() = supportFragmentManager.show(settingsFragment, R.id.main_layout_base_frameLayout)

    internal fun collapseMenuItemSearch() {
        if (::searchMenuItem.isInitialized && searchMenuItem.isActionViewExpanded) searchMenuItem.collapseActionView()
    }

    protected fun onBackPressedBrowserRemote() {
        val previousBook = viewModel.getPreviousBook()
        when (previousBook != null) {
            true -> browserRemoteFragment.populatePrevious(previousBook!!)
            false -> selectHome()
        }
    }

    protected fun showActivityAbout() = startActivity(Intent(this, AboutActivity::class.java))

    protected fun showDialogHttps() {
        val tlsCipherSuite = viewModel.getTlsCipherSuite()
        dialogUtil.getDialogHttps(this, tlsCipherSuite).apply {
            findViewById<TextView>(android.R.id.message)?.textSize = 9f
            show()
        }
    }

    protected fun showFragmentBrowserRemote() = supportFragmentManager.show(browserRemoteFragment, R.id.main_layout_base_frameLayout)

    protected fun showFragmentBrowserSearch(stringQuery: String) = supportFragmentManager.show(BrowserSearchFragment.newInstance(stringQuery), R.id.main_layout_base_frameLayout)

    protected fun showFragmentDownloads() = supportFragmentManager.show(downloadFragment, R.id.main_layout_base_frameLayout)

    private fun showFragmentLoading() = supportFragmentManager.show(loadingFragment, R.id.main_layout_base_frameLayout)

    protected fun showFragmentHome() = when (viewModel.isLoginListEmpty() || viewModel.isActiveLoginEmpty()) {
        true -> supportFragmentManager.show(welcomeFragment, R.id.main_layout_base_frameLayout)
        false -> supportFragmentManager.show(homeFragment, R.id.main_layout_base_frameLayout)
    }

    protected fun isHomeSelected() = bottomNav.selectedItemId == R.id.navigation_home

    protected fun isBrowseSelected() = bottomNav.selectedItemId == R.id.navigation_browse

    private fun getFragmentTitle(currentFragment: Fragment) = when (currentFragment) {
        is BrowserRemoteFragment -> if (isMenuStateSelected()) getSelectedBrowserTitle() else getString(R.string.main_browse)
        is BrowserRecentFragment -> if (isMenuStateSelected()) getSelectedBrowserTitle() else getString(R.string.main_recent)
        is BrowserSearchFragment -> if (isMenuStateSelected()) getSelectedBrowserTitle() else getString(R.string.main_search)
        is BrowserSeriesFragment -> if (isMenuStateSelected()) getSelectedBrowserTitle() else getString(R.string.main_series)
        is DownloadsFragment -> getString(R.string.main_downloads)
        is FailFragment -> getString(R.string.app_label)
        is LoginBrowserFragmentImpl0_View -> getString(R.string.login_servers)
        is LoginEditFragment -> getString(R.string.login_edit_server)
        is SettingsFragmentImp0_View -> getString(R.string.main_settings)
        is HomeFragmentImpl1_Content -> getString(R.string.main_read_now)
        is WelcomeFragment -> getString(R.string.main_home_welcome)
        else -> "ERROR"
    }

    private fun getNavigationId(currentFragment: Fragment) = when (currentFragment) {
        is BrowserRecentFragment -> R.id.navigation_browse
        is BrowserRemoteFragment -> R.id.navigation_browse
        is BrowserSeriesFragment -> R.id.navigation_browse
        is DownloadsFragment -> R.id.navigation_downloads
        is LoginBrowserFragment -> R.id.navigation_settings
        is LoginEditFragment -> R.id.navigation_settings
        is SettingsFragmentImp0_View -> R.id.navigation_settings
        else -> R.id.navigation_home
    }

    protected fun getSelectedBrowserTitle() = "${getString(R.string.main_selected)} (${viewModel.getSelectedListSize()})"

    protected fun hideMenuItemHttps() {
        if (::httpsMenuItem.isInitialized && httpsMenuItem.isVisible) httpsMenuItem.isVisible = false
    }

    private fun hideMenuItemSearch() {
        if (::searchMenuItem.isInitialized && searchMenuItem.isVisible) searchMenuItem.isVisible = false
    }

    private fun showFragmentFail(response: Response?) = supportFragmentManager.show(FailFragment.newInstance(response), R.id.main_layout_base_frameLayout)

    private fun showMenuItemHttps() {
        if (::httpsMenuItem.isInitialized && !httpsMenuItem.isVisible) httpsMenuItem.isVisible = true
    }

    private fun showMenuItemSearch() {
        if (::searchMenuItem.isInitialized && !searchMenuItem.isVisible) searchMenuItem.isVisible = true
    }

    protected fun toggleMenuItemHttps() = when (viewModel.isConnectedEncrypted()) {
        true -> showMenuItemHttps()
        false -> hideMenuItemHttps()
    }

}