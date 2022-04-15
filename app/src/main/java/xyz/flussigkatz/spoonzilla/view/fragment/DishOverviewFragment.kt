package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html.*
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.searchmovie.view.notification.NotificationHelper
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishOverviewBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.viewmodel.DishOverviewFragmentViewModel
import java.util.*


class DishOverviewFragment : Fragment() {
    private val viewModel: DishOverviewFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentDishOverviewBinding
    private var mDishAdvancedInfo: DishAdvancedInfo? = null
    private val dishOverviewScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDishAdvancedInfo = arguments?.getParcelable(KEY_DISH)
        mDishAdvancedInfo?.let { bindData(it) }
        dishOverviewScope.launch {
            repeat(GET_DISH_REPLAY) {
                if (mDishAdvancedInfo == null) {
                    getDish()
                    delay(GET_DISH_DELAY)
                }
            }
        }
    }

    private fun getDish() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(KEY_DISH_ID)
            if (mDishAdvancedInfo == null) {
                viewModel.getDishAdvancedInfoFromDb(dishId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            if (it.isNotEmpty()) {
                                mDishAdvancedInfo = it.first()
                                bindData(it.first())
                                viewModel.getIngredientsByIdFromApi(dishId)
                                viewModel.getEquipmentsByIdFromApi(dishId)
                                viewModel.getInstructionsByIdFromApi(dishId)
                                viewModel.getNutrientByIdFromApi(dishId)
                            } else {
                                viewModel.getRecipeByIdFromApi(dishId)
                            }
                        },
                        { println("$TAG getDish onError: ${it.localizedMessage}") }
                    )
            } else bindData(mDishAdvancedInfo!!)
        }
    }

    private fun bindData(dishAdvancedInfo: DishAdvancedInfo) {
        val coast = "${dishAdvancedInfo.pricePerServing}" + DOLLAR_SYMBOL
        val cookingTime =
            "${dishAdvancedInfo.readyInMinutes}" + resources.getText(R.string.time_cooking_measure)
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val calendar = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .build()
        timePicker.addOnPositiveButtonClickListener {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            val alarmTime = calendar.timeInMillis
            if (alarmTime > System.currentTimeMillis()) {
                NotificationHelper.setDishRemind(
                    requireContext(),
                    dishAdvancedInfo,
                    alarmTime
                )
                dishOverviewScope.launch {
                    setDishRemind(alarmTime, dishAdvancedInfo)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getText(R.string.time_picker_text_past_time),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val calendarConstraints = CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraints.build())
            .setTitleText(resources.getText(R.string.date_picker_text))
            .build()
        datePicker.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            timePicker.show(parentFragmentManager, TIME_PICKER_TAG)
        }
        binding.recipeOverviewTitle.text = dishAdvancedInfo.title
        binding.recipeOverviewDishLink.text = dishAdvancedInfo.sourceUrl
        binding.recipeOverviewCoastText.text = coast
        binding.recipeOverviewLikesText.text = dishAdvancedInfo.aggregateLikes.toString()
        binding.recipeOverviewTimeCookText.text = cookingTime
        binding.overviewShareFab.apply {
            show()
            setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    val shareMessage =
                        resources.getText(R.string.share_message).toString()
                    val sourceText =
                        resources.getText(R.string.share_message_source_link).toString()
                    putExtra(
                        Intent.EXTRA_TEXT,
                        shareMessage + dishAdvancedInfo.title +
                                sourceText + dishAdvancedInfo.sourceUrl
                    )
                    type = TEXT_TYPE
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        binding.overviewReminderFab.apply {
            show()
            setOnClickListener {
                datePicker.let {
                    if (!it.isAdded) datePicker.show(parentFragmentManager, DATE_PICKER_TAG)
                }
            }
        }
        binding.overviewMarkFab.apply {
            if (dishAdvancedInfo.mark) setImageResource(R.drawable.ic_marked)
            show()
            setOnClickListener {
                dishAdvancedInfo.mark = !dishAdvancedInfo.mark
                if (dishAdvancedInfo.mark) setImageResource(R.drawable.ic_marked)
                else setImageResource(R.drawable.ic_unmarked_fab)
                dishOverviewScope.launch { viewModel.setDishMark(dishAdvancedInfo) }
            }
        }
        binding.root.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY != ZERO) {
                    binding.overviewShareFab.hide()
                    binding.overviewMarkFab.hide()
                    binding.overviewReminderFab.hide()
                } else {
                    binding.overviewShareFab.show()
                    binding.overviewMarkFab.show()
                    binding.overviewReminderFab.show()
                }
            }
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.recipeOverviewDescription.text =
                fromHtml(dishAdvancedInfo.summary, FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            binding.recipeOverviewDescription.text = fromHtml(dishAdvancedInfo.summary)
        }
        if (dishAdvancedInfo.cheap) {
            binding.recipeOverviewCoast.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cheap,
                    null
                )
            )
        }
        val callbackPicasso = object : Callback {
            override fun onSuccess() {
                binding.recipeOverviewDishImage.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun onError(e: Exception?) {
                println("$TAG callbackPicasso onError: ${e?.localizedMessage}")
            }

        }
        Picasso.get()
            .load(dishAdvancedInfo.image)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_food)
            .error(R.drawable.ic_food)
            .into(binding.recipeOverviewDishImage, callbackPicasso)
    }

    private fun setDishRemind(alarmTime: Long, dishAdvancedInfo: DishAdvancedInfo) {
        val dishAlarm = DishAlarm(
            id = dishAdvancedInfo.id,
            title = dishAdvancedInfo.title,
            image = dishAdvancedInfo.image,
            alarmTime = alarmTime,
        )
        viewModel.putDishAlarmToDb(dishAlarm)
    }


    override fun onDestroy() {
        dishOverviewScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "DishOverviewFragment"
        private const val DATE_PICKER_TAG = "datePicker"
        private const val TIME_PICKER_TAG = "timePicker"
        private const val TEXT_TYPE = "text/plain"
        private const val DOLLAR_SYMBOL = "$"
        private const val GET_DISH_DELAY = 2000L
        private const val GET_DISH_REPLAY = 5
        private const val ZERO = 0
    }

}