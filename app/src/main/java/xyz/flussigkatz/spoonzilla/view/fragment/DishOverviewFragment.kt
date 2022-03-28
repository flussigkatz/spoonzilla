package xyz.flussigkatz.spoonzilla.view.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishOverviewBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.viewmodel.DishOverviewFragmentViewModel


class DishOverviewFragment : Fragment() {
    private lateinit var binding: FragmentDishOverviewBinding
    private var dishAdvancedInfo: DishAdvancedInfo? = null
    private val dishOverviewScope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DishOverviewFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dishOverviewScope.launch {
            repeat(GET_DISH_REPLAY) {
                if (dishAdvancedInfo == null) {
                    getDish()
                    delay(GET_DISH_DELAY)
                }
            }
        }
    }

    private fun getDish() {
        arguments?.let { bundle ->
            val dishId = bundle.getInt(KEY_DISH_ID)
            if (dishAdvancedInfo == null) {
                    viewModel.getDishAdvancedInfoFromDb(dishId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                if (it.isNotEmpty()) {
                                    dishAdvancedInfo = it.first()
                                    bind(it.first())
                                    viewModel.getIngredientsByIdFromApi(dishId)
                                    viewModel.getEquipmentsByIdFromApi(dishId)
                                    viewModel.getInstructionsByIdFromApi(dishId)
                                }
                                else {
                                    viewModel.getRecipeByIdFromApi(dishId)
                                }
                            },
                            { println("$TAG getDish onError: ${it.localizedMessage}") }
                        )
            } else bind(dishAdvancedInfo!!)
        }
    }

    private fun bind(dishAdvancedInfo: DishAdvancedInfo) {
        binding.recipeOverviewTitle.text = dishAdvancedInfo.title
        binding.recipeOverviewDishLink.text = dishAdvancedInfo.sourceUrl
        binding.recipeOverviewCoastText.text = dishAdvancedInfo.pricePerServing.toString()
        binding.recipeOverviewLikesText.text = dishAdvancedInfo.aggregateLikes.toString()
        binding.recipeOverviewTimeCookText.text = dishAdvancedInfo.readyInMinutes.toString()
        binding.overviewMarkCheckBox.apply {
            isChecked = dishAdvancedInfo.mark
            setOnCheckedChangeListener { _, isChecked ->
                if (dishAdvancedInfo.mark != isChecked) {
                    dishOverviewScope.launch { viewModel.setDishMark(dishAdvancedInfo, isChecked) }
                    dishAdvancedInfo.mark = isChecked
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.recipeOverviewDescription.text =
                Html.fromHtml(dishAdvancedInfo.summary, Html.FROM_HTML_MODE_LEGACY)
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

    override fun onDestroy() {
        dishOverviewScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "DishOverviewFragment"
        private const val GET_DISH_DELAY = 2000L
        private const val GET_DISH_REPLAY = 5
    }

}