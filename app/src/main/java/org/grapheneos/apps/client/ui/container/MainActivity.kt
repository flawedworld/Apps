package org.grapheneos.apps.client.ui.container

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.grapheneos.apps.client.R
import org.grapheneos.apps.client.databinding.ActivityMainBinding
import org.grapheneos.apps.client.service.SeamlessUpdaterJob


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var views: ActivityMainBinding
    private val navCtrl by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(views.container.id) as NavHostFragment

        navHostFragment.navController
    }
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(navCtrl.graph).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        setSupportActionBar(views.toolbar)
        window.setDecorFitsSystemWindows(false)

        ViewCompat.setOnApplyWindowInsetsListener(
            views.toolbar
        ) { v, insets ->

            val paddingInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.mandatorySystemGestures() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = paddingInsets.top
            }
            insets
        }

        if (SeamlessUpdaterJob.NOTIFICATION_ACTION == intent.action) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
                SeamlessUpdaterJob.NOTIFICATION_ID
            )
        }
        setupActionBarWithNavController(navCtrl, appBarConfiguration)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navCtrl) ||
                super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navCtrl.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}