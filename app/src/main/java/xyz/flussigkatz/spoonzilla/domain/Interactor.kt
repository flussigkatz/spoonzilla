package xyz.flussigkatz.spoonzilla.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.remote.SpoonacularApi
import xyz.flussigkatz.spoonzilla.ApiKey
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.spoonzilla.data.db.MainRepository
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.util.DishConverter

class Interactor(
    private val repository: MainRepository,
    private val searchPublishSubject: PublishSubject<String?>,
    private val refreshState: BehaviorSubject<Boolean>,
    private val preferences: PreferenceProvider,
    private val retrofitService: SpoonacularApi
) {

    private val tags: List<String> = listOf("")

    fun getRecipeByIdFromApi(id: Int) {
        retrofitService.getRecipeById(
            id = id,
            includeNutrition = true,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .subscribe(
                { println(it.title) },
                { println("$TAG getRecipeByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRandomRecipeFromApi(number: Int) {
        retrofitService.getRandomRecipes(
            limitLicense = false,
            tags = tags.joinToString(),
            number = number,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .filter { !it.recipes.isNullOrEmpty() }
            .map { DishConverter.convertRandomRecipeFromApi(it) }
            .doOnSubscribe { refreshState.onNext(true) }
            .doOnComplete { refreshState.onNext(false) }
            .doOnError { refreshState.onNext(false) }
            .subscribe(
                {
                    var deleteItems: Int
                    do deleteItems = repository.clearDB()
                    while (deleteItems != 0)
                    repository.putFilmToDB(it)
                },
                { println("$TAG getRandomRecipeFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRandomRecipeFromDb(): Observable<List<Dish>> {
        return repository.getAllFilmsFromDB()
    }

    fun getSimilarRecipesFromApi(id: Int) {
        retrofitService.getSimilarRecipes(
            id = id,
            number = 4,
            limitLicense = false,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .subscribe(
                { onNext -> onNext.forEach { println(it.title) } },
                { println("$TAG getSimilarRecipesFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRecipeTasteFromApi(id: Int) {
        retrofitService.getRecipeTaste(
            id = id,
            normalize = true,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .subscribe(
                { println(it) },
                { println("$TAG getRecipeTasteFromApi onError: ${it.localizedMessage}") }
            )

    }

    fun getSearchedRecipesFromApi(
        query: String,
        offset: Int,
        number: Int,
    ): Observable<List<Dish>> {
        return retrofitService.getSearchedRecipes(
            query = query,
            offset = offset,
            number = number,
            limitLicense = false,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .map { DishConverter.convertSearchedRecipeBasicInfoFromApi(it) }
    }

    fun putSearchQuery(query: String?) {
        searchPublishSubject.onNext(query)
    }

    fun getSearchPublishSubject() = searchPublishSubject

    fun putCuisineToPreference(cuisine: Set<String>) {
        preferences.putCuisine(cuisine)
    }

    fun getCuisineFromPreference(): MutableSet<String>? = preferences.getCuisine()

    fun getRefreshState() = refreshState

    companion object {
        private const val TAG = "Interactor"
    }

}