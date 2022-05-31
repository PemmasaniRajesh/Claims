package myapp.claims.Database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class ClaimType(
    @PrimaryKey(autoGenerate = true)
    val rowId:Int,

    val id: Int,

    val name: String,

    @Ignore
    var claimFields: List<ClaimField>
) {
    constructor(rowId:Int,id: Int, name: String) : this(0,id, name, mutableListOf())
    constructor(name: String,fields:List<ClaimField>):this(0,0,name,fields)

    override fun toString(): String {
        return name
    }
}
