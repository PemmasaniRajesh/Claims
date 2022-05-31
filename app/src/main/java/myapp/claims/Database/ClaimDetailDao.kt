package myapp.claims.Database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ClaimDetailDao {

    @Insert
    suspend fun insert(claimDetail: ClaimDetail)
}