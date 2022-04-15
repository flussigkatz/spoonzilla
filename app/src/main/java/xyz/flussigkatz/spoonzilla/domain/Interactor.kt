package xyz.flussigkatz.spoonzilla.domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem
import xyz.flussigkatz.core_api.entity.instructions.InstructionsItem
import xyz.flussigkatz.core_api.entity.nutrient.NutrientItem
import xyz.flussigkatz.remote.SpoonacularApi
import xyz.flussigkatz.spoonzilla.ApiKey.API_KEY
import xyz.flussigkatz.spoonzilla.data.db.MainRepository
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.util.Converter
import java.lang.Exception

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
            apiKey = API_KEY
        ).subscribeOn(Schedulers.io())
            .map {
                val markedIds = repository.getIdsMarkedDishesFromDbToList()
                Converter.convertRecipeByIdFromApi(it, markedIds)
            }.subscribe(
                { repository.putAdvancedInfoDishToDb(it) },
                { println("$TAG getRecipeByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRecentlyViewedDishes(): Observable<List<Dish>> {
        return repository.getRecentlyViewedDishes()
    }

    fun getIngredientsByIdFromDb(id: Int): Observable<List<IngredientItem>> {
        return repository.getIngredients(id)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertIngredientsFromDb(it) }
    }

    fun getIngredientsByIdFromApi(id: Int) {
        val metric = preferences.getMetric()
        retrofitService.getIngredientsById(id = id, apiKey = API_KEY)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertIngredientsFromApi(it, metric, id) }
            .subscribe(
                { repository.putIngredients(it) },
                { println("$TAG getIngredientsByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getEquipmentsByIdFromDb(id: Int): Observable<List<EquipmentItem>> {
        return repository.getEquipments(id)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertEquipmentsByIdFromDb(it) }
    }

    fun getEquipmentsByIdFromApi(id: Int) {
        retrofitService.getEquipmentsById(id = id, apiKey = API_KEY)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertEquipmentsFromApi(it, id) }
            .subscribe(
                { repository.putEquipments(it) },
                { println("$TAG getEquipmentsByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getInstructionsByIdFromDb(id: Int): Observable<List<InstructionsItem>> {
        return repository.getInstructions(id)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertInstructionsByIdFromDb(it) }
    }

    fun getInstructionsByIdFromApi(id: Int) {
        retrofitService.getInstructionsById(id = id, apiKey = API_KEY)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertInstructionsByIdFromApi(it, id) }
            .subscribe(
                { repository.putInstructions(it) },
                { println("$TAG getInstructionsByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getNutrientByIdFromDb(id: Int): Observable<List<NutrientItem>> {
        return repository.getNutrients(id).subscribeOn(Schedulers.io())
            .map { Converter.convertNutrientsByIdFromDb(it) }
    }

    fun getNutrientByIdFromApi(id: Int) {
        retrofitService.getNutrientById(id = id, apiKey = API_KEY)
            .subscribeOn(Schedulers.io())
            .map { Converter.convertNutrientsFromApi(it, id) }
            .subscribe(
                { repository.putNutrients(it) },
                { println("$TAG getNutrientByIdFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getRandomRecipeFromApi(
        number: Int,
        tags: String,
        clearDb: Boolean
    ) {
        retrofitService.getRandomRecipes(
            limitLicense = false,
            tags = tags,
            number = number,
            apiKey = API_KEY
        ).subscribeOn(Schedulers.io())
            .filter { !it.recipes.isNullOrEmpty() }
            .map {
                val markedIds = repository.getIdsMarkedDishesFromDbToList()
                Converter.convertRandomRecipeFromApi(it, markedIds)
            }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) repository.clearDishTable()
                    repository.putDishesToDb(it)
                },
                { println("$TAG getRandomRecipeFromApi onError: ${it.localizedMessage}") }
            )
    }

    fun getDishesFromDb() = repository.getAllDishesFromDb()

    //    DishAlarm
    fun getDishAlarmsFromDb() = repository.getAllDishAlarmsFromDb()

    fun putDishAlarmToDb(dishAlarm: DishAlarm) {
        repository.putDishAlarmToDb(dishAlarm)
    }

    fun updateDishAlarm(dishAlarm: DishAlarm) {
        repository.updateDishAlarm(dishAlarm)
    }
    fun deleteDishAlarm(localId: Int) {
        repository.deleteDishAlarm(localId)
    }

    fun getMarkedDishesFromDb(query: String): Observable<List<DishMarked>> {
        return repository.getAllMarkedDishesFromDb(query)
    }

    fun setDishMark(dish: Dish) {
        try {
            if (dish.mark) repository.putMarkedDishToDb(Converter.convertDishToDishMarked(dish))
            else repository.deleteMarkedDishFromDb(dish.id)
            val cashedDish = repository.getDishFromDb(dish.id)
            cashedDish.mark = dish.mark
            repository.updateDish(cashedDish)
            val dishAdvancedInfo = repository.getAdvancedInfoDishFromDbToList(dish.id)
            dishAdvancedInfo.map { it.mark = dish.mark }
            if (dishAdvancedInfo.isNotEmpty()) repository.updateAdvancedInfoDish(dishAdvancedInfo.first())
        } catch (e: Exception) {
            println("$TAG setDishMark ${e.localizedMessage}")
        }
    }

    fun getSimilarRecipesFromApi(id: Int) {
        retrofitService.getSimilarRecipes(
            id = id,
            number = 4,
            limitLicense = false,
            apiKey = API_KEY
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
            apiKey = API_KEY
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
            apiKey = API_KEY
        ).subscribeOn(Schedulers.io())
            .map {
                val markedIds = repository.getIdsMarkedDishesFromDbToList()
                Converter.convertSearchedRecipeBasicInfoFromApi(it, markedIds)
            }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) repository.clearDishTable()
                    repository.putDishesToDb(it)
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
            apiKey = API_KEY
        ).subscribeOn(Schedulers.io())
            .map {
                val markedIds = repository.getIdsMarkedDishesFromDbToList()
                Converter.convertSearchedRecipeBasicInfoFromApi(it, markedIds)
            }
            .doOnSubscribe { loadingState.onNext(true) }
            .doOnComplete { loadingState.onNext(false) }
            .doOnError { loadingState.onNext(false) }
            .subscribe(
                {
                    if (clearDb) repository.clearDishTable()
                    repository.putDishesToDb(it)
                },
                { println("$TAG getAdvancedSearchedRecipes onError: ${it.localizedMessage}") }
            )
    }

    fun getDishAdvancedInfoFromDb(dishId: Int) = repository.getAdvancedInfoDishFromDb(dishId)

    fun deleteAdvancedInfoDishFromDb(dishId: Int) {
        repository.deleteAdvancedInfoDishFromDb(dishId)
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