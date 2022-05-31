package myapp.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClaimFieldOptionDao {

    @Query("SELECT * FROM ClaimFieldOption WHERE claimFieldId=:fieldId")
    fun getFieldOptions(fieldId:Int):LiveData<List<ClaimFieldOption>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fieldOptions:List<ClaimFieldOption>)
}