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
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_MEAL_TYPE_FROM_PROFILE
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_PERSONAL_PREFERENCES
import xyz.flussigkatz.spoonzilla.util.AppConst.PAGINATION_NUMBER_ELEMENTS
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val dishList: Observable<List<Dish>>
    val searchPublishSubject: PublishSubject<String>
    val loadingState: BehaviorSubject<Boolean>

    init {
        App.instance.dagger.inject(this)
        getPopularRecipe()
        dishList = interactor.getDishesFromDb()
        loadingState = interactor.getRefreshState()
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun getSearchedRecipes(query: String) {
        interactor.getSearchedRecipesFromApi(
            query = query,
            cuisine = null,
            diet = null,
            intolerances = null,
            type = null,
            sort = null,
            offset = null,
            number = PAGINATION_NUMBER_ELEMENTS,
            clearDb = true
        )
    }

    fun doSearchedRecipesPagination(query: String, offset: Int) {
        interactor.getSearchedRecipesFromApi(
            query = query,
            cuisine = null,
            diet = null,
            intolerances = null,
            type = null,
            sort = null,
            offset = offset,
            number = PAGINATION_NUMBER_ELEMENTS,
            clearDb = false
        )
    }

    fun getPopularRecipe() {
        var cuisineKey = KEY_EMPTY
        var dietKey = KEY_EMPTY
        var intolerancesKey = KEY_EMPTY
        var typeKey = KEY_EMPTY
        interactor.getPersonalPreferencesSwitchState(KEY_PERSONAL_PREFERENCES).apply {
            if (this) {
                cuisineKey = KEY_CUISINE_FROM_PROFILE
                dietKey = KEY_DIET_FROM_PROFILE
                intolerancesKey = KEY_INTOLERANCE_FROM_PROFILE
                typeKey = KEY_MEAL_TYPE_FROM_PROFILE
            }
        }
        val cuisine = interactor.getSearchSettings(cuisineKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val diet = interactor.getSearchSettings(dietKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val intolerances = interactor.getSearchSettings(intolerancesKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val type = interactor.getSearchSettings(typeKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val sort = interactor.getHomePageContent()
        interactor.getSearchedRecipesFromApi(
            query = null,
            cuisine = cuisine,
            diet = diet,
            intolerances = intolerances,
            type = type,
            sort = sort,
            offset = null,
            number = TOTAL_NUMBER_ELEMENTS,
            clearDb = true
        )
    }

    fun doPopularRecipePagination(offset: Int) {
        var cuisineKey = KEY_EMPTY
        var dietKey = KEY_EMPTY
        var intolerancesKey = KEY_EMPTY
        var typeKey = KEY_EMPTY
        interactor.getPersonalPreferencesSwitchState(KEY_PERSONAL_PREFERENCES).apply {
            if (this) {
                cuisineKey = KEY_CUISINE_FROM_PROFILE
                dietKey = KEY_DIET_FROM_PROFILE
                intolerancesKey = KEY_INTOLERANCE_FROM_PROFILE
                typeKey = KEY_MEAL_TYPE_FROM_PROFILE
            }
        }
        val cuisine = interactor.getSearchSettings(cuisineKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val diet = interactor.getSearchSettings(dietKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val intolerances = interactor.getSearchSettings(intolerancesKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val type = interactor.getSearchSettings(typeKey)
            ?.joinToString(separator = STRING_SEPARATOR)
        val sort = interactor.getHomePageContent()
        interactor.getSearchedRecipesFromApi(
            query = null,
            cuisine = cuisine,
            diet = diet,
            intolerances = intolerances,
            type = type,
            sort = sort,
            offset = offset,
            number = PAGINATION_NUMBER_ELEMENTS,
            clearDb = false
        )
    }

    fun setDishMark(dish: Dish) {
        interactor.setDishMark(dish)
    }

    companion object {
        private const val TOTAL_NUMBER_ELEMENTS = 10
        private const val STRING_SEPARATOR = " "
        private const val KEY_EMPTY = ""
    }

}