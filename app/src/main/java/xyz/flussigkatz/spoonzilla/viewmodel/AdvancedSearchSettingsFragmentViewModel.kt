package xyz.flussigkatz.spoonzilla.viewmodel

import androidx.lifecycle.ViewModel
import xyz.flussigkatz.spoonzilla.App
import xyz.flussigkatz.spoonzilla.domain.Interactor
import xyz.flussigkatz.spoonzilla.view.dialog.CuisineDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.DietsDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.MealTypesDialogFragment
import javax.inject.Inject

class AdvancedSearchSettingsFragmentViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var cuisineDialog: CuisineDialogFragment

    @Inject
    lateinit var dietsDialog: DietsDialogFragment

    @Inject
    lateinit var intolerancesDialog: IntolerancesDialogFragment

    @Inject
    lateinit var mealTypesDialog: MealTypesDialogFragment

    init {
        App.instance.dagger.inject(this)
    }

    fun getCuisineDialogFragment() = cuisineDialog

    fun getDietsDialogFragment() = dietsDialog

    fun getIntolerancesDialogFragment() = intolerancesDialog

    fun getMealTypesDialogFragment() = mealTypesDialog

    fun saveAdvancedSearchSwitchState(key: String, state: Boolean) {
        interactor.saveAdvancedSearchSwitchState(key, state)
    }

    fun getAdvancedSearchSwitchState(key: String) = interactor.getAdvancedSearchSwitchState(key)

    fun existProfile() = !interactor.getProfile().isNullOrEmpty()
}