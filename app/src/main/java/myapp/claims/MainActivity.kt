package myapp.claims

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import myapp.claims.Database.ClaimField
import myapp.claims.Database.ClaimType
import myapp.claims.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private val viewModel by viewModel<ClaimsDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoadJson.setOnClickListener {
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

    private fun loadClaimsJson(): JsonObject? {
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