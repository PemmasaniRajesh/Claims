package bizome.claims.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClaimDetail(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val claimTypeId:Int,
    val claimFieldId:Int
)
