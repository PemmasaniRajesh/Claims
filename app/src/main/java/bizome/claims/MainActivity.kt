package bizome.claims

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import bizome.claims.Database.AppDatabase
import bizome.claims.Database.ClaimField
import bizome.claims.Database.ClaimType
import bizome.claims.Database.ClaimsRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btn_loadJson)

        val appDatabase = AppDatabase.getInstance(MainActivity@this)
        val repository = ClaimsRepository(appDatabase)


        val viewModelFactory = ClaimsDataViewModel.ClaimsDataViewModelFactory(repository)

        val viewModel = ViewModelProvider(this,viewModelFactory).get(ClaimsDataViewModel::class.java)

        btn.setOnClickListener {
            val jsonObject = loadClaimsJson()
            val claimTypList:MutableList<ClaimType> = mutableListOf()


            for (obj in jsonObject?.getAsJsonArray("Claims")!!) {
                val claimFieldsList: MutableList<ClaimField> = mutableListOf()
                val claimType =
                    Gson().fromJson(obj.asJsonObject.get("Claimtype"), ClaimType::class.java)

                val claimDetailArr = obj.asJsonObject.getAsJsonArray("Claimtypedetail")
                for (obj1 in claimDetailArr) {
                    val claimField = Gson().fromJson(
                        obj1.asJsonObject.getAsJsonObject("Claimfield"),
                        ClaimField::class.java
                    )
                    claimField.claimtype_id=claimType.id
                    claimFieldsList.add(claimField)
                }
                claimType.claimFields = claimFieldsList
                claimTypList.add(claimType)

                //adding to room database....
                viewModel.insert(claimType)

            }
            println("Checking Entries")
        }
    }

    fun loadClaimsJson(): JsonObject? {
        var jsonString = "{}"
        try {
            jsonString = this.assets.open("claims_json.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            print(ioException)
        }

        return Gson().fromJson(jsonString, JsonObject::class.java)
    }
}