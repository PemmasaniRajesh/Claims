package bizome.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClaimTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(claimType: ClaimType)

    @Query("SELECT * FROM ClaimType")
    fun getAllClaims(): LiveData<List<ClaimType>>
}