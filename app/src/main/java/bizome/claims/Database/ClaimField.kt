package bizome.claims.Database

import android.view.View
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ClaimField(
    @PrimaryKey(autoGenerate = true)
    val rowId:Int,
    val id: Int,
    var claimtype_id:Int,
    val name:String,
    val label:String,
    val type:String,
    var required:Boolean,
    val isdependant:Boolean,
    val created:String,
    val modified:String,
    @Ignore
    @SerializedName(value = "Claimfieldoption")
    val claimFieldOptions:List<ClaimFieldOption>,
    @Ignore
    var view: View?
){
    constructor(rowId:Int,id:Int,claimtype_id: Int,name: String,label: String,type: String,required: Boolean,isdependant: Boolean,
    created: String,modified: String):
            this(0,id,claimtype_id,name,label, type, required, isdependant, created, modified, mutableListOf(),null)
}
