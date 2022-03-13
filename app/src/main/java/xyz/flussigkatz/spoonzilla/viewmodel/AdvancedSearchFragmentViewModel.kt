package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.util.AppConst
import javax.inject.Inject

class AdvancedSearchFragmentViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    init {
        App.instance.dagger.inject(this)
    }

    fun getAdvancedSearchedRecipes(
        query: String,
    ) {
        val cuisine = interactor.getDialogItemsFromPreference(AppConst.KEY_CUISINE)
        val diet = interactor.getDialogItemsFromPreference(AppConst.KEY_DIET)
        val intolerances = interactor.getDialogItemsFromPreference(AppConst.KEY_INTOLERANCE)
        val type = interactor.getDialogItemsFromPreference(AppConst.KEY_MEAT_TYPE)
        val instructionsRequired = interactor.getAdvancedSearchSwitchState(AppConst.KEY_INSTRUCTIONS_SWITCH)
        interactor.getAdvancedSearchedRecipes(
            query = query,
            cuisine = cuisine?.joinToString(),
            diet = diet?.joinToString(),
            intolerances = intolerances?.joinToString(),
            type = type?.joinToString(),
            instructionsRequired = instructionsRequired,
            offset = AppConst.DEFAULT_OFFSET,
            number = AppConst.TOTAL_NUMBER_ELEMENTS,
            clearDb = true
        )
    }
}