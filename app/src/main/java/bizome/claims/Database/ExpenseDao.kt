package bizome.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM Expense WHERe claimTypeId=:claimTypeId")
    fun getExpenseByClaimType(claimTypeId: Int): LiveData<List<Expense>>
}