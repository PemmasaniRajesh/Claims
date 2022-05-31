package myapp.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ClaimTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(claimType: ClaimType)

    @Query("SELECT * FROM ClaimType")
    fun getAllClaims(): LiveData<List<ClaimType>>
}