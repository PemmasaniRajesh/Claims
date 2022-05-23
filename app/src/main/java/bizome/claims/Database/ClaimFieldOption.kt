package bizome.claims.Database

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(indices = [Index(value = ["id"], unique = true)])
data class ClaimFieldOption(
    @PrimaryKey(autoGenerate = true)
    val rowId:Int,
    val id: Int,
    val name:String,
    @SerializedName(value = "claimfield_id")
    val claimfieldid:Int,
    val label:String,
    @Nullable
    val belongsTo:String?,
    val hasmany:Boolean
) {
    override fun toString(): String {
        return name
    }
}
