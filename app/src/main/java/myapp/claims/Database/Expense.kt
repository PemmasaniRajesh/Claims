package myapp.claims.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val claimTypeId:Int,
    val claimType:String,
    val claimDate:Long,
    val amount:Int
)
