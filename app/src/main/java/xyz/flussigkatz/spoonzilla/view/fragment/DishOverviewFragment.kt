package xyz.flussigkatz.spoonzilla.view.fragment

import android.content.Intent
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
            } else bindData(dishAdvancedInfo!!)
        }
    }

    private fun bindData(dishAdvancedInfo: DishAdvancedInfo) {
        val coast = "${dishAdvancedInfo.pricePerServing}$"
        val cookingTime = "${dishAdvancedInfo.readyInMinutes}min"
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
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Check out this recipe I found on the Spoonzilla app. " +
                                "${dishAdvancedInfo.title}. " +
                                "Source link: ${dishAdvancedInfo.sourceUrl}"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.root.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY != 0) {
                    binding.overviewShareFab.hide()
                    binding.overviewMarkFab.hide()
                } else {
                    binding.overviewShareFab.show()
                    binding.overviewMarkFab.show()
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