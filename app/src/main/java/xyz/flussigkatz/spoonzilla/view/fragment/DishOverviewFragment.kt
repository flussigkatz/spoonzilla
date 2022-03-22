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
import xyz.flussigkatz.spoonzilla.R
import xyz.flussigkatz.spoonzilla.data.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishOverviewBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH_ID
import xyz.flussigkatz.spoonzilla.viewmodel.DishOverviewFragmentViewModel


class DishOverviewFragment : Fragment() {
    private lateinit var binding: FragmentDishOverviewBinding
    private var dishAdvancedInfo: DishAdvancedInfo? = null
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
        getDish()
    }

    private fun getDish() {
        arguments?.let { bundle ->
            if (dishAdvancedInfo == null) {
                viewModel.getRecipeByIdFromApi(bundle.getInt(KEY_DISH_ID))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            dishAdvancedInfo = it
                            bind(it)
                        },
                        { println("$TAG getDish onError: ${it.localizedMessage}") }
                    )
            } else bind(dishAdvancedInfo!!)
        }
    }

    private fun bind(dishAdvancedInfo: DishAdvancedInfo) {
        binding.recipeOverviewDishLink.text = dishAdvancedInfo.sourceUrl
        binding.recipeOverviewCoastText.text = dishAdvancedInfo.pricePerServing.toString()
        binding.recipeOverviewLikesText.text = dishAdvancedInfo.aggregateLikes.toString()
        binding.recipeOverviewTimeCookText.text = dishAdvancedInfo.readyInMinutes.toString()
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

    companion object {
        private const val TAG = "DishOverviewFragment"
    }

}