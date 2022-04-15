package xyz.flussigkatz.spoonzilla.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.ActivityMainBinding
import xyz.flussigkatz.spoonzilla.util.AppConst
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_ADVANCED_SEARCH_SETTINGS
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_REMINDER_NOTIFICATION_DISH
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_ADVANCED_SEARCH_ACTION
import xyz.flussigkatz.spoonzilla.util.AppConst.NAVIGATE_TO_DETAILS_ACTION
import xyz.flussigkatz.spoonzilla.util.AppConst.REMINDER_NOTIFICATION_DISH_ACTION
import xyz.flussigkatz.spoonzilla.util.AutoDisposable
import xyz.flussigkatz.spoonzilla.util.NavigationHelper
import xyz.flussigkatz.spoonzilla.util.addTo
import xyz.flussigkatz.spoonzilla.view.rv_adapter.DishRecyclerAdapter
import xyz.flussigkatz.spoonzilla.view.rv_adapter.rv_decoration.SpacingItemDecoration
import xyz.flussigkatz.spoonzilla.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private val receiver = Receiver()
    private var adapterIsNotEmpty = false
    private lateinit var recentlyViewedAdapter: DishRecyclerAdapter
    private lateinit var recentlyViewedBottomSheet: BottomSheetBehavior<ConstraintLayout>
    private val autoDisposable = AutoDisposable()
    private val mainActivityFragmentScope = CoroutineScope(Dispatchers.IO)
    private var timeOnPressed = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDisposable.bindTo(lifecycle)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        initQuickSearch()
        initReceiver()
        initRecentlyViewedBottomSheet()
        initRecentlyViewedAdapter()
        initContent()
    }

    private fun initRecentlyViewedBottomSheet() {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0) binding.mainTint.alpha = slideOffset / MAIN_TINT_ALPHA_RATIO
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.recentlyViewedLabel.apply {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            text = resources.getText(R.string.recently_viewed_label_title_expanded)
                            binding.recentlyViewedFab.animate()
                                .scaleX(0f)
                                .scaleY(0f)
                                .setDuration(RECENTLY_VIEWED_FAB_ANIM_DURATION)
                                .start()
                        }
                        BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.recentlyViewedFab.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(RECENTLY_VIEWED_FAB_ANIM_DURATION)
                                .start()
                            binding.mainTint.animate()
                                .alpha(0F)
                                .setDuration(MAIN_TINT_ANIM_DURATION)
                                .start()
                            text = resources.getText(R.string.recently_viewed_label_title)
                        }
                        else -> {}
                    }
                }
            }
        }
        recentlyViewedBottomSheet = BottomSheetBehavior.from(binding.mainRecentlyViewed).apply {
            maxHeight = resources.getDimension(R.dimen.main_recently_viewed_height).toInt()
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(bottomSheetCallback)
        }
        binding.recentlyViewedFab.setOnClickListener {
            mainSearchViewClearFocus()
            recentlyViewedBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun initQuickSearch() {
        binding.mainQuickSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.mainAppbar.setExpanded(true)
            recentlyViewedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
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
        navController = Navigation.findNavController(this, R.id.main_nav_host_fragment)
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
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

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
        recentlyViewedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
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
            it.addAction(NAVIGATE_TO_DETAILS_ACTION)
            it.addAction(NAVIGATE_TO_ADVANCED_SEARCH_ACTION)
            it.addAction(REMINDER_NOTIFICATION_DISH_ACTION)
        }
        registerReceiver(receiver, filter)
    }

    private fun initRecentlyViewedAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        val clickListener = object : DishRecyclerAdapter.OnItemClickListener {
            override fun click(dishId: Int) {
                val intent = Intent().apply {
                    action = NAVIGATE_TO_DETAILS_ACTION
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
                    mainActivityFragmentScope.launch { viewModel.setDishMark(dish) }
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
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
            layoutManager = mLayoutManager
            adapter = recentlyViewedAdapter
            addOnScrollListener(scrollListener)
            addItemDecoration(SpacingItemDecoration(AppConst.PADDING_DP))
        }
    }

    private fun initContent() {
        viewModel.getRecentlyViewedDishes().subscribeOn(Schedulers.io())
            .filter { !it.isNullOrEmpty() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    recentlyViewedAdapter.updateData(it)
                    binding.recentlyViewedRecycler.smoothScrollToPosition(0)
                    adapterIsNotEmpty = it.isNotEmpty()
                },
                { println("$TAG initContent onError: ${it.localizedMessage}") }
            ).addTo(autoDisposable)
    }

    fun hideBottomSheet() {
        recentlyViewedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun mainSearchViewClearFocus() {
        binding.mainQuickSearch.clearFocus()
    }

    private fun showRecentlyViewedFab(state: Boolean) {
        binding.recentlyViewedFab.visibility = if (state && adapterIsNotEmpty) View.VISIBLE
        else View.GONE
    }

    private fun showMainQuickSearch(state: Boolean) {
        binding.mainQuickSearch.visibility = if (state) View.VISIBLE
        else View.GONE
    }

    override fun onBackPressed() {
        hideBottomSheet()
        if (
            timeOnPressed + EXIT_TIME_INTERVAL < System.currentTimeMillis() &&
            navController.currentDestination?.id == R.id.homeFragment
        ) {
            timeOnPressed = System.currentTimeMillis()
            Toast.makeText(this, resources.getText(R.string.exit_message), Toast.LENGTH_SHORT)
                .show()
        } else super.onBackPressed()
    }

    private inner class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recentlyViewedBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            when (intent?.action) {
                NAVIGATE_TO_DETAILS_ACTION -> {
                    val onScreenFragmentId = navController.currentDestination?.id
                    NavigationHelper.navigateToDetailsFragment(
                        navController,
                        intent.getBundleExtra(KEY_DISH_ID),
                        onScreenFragmentId
                    )
                    binding.mainAppbar.setExpanded(true)
                }
                NAVIGATE_TO_ADVANCED_SEARCH_ACTION -> {
                    NavigationHelper.navigateToAdvancedSearchFragment(
                        navController,
                        intent.getBundleExtra(KEY_ADVANCED_SEARCH_SETTINGS)
                    )
                }

                REMINDER_NOTIFICATION_DISH_ACTION -> {
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
        mainActivityFragmentScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val EXIT_TIME_INTERVAL = 2000L
        private const val MAIN_TINT_ANIM_DURATION = 200L
        private const val RECENTLY_VIEWED_FAB_ANIM_DURATION = 200L
        private const val MAIN_TINT_ALPHA_RATIO = 1.1F
    }
}