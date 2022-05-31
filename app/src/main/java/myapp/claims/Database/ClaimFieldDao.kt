package myapp.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClaimFieldDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(field: ClaimField)

    @Query("SELECT * FROM ClaimField where claimtype_id=:claimTypeId")
    fun getClaimFieldsByType(claimTypeId:Int): LiveData<List<ClaimField>>
}