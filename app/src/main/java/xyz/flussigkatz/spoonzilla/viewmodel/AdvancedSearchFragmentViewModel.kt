package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst.KEY_INSTRUCTIONS_SWITCH
import xyz.flussigkatz.spoonzilla.util.AppConst.PAGINATION_NUMBER_ELEMENTS
import javax.inject.Inject

class AdvancedSearchFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val searchPublishSubject: PublishSubject<String>
    val dishList: Observable<List<Dish>>
    val loadingState: BehaviorSubject<Boolean>

    init {
        App.instance.dagger.inject(this)
        searchPublishSubject = interactor.getSearchPublishSubject()
        dishList = interactor.getRecipeFromDb()
        loadingState = interactor.getRefreshState()
    }

    fun getAdvancedSearchedRecipes(
        query: String?,
        keyCuisine: String,
        keyDiet: String,
        keyIntolerance: String,
        keyMeatType: String

    ) {
        val cuisine = interactor.getSearchSettings(keyCuisine)
        val diet = interactor.getSearchSettings(keyDiet)
        val intolerances = interactor.getSearchSettings(keyIntolerance)
        val type = interactor.getSearchSettings(keyMeatType)
        val instructionsRequired = interactor.getAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH)
        interactor.getAdvancedSearchedRecipes(
            query = query,
            cuisine = cuisine?.joinToString(),
            diet = diet?.joinToString(),
            intolerances = intolerances?.joinToString(),
            type = type?.joinToString(),
            instructionsRequired = instructionsRequired,
            offset = null,
            number = null,
            clearDb = true
        )
    }

    fun doSearchedRecipesPagination(
        query: String?,
        offset: Int,
        keyCuisine: String,
        keyDiet: String,
        keyIntolerance: String,
        keyMeatType: String
    ) {
        val cuisine = interactor.getSearchSettings(keyCuisine)
        val diet = interactor.getSearchSettings(keyDiet)
        val intolerances = interactor.getSearchSettings(keyIntolerance)
        val type = interactor.getSearchSettings(keyMeatType)
        val instructionsRequired = interactor.getAdvancedSearchSwitchState(KEY_INSTRUCTIONS_SWITCH)
        interactor.getAdvancedSearchedRecipes(
            query = query,
            cuisine = cuisine?.joinToString(),
            diet = diet?.joinToString(),
            intolerances = intolerances?.joinToString(),
            type = type?.joinToString(),
            instructionsRequired = instructionsRequired,
            offset = offset,
            number = PAGINATION_NUMBER_ELEMENTS,
            clearDb = false
        )
    }
}