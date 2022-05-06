package xyz.flussigkatz.core_impl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.flussigkatz.core_api.db.DishDao
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishMarked

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: DishDao
    private val id = 1

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.dishesDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun checkWriteReadAndDeleteDish() {
        val dish = Dish(localId = id, id = id, title = "title", image = null, mark = false)
        dao.insertAllDishes(listOf(dish))
        val cashedDish = dao.getCashedDish(id)
        assertThat(dish, equalTo(cashedDish))
        dao.deleteDishes(listOf(dish))
        val list = dao.getCashedDishesToList()
        assertEquals(true, list.isEmpty())
    }

    @Test
    fun checkWriteReadAndDeleteDishMarked() {
        val dishMarked =
            DishMarked(localId = id, id = id, title = "title", image = null, mark = true)
        dao.insertMarkedDishes(dishMarked)
        val cashedDishMarked = dao.getOneCashedMarkedDish(id)
        assertThat(dishMarked, equalTo(cashedDishMarked))
        dao.deleteMarkedDish(dishMarked)
        val list = dao.getIdsCashedMarkedDishesToList()
        assertEquals(true, list.isEmpty())
    }

    @Test
    fun checkWriteReadAndDeleteDishAdvancedInfo() {
        val dishAdvancedInfo = DishAdvancedInfo(
            localId = id,
            id = id,
            aggregateLikes = 0,
            cheap = false,
            image = null,
            pricePerServing = 0.0,
            readyInMinutes = 0,
            servings = 0,
            sourceUrl = "sourceUrl",
            summary = "summary",
            title = "title",
            mark = false
        )
        dao.insertAdvancedInfoDish(dishAdvancedInfo)
        val cashedDishAdvancedInfo = dao.getSingleCashedAdvancedInfoDish(id)
        assertThat(dishAdvancedInfo, equalTo(cashedDishAdvancedInfo))
        dao.deleteAdvancedInfoDish(dishAdvancedInfo)
        val list = dao.getCashedObservableAdvancedInfoDishToList(id)
        assertEquals(true, list.isEmpty())
    }

}