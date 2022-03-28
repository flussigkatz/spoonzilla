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
import xyz.flussigkatz.spoonzilla.util.AppConst.PAGINATION_NUMBER_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.AppConst.TOTAL_NUMBER_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.Converter
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
            interactor.getSearchSettings(KEY_CUISINE_FROM_PROFILE)?.joinToString().orEmpty()
        val diet =
            interactor.getSearchSettings(KEY_DIET_FROM_PROFILE)?.joinToString().orEmpty()
        val intolerances =
            interactor.getSearchSettings(KEY_INTOLERANCE_FROM_PROFILE)?.joinToString().orEmpty()
        val tags = cuisine + diet + intolerances
        interactor.getRandomRecipeFromApi(TOTAL_NUMBER_ELEMENTS, tags, true)
    }

    fun doRandomRecipePagination() {
        val cuisine =
            interactor.getSearchSettings(KEY_CUISINE_FROM_PROFILE)?.joinToString().orEmpty()
        val diet =
            interactor.getSearchSettings(KEY_DIET_FROM_PROFILE)?.joinToString().orEmpty()
        val intolerances =
            interactor.getSearchSettings(KEY_INTOLERANCE_FROM_PROFILE)?.joinToString().orEmpty()
        val tags = cuisine + diet + intolerances
        interactor.getRandomRecipeFromApi(PAGINATION_NUMBER_ELEMENTS, tags, false)
    }

    fun setDishMark(dish: Dish, isChecked: Boolean) {
        if (isChecked) interactor.putMarkedDishToDB(Converter.convertDishToDishMarked(dish))
        else interactor.deleteMarkedDishFromDb(dish.id)
        interactor.updateDish(dish)
    }

}