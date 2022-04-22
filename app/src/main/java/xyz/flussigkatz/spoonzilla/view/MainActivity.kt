package xyz.flussigkatz.spoonzilla.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.schedulers.Schedulers.io
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.spoonzilla.view.notification.NotificationHelper
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_ADVANCED_SEARCH_SETTINGS
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_LOCAL_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_ADVANCED_SEARCH
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS
import xyz.flussigkatz.spoonzilla.util.AppConst.PADDING_DP
import xyz.flussigkatz.spoonzilla.util.AppConst.REMINDER_NOTIFICATION
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.NavigationHelper
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.rv_decoration.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.MainActivityViewModel
import java.lang.System.currentTimeMillis


class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private val receiver = Receiver()
    private var adapterIsNotEmpty = false
    private lateinit var recentlyViewedAdapter: DishRecyclerAdapter
    private lateinit var recentlyViewedBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private val autoDisposable = AutoDisposable()
    private val mainActivityScope = CoroutineScope(Dispatchers.IO)
    private var timeOnPressed = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        AppCompatDelegate.setDefaultNightMode(viewModel.getNightModeFromPreferences())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        initQuickSearch()
        initReceiver()
        initRecentlyViewedBottomSheet()
        initRecentlyViewedAdapter()
        initContent()
        initAlarms()
    }

    private fun initRecentlyViewedBottomSheet() {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= IS_SLIDE_FLAG) {
                    binding.mainTint.alpha = slideOffset / MAIN_TINT_ALPHA_RATIO
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.recentlyViewedLabel.apply {
                    when (newState) {
                        STATE_EXPANDED -> {
                            text = resources.getText(R.string.recently_viewed_label_title_expanded)
                            binding.recentlyViewedFab.animate()
                                .scaleX(ZERO_SCALE)
                                .scaleY(ZERO_SCALE)
                                .setDuration(RECENTLY_VIEWED_FAB_ANIM_DURATION)
                                .start()
                        }
                        STATE_COLLAPSED, STATE_HIDDEN -> {
                            binding.recentlyViewedFab.animate()
                                .scaleX(FULL_SCALE)
                                .scaleY(FULL_SCALE)
                                .setDuration(RECENTLY_VIEWED_FAB_ANIM_DURATION)
                                .start()
                            binding.mainTint.animate()
                                .alpha(ZERO_ALPHA)
                                .setDuration(MAIN_TINT_ANIM_DURATION)
                                .start()
                            text = resources.getText(R.string.recently_viewed_label_title)
                        }
                        else -> {}
                    }
                }
            }
        }
        recentlyViewedBottomSheet = from(binding.mainRecentlyViewed).apply {
            maxHeight = resources.getDimension(R.dimen.main_recently_viewed_max_height).toInt()
            state = STATE_HIDDEN
            addBottomSheetCallback(bottomSheetCallback)
        }
        binding.recentlyViewedFab.setOnClickListener {
            mainSearchViewClearFocus()
            recentlyViewedBottomSheet.state = STATE_COLLAPSED
        }
    }

    private fun initQuickSearch() {
        binding.mainQuickSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.mainAppbar.setExpanded(true)
            recentlyViewedBottomSheet.state = STATE_HIDDEN
        }
        binding.mainQuickSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (binding.mainQuickSearch.hasFocus()) {
                        viewModel.putSearchQuery(query.orEmpty())
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (binding.mainQuickSearch.hasFocus()) {
                        viewModel.putSearchQuery(newText.orEmpty())
                    }
                    return false
                }
            })
    }

    private fun initNavigation() {
        setSupportActionBar(binding.mainToolbar)
        binding.mainNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
        navController = findNavController(this, R.id.main_nav_host_fragment)
        binding.mainNavigationView.menu.findItem(R.id.logOutMenu).isEnabled = false
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.homeFragment,
                R.id.advancedSearchSettingsFragment,
                R.id.markedFragment,
                R.id.dishRemindsFragment,
                R.id.profileFragment,
                R.id.settingsFragment
            ),
            drawerLayout = binding.mainDrawerLayout
        )
        setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            binding.mainDrawerLayout.closeDrawers()
            val onScreenFragmentId = navController.currentDestination?.id
            if (onScreenFragmentId != menuItem.itemId) {
                NavigationHelper.navigate(navController, menuItem.itemId, onScreenFragmentId)
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.mainQuickSearch.apply {
                setQuery(null, false)
                clearFocus()
            }
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.mainQuickSearch.queryHint = getText(R.string.home_search_hint)
                    showMainQuickSearch(true)
                    showRecentlyViewedFab(true)
                }
                R.id.profileFragment -> {
                    showMainQuickSearch(false)
                    showRecentlyViewedFab(false)
                }
                R.id.settingsFragment -> {
                    showMainQuickSearch(false)
                    showRecentlyViewedFab(false)
                }
                R.id.advancedSearchSettingsFragment -> {
                    binding.mainQuickSearch.queryHint = getText(R.string.advanced_search_hint)
                    showMainQuickSearch(true)
                    showRecentlyViewedFab(false)
                }
                R.id.advancedSearchFragment -> {
                    binding.mainQuickSearch.queryHint = getText(R.string.advanced_search_hint)
                    showMainQuickSearch(true)
                    showRecentlyViewedFab(true)
                }
                R.id.markedFragment -> {
                    binding.mainQuickSearch.queryHint = getText(R.string.marked_search_hint)
                    showMainQuickSearch(true)
                    showRecentlyViewedFab(true)
                }
                R.id.dishRemindsFragment -> {
                    showMainQuickSearch(false)
                    showRecentlyViewedFab(false)
                }
                R.id.detailsFragment -> {
                    showMainQuickSearch(false)
                    showRecentlyViewedFab(false)
                }
                else -> throw IllegalArgumentException("Wrong NavDestination Id")
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        binding.mainAppbar.setExpanded(true)
        recentlyViewedBottomSheet.state = STATE_HIDDEN
        val onScreenFragmentId = navController.currentDestination?.id
        if (
            onScreenFragmentId == R.id.detailsFragment ||
            onScreenFragmentId == R.id.advancedSearchFragment
        ) {
            navController.popBackStack()
        } else {
            mainSearchViewClearFocus()
            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initReceiver() {
        val filter = IntentFilter().also {
            it.addAction(NAVIGATE_TO_DETAILS)
            it.addAction(NAVIGATE_TO_ADVANCED_SEARCH)
            it.addAction(REMINDER_NOTIFICATION)
        }
        registerReceiver(receiver, filter)
    }

    private fun initRecentlyViewedAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = NAVIGATE_TO_DETAILS
                    val bundle = Bundle().apply { putInt(KEY_DISH_ID, dishId) }
                    putExtra(KEY_DISH_ID, bundle)
                }
                sendBroadcast(intent)
            }
        }
        val checkedChangeListener = object : DishRecyclerAdapter.OnCheckedChangeListener {
            override fun checkedChange(dish: Dish, isChecked: Boolean) {
                if (dish.mark != isChecked) {
                    dish.mark = isChecked
                    mainActivityScope.launch { viewModel.setDishMark(dish) }
                }
            }
        }
        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != 0) mainSearchViewClearFocus()
            }
        }
        binding.recentlyViewedRecycler.apply {
            recentlyViewedAdapter =
                DishRecyclerAdapter(clickListener, checkedChangeListener).apply {
                    stateRestorationPolicy = PREVENT_WHEN_EMPTY
                }
            layoutManager = mLayoutManager
            adapter = recentlyViewedAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(PADDING_DP))
        }
    }

    private fun initContent() {
        viewModel.getRecentlyViewedDishes().subscribeOn(io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(mainThread())
            .subscribe(
                {
                    recentlyViewedAdapter.updateData(it)
                    binding.recentlyViewedRecycler.smoothScrollToPosition(FIRST_POSITION)
                    adapterIsNotEmpty = it.isNotEmpty()
                },
                { Timber.e(it, "initContent onError") }
            ).addTo(autoDisposable)
    }

    private fun initAlarms() {
        viewModel.dishAlarmsList.subscribeOn(io())
            .filter { !it.isNullOrEmpty() }
            .subscribe(
                { initDishReminds(it) },
                { Timber.e(it, "initAlarms onError") }
            ).addTo(autoDisposable)
    }

    private fun initDishReminds(dishAlarmList: List<DishAlarm>) {
        dishAlarmList.filter { it.alarmTime >= currentTimeMillis() }.forEach { dishAlarm ->
            val dishAdvancedInfo = viewModel.getSingleCashedAdvancedInfoDishFromDb(dishAlarm.id)
            dishAdvancedInfo.let {
                NotificationHelper.setDishRemind(
                    this@MainActivity,
                    it,
                    dishAlarm.alarmTime,
                    dishAlarm.localId
                )
            }
        }
    }

    fun getSearchQuery(): CharSequence? = binding.mainQuickSearch.query

    fun hideBottomSheet() {
        recentlyViewedBottomSheet.state = STATE_HIDDEN
    }

    fun mainSearchViewClearFocus() {
        binding.mainQuickSearch.clearFocus()
    }

    private fun showRecentlyViewedFab(state: Boolean) {
        binding.recentlyViewedFab.visibility = if (state && adapterIsNotEmpty) VISIBLE
        else GONE
    }

    private fun showMainQuickSearch(state: Boolean) {
        binding.mainQuickSearch.visibility = if (state) VISIBLE
        else GONE
    }

    override fun onBackPressed() {
        hideBottomSheet()
        if (
            timeOnPressed + EXIT_TIME_INTERVAL < currentTimeMillis() &&
            navController.currentDestination?.id == R.id.homeFragment
        ) {
            timeOnPressed = currentTimeMillis()
            Toast.makeText(this, resources.getText(R.string.exit_message), Toast.LENGTH_SHORT)
                .show()
        } else super.onBackPressed()
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recentlyViewedBottomSheet.state = STATE_HIDDEN
            when (intent?.action) {
                NAVIGATE_TO_DETAILS -> {
                    val onScreenFragmentId = navController.currentDestination?.id
                    NavigationHelper.navigateToDetailsFragment(
                        navController,
                        intent.getBundleExtra(KEY_DISH_ID),
                        onScreenFragmentId
                    )
                    binding.mainAppbar.setExpanded(true)
                }
                NAVIGATE_TO_ADVANCED_SEARCH -> {
                    NavigationHelper.navigateToAdvancedSearchFragment(
                        navController,
                        intent.getBundleExtra(KEY_ADVANCED_SEARCH_SETTINGS)
                    )
                }

                REMINDER_NOTIFICATION -> {
                    val bundle = intent.getBundleExtra(KEY_REMINDER_NOTIFICATION_DISH)
                    val activityStartIntent = Intent(context, MainActivity::class.java)
                    bundle?.let { activityStartIntent.putExtras(bundle) }
                    this@MainActivity.intent.putExtra(KEY_REMINDER_NOTIFICATION_DISH, bundle)
                    when (this@MainActivity.lifecycle.currentState) {
                        RESUMED -> startDishReminderDetailsFragment(intent)
                        else -> startActivity(activityStartIntent)
                    }
                }
            }
        }
    }

    private fun startDishReminderDetailsFragment(intent: Intent?) {
        val bundle = intent?.getBundleExtra(KEY_REMINDER_NOTIFICATION_DISH)
        val onScreenFragmentId = navController.currentDestination?.id
        bundle?.let {
            NavigationHelper.navigateToDetailsFragment(
                navController,
                bundle,
                onScreenFragmentId
            )
            val localId = it.getInt(KEY_DISH_LOCAL_ID, DEFAULT_VALUE_FOR_LOCAL_ID)
            if (localId != DEFAULT_VALUE_FOR_LOCAL_ID) {
                mainActivityScope.launch {
                    viewModel.deleteDishAlarmFromDb(localId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        startDishReminderDetailsFragment(intent)
        super.onNewIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        startDishReminderDetailsFragment(intent)
    }

    override fun onDestroy() {
        mainActivityScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val EXIT_TIME_INTERVAL = 2000L
        private const val MAIN_TINT_ANIM_DURATION = 200L
        private const val RECENTLY_VIEWED_FAB_ANIM_DURATION = 200L
        private const val MAIN_TINT_ALPHA_RATIO = 1.1F
        private const val FIRST_POSITION = 0
        private const val ZERO_ALPHA = 0f
        private const val ZERO_SCALE = 0f
        private const val FULL_SCALE = 1f
        private const val IS_SLIDE_FLAG = 0
        private const val DEFAULT_VALUE_FOR_LOCAL_ID = -1
    }
}