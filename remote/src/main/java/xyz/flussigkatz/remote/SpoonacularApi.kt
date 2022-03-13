package xyz.flussigkatz.remote

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import xyz.flussigkatz.remote.entity.random.RandomRecipesDto
import xyz.flussigkatz.remote.entity.seached.SearchedRecipesDto
import xyz.flussigkatz.remote.entity.searched_by_id.SearchedRecipeByIdDto
import xyz.flussigkatz.remote.entity.similar.SimilarRecipesDto
import xyz.flussigkatz.remote.entity.taste.TasteByIdDto

interface SpoonacularApi {
    @GET("/recipes/random")
    fun getRandomRecipes(
        @Query("limitLicense") limitLicense: Boolean,
        @Query("tags") tags: String?,
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String
    ): Observable<RandomRecipesDto>

    @GET("/recipes/{id}/information")
    fun getRecipeById(
        @Path("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean,
        @Query("apiKey") apiKey: String
    ): Observable<SearchedRecipeByIdDto>

    @GET("/recipes/{id}/similar")
    fun getSimilarRecipes(
        @Path("id") id: Int,
        @Query("number") number: Int,
        @Query("limitLicense") limitLicense: Boolean,
        @Query("apiKey") apiKey: String
    ): Observable<SimilarRecipesDto>

    @GET("/recipes/{id}/tasteWidget.json")
    fun getRecipeTaste(
        @Path("id") id: Int,
        @Query("normalize") normalize: Boolean,
        @Query("apiKey") apiKey: String
    ): Observable<TasteByIdDto>

    @GET("/recipes/complexSearch")
    fun getSearchedRecipes(
        @Query("query") query: String,
        @Query("offset") offset: Int?,
        @Query("number") number: Int?,
        @Query("limitLicense") limitLicense: Boolean,
        @Query("apiKey") apiKey: String
    ): Observable<SearchedRecipesDto>

    @GET("/recipes/complexSearch")
    fun getAdvancedSearchedRecipes(
        @Query("query") query: String,
        @Query("cuisine") cuisine: String?,
        @Query("diet") diet: String?,
        @Query("intolerances") intolerances: String?,
        @Query("type") type: String?,
        @Query("instructionsRequired") instructionsRequired: Boolean?,
        @Query("offset") offset: Int?,
        @Query("number") number: Int?,
        @Query("limitLicense") limitLicense: Boolean,
        @Query("apiKey") apiKey: String
    ): Observable<SearchedRecipesDto>
}