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
    private val searchPublishSubject: PublishSubject<String>,
    private val loadingState: BehaviorSubject<Boolean>,
    private val preferences: PreferenceProvider,
    private val retrofitService: SpoonacularApi
) {

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

    fun getRandomRecipeFromApi(
        number: Int,
        tags: String,
        clearDb: Boolean) {
        retrofitService.getRandomRecipes(
            limitLicense = false,
            tags = tags,
            number = number,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .filter { !it.recipes.isNullOrEmpty() }
            .map { DishConverter.convertRandomRecipeFromApi(it) }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) {
                        var isClear: Boolean
                        do isClear = repository.clearDb()
                        while (!isClear)
                    }
                    repository.putFilmToDB(it)
                },
                { println("$TAG getRandomRecipeFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRecipeFromDb(): Observable<List<Dish>> {
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
        offset: Int?,
        number: Int?,
        clearDb: Boolean
    ) {
        retrofitService.getSearchedRecipes(
            query = query,
            offset = offset,
            number = number,
            limitLicense = false,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .map { DishConverter.convertSearchedRecipeBasicInfoFromApi(it) }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) {
                        var isClear: Boolean
                        do isClear = repository.clearDb()
                        while (!isClear)
                    }
                    repository.putFilmToDB(it)
                },
                { println("$TAG getSearchedRecipesFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getAdvancedSearchedRecipes(
        query: String?,
        cuisine: String?,
        diet: String?,
        intolerances: String?,
        type: String?,
        instructionsRequired: Boolean?,
        offset: Int?,
        number: Int?,
        clearDb: Boolean
    ) {
        retrofitService.getAdvancedSearchedRecipes(
            query = query,
            cuisine = cuisine,
            diet = diet,
            intolerances = intolerances,
            type = type,
            instructionsRequired = instructionsRequired,
            offset = offset,
            number = number,
            limitLicense = false,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .map { DishConverter.convertSearchedRecipeBasicInfoFromApi(it) }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) {
                        var isClear: Boolean
                        do isClear = repository.clearDb()
                        while (!isClear)
                    }
                    repository.putFilmToDB(it)
                },
                { println("$TAG getAdvancedSearchedRecipes onError: ${it.localizedMessage}") }
            )
    }

    fun putSearchQuery(query: String) {
        searchPublishSubject.onNext(query.lowercase().trim())
    }

    fun getSearchPublishSubject() = searchPublishSubject

    fun putDialogItemsToPreference(key: String, set: Set<String>) {
        preferences.putDialogItems(key, set)
    }

    fun getSearchSettings(key: String): MutableSet<String>? {
        return preferences.getDialogItems(key)
    }

    fun getRefreshState() = loadingState

    fun serProfile(profile: String?) {
        preferences.setProfile(profile)
    }

    fun getProfile() = preferences.getProfile()

    fun saveAdvancedSearchSwitchState(key: String, state: Boolean) {
        preferences.saveAdvancedSearchSwitchState(key, state)
    }

    fun getAdvancedSearchSwitchState(key: String) = preferences.getAdvancedSearchSwitchState(key)


    companion object {
        private const val TAG = "Interactor"
    }

}