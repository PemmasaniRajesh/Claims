package myapp.claims

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.PreferenceManager
import myapp.claims.Database.ClaimField
import myapp.claims.Database.ClaimType
import com.google.gson.Gson
import com.google.gson.JsonObject
import myapp.claims.databinding.ActivityClaimsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class SplashScreenActivity : AppCompatActivity() {


    private val viewModel by viewModel<ClaimsDataViewModel>()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityClaimsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClaimsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity@this)

        if(sharedPreferences.getBoolean(Global.CLAIMS_DATA_LOADED,false)){
            startClaimsActivity()
        }else{
            insertClaimsJsonToRoomDb()
        }
    }

    private fun startClaimsActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            val intentLogin = Intent(this, ClaimsActivity::class.java)
            startActivity(intentLogin)
            finish()
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        },3000)
    }

    private fun insertClaimsJsonToRoomDb(){
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
                claimField.required =
                    obj1.asJsonObject.getAsJsonObject("Claimfield").getAsJsonPrimitive("required").asInt==1

                claimField.claimtype_id=claimType.id
                claimFieldsList.add(claimField)
            }
            claimType.claimFields = claimFieldsList
            claimTypList.add(claimType)

            //adding to room database....
            viewModel.insert(claimType)
        }

        sharedPreferences.edit().putBoolean(Global.CLAIMS_DATA_LOADED,true).apply()

        startClaimsActivity()
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