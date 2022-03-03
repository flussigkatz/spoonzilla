package xyz.flussigkatz.spoonzilla.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.remote.SpoonacularApi
import xyz.flussigkatz.spoonzilla.ApiKey
import xyz.flussigkatz.spoonzilla.data.entity.Dish
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.util.DishConverter

class Interactor(
    private val searchPublishSubject: PublishSubject<String?>,
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

    fun getRandomRecipeFromApi(number: Int): Observable<List<Dish>> {
        return retrofitService.getRandomRecipes(
            limitLicense = false,
            tags = tags.joinToString(),
            number = number,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .filter { !it.recipes.isNullOrEmpty() }
            .map { DishConverter.convertRandomRecipeFromApi(it) }
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
        cuisine: List<String>,
        excludeCuisine: List<String>,
        diet: List<String>,
        intolerances: List<String>,
        type: List<String>,
        instructionsRequired: Boolean,
        tags: List<String>,
        offset: Int?,
        number: Int?,
    ): Observable<List<Dish>> {
        return retrofitService.getSearchedRecipes(
            query = query,
            cuisine = cuisine.joinToString(),
            excludeCuisine = excludeCuisine.joinToString(),
            diet = diet.joinToString(),
            intolerances = intolerances.joinToString(),
            type = type.joinToString(),
            instructionsRequired = instructionsRequired,
            tags = tags.joinToString(),
            offset = offset,
            number = number,
            limitLicense = false,
            apiKey = ApiKey.API_KEY
        ).subscribeOn(Schedulers.io())
            .map { DishConverter.convertSearchedRecipeFromApi(it) }
    }

    fun putSearchQuery(query: String?) {
        searchPublishSubject.onNext(query)
    }

    fun getSearchPublishSubject() = searchPublishSubject

    companion object {
        private const val TAG = "Interactor"
    }

}