package xyz.flussigkatz.spoonzilla.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.spoonzilla.databinding.FragmentDishIngredientsBinding
import xyz.flussigkatz.spoonzilla.util.AppConst.HIDE_KEYBOARD_FLAG
import xyz.flussigkatz.spoonzilla.util.AppConst.IS_SCROLL_FLAG
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DISH
import xyz.flussigkatz.spoonzilla.view.rv_adapter.IngredientsRecyclerAdapter
import xyz.flussigkatz.spoonzilla.viewmodel.DishIngredientsFragmentViewModel


class DishIngredientsFragment : Fragment() {
    private lateinit var binding: FragmentDishIngredientsBinding
    private lateinit var ingredientsAdapter: IngredientsRecyclerAdapter
    private val ingredientsFragmentCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val viewModel: DishIngredientsFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getIngredients()
    }

    private fun getIngredients() {
        arguments?.getParcelable<DishAdvancedInfo?>(KEY_DISH)?.let { dishAdvancedInfo ->
            initServingsCount(dishAdvancedInfo.servings)
            viewModel.getIngredientsByIdFromDb(dishAdvancedInfo.id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { initIngredientAdapter(dishAdvancedInfo.servings) }
                .subscribe(
                    { ingredientsAdapter.updateItems(it) },
                    { Timber.d(it, "getIngredients onError") }
                )
            ingredientsFragmentCoroutineScope.launch {
                val ingredientsIsEmpty =
                    viewModel.getIngredientsToListByIdFromDb(dishAdvancedInfo.id).isEmpty()
                if (ingredientsIsEmpty) viewModel.getIngredientsByIdFromApi(dishAdvancedInfo.id)
                return@launch
            }
        }
    }

    private fun initIngredientAdapter(servings: Int) {
        val scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != IS_SCROLL_FLAG) {
                    binding.servingsCount.apply {
                        clearFocus()
                        hideSoftKeyboard(this)
                    }
                }
            }
        }
        binding.ingredientsRecycler.apply {
            ingredientsAdapter = IngredientsRecyclerAdapter(servings)
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(scrollListener)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, HIDE_KEYBOARD_FLAG)
    }

    private fun initServingsCount(servings: Int) {
        binding.servingsCount.setText(servings.toString())
        binding.servingsCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) ingredientsAdapter.updateServingsCount(s.toString().toInt())
            }
        })
        val inputFilter = InputFilter { source, _, _, dest, _, _ ->
            if (!dest.isNullOrBlank()) {
                val formatSource = if (source.isDigitsOnly()) source else ""
                val intValue = (dest.toString() + formatSource.toString()).toInt()
                if (intValue in 0..999) source else ""
            } else source
        }
        binding.servingsCount.filters = arrayOf(inputFilter)
    }

    override fun onDestroy() {
        ingredientsFragmentCoroutineScope.cancel()
        super.onDestroy()
    }
}