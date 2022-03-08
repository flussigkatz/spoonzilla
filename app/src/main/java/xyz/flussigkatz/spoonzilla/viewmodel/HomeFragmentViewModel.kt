package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val dishList: Observable<List<Dish>>
    val searchPublishSubject: PublishSubject<String?>
    val refreshState: BehaviorSubject<Boolean>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        getRandomRecipe(10)
        dishList = interactor.getRandomRecipeFromDb()
        refreshState = interactor.getRefreshState()
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun getSearchedRecipes(query: String, offset: Int, number: Int): Observable<List<Dish>> {
        return interactor.getSearchedRecipesFromApi(
            query = query,
            offset = offset,
            number = number
        )
    }

    fun getRandomRecipe(number: Int) {
        interactor.getRandomRecipeFromApi(number)
    }

}