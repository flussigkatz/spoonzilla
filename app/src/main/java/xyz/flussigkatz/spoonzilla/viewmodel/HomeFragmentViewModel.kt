package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.data.entity.Dish
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Inject

class HomeFragmentViewModel : ViewModel() {
    val dishList: Observable<List<Dish>>
    val searchPublishSubject: PublishSubject<String?>

    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
        dishList = interactor.getRandomRecipeFromApi(10)
        searchPublishSubject = interactor.getSearchPublishSubject()
    }

    fun getSearchedRecipes(query: String): Observable<List<Dish>> {
        return interactor.getSearchedRecipesFromApi(
            query = query,
            offset = null,
            number = 5
        )
    }

}