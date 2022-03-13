package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.annotation.Nullable
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst.PAGINATION_NUMBER_ELEMENTS
import xyz.flussigkatz.spoonzilla.util.AppConst.TOTAL_NUMBER_ELEMENTS
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val dishList: Observable<List<Dish>>
    val searchPublishSubject: PublishSubject<String>
    val loadingState: BehaviorSubject<Boolean>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getRandomRecipe()
        dishList = interactor.getRandomRecipeFromDb()
        loadingState = interactor.getRefreshState()
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun getSearchedRecipes(query: String, offset: Int?) {
        interactor.getSearchedRecipesFromApi(
            query = query,
            offset = offset,
            number = TOTAL_NUMBER_ELEMENTS,
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
        interactor.getRandomRecipeFromApi(TOTAL_NUMBER_ELEMENTS, true)
    }

    fun doRandomRecipePagination() {
        interactor.getRandomRecipeFromApi(PAGINATION_NUMBER_ELEMENTS, false)
    }

}