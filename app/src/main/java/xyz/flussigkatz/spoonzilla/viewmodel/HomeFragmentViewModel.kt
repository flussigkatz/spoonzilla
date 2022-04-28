package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_CUISINE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_DIET_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INTOLERANCE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_PERSONAL_PREFERENCES
import xyz.flussigkatz.spoonzilla.util.AppConst.PAGINATION_NUMBER_ELEMENTS
import java.util.*
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val dishList: Observable<List<Dish>>
    val searchPublishSubject: PublishSubject<String>
    val loadingState: BehaviorSubject<Boolean>

    init {
        App.instance.dagger.inject(this)
        getRandomRecipe()
        dishList = interactor.getDishesFromDb()
        loadingState = interactor.getRefreshState()
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun getSearchedRecipes(query: String) {
        interactor.getSearchedRecipesFromApi(
            query = query,
            offset = null,
            number = null,
            true
        )
    }

    fun doSearchedRecipesPagination(query: String, offset: Int) {
        interactor.getSearchedRecipesFromApi(
            query = query,
            offset = offset,
            number = PAGINATION_NUMBER_ELEMENTS,
            false
        )
    }

    fun getRandomRecipe() {
        val cuisine =
            interactor.getSearchSettings(KEY_CUISINE_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR)
                .orEmpty()
        val diet =
            interactor.getSearchSettings(KEY_DIET_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR)
                .orEmpty()
        val intolerances =
            interactor.getSearchSettings(KEY_INTOLERANCE_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR).orEmpty()
        val tags = if (interactor.getPersonalPreferencesSwitchState(KEY_PERSONAL_PREFERENCES)) {
            "${cuisine.trim()} ${diet.trim()} ${intolerances.trim()}"
        } else String()
        interactor.getRandomRecipeFromApi(
            TOTAL_NUMBER_ELEMENTS,
            tags.lowercase(Locale.getDefault()).trim(),
            true
        )
    }

    fun doRandomRecipePagination() {
        val cuisine =
            interactor.getSearchSettings(KEY_CUISINE_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR)
                .orEmpty()
        val diet =
            interactor.getSearchSettings(KEY_DIET_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR)
                .orEmpty()
        val intolerances =
            interactor.getSearchSettings(KEY_INTOLERANCE_FROM_PROFILE)
                ?.joinToString(separator = STRING_SEPARATOR).orEmpty()
        val tags = if (interactor.getPersonalPreferencesSwitchState(KEY_PERSONAL_PREFERENCES)) {
            "${cuisine.trim()} ${diet.trim()} ${intolerances.trim()}"
        } else String()
        interactor.getRandomRecipeFromApi(
            PAGINATION_NUMBER_ELEMENTS,
            tags.lowercase(Locale.getDefault()).trim(),
            false
        )
    }

    fun setDishMark(dish: Dish) {
        interactor.setDishMark(dish)
    }

    companion object {
        private const val TOTAL_NUMBER_ELEMENTS = 10
        private const val STRING_SEPARATOR = " "
    }

}