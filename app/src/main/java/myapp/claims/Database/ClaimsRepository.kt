package myapp.claims.Database

import androidx.lifecycle.LiveData
import androidx.room.withTransaction

class ClaimsRepository (private val appDatabase: AppDatabase){

    suspend fun insertClaim(claimType: ClaimType){
        appDatabase.claimTypeDao().insert(claimType)
    }

    suspend fun insertClaimTrans(claimType: ClaimType){
        appDatabase.withTransaction {
            appDatabase.claimTypeDao().insert(claimType)
            for(claimField in claimType.claimFields){
                appDatabase.claimFieldDao().insert(claimField)
                appDatabase.claimFieldOptionDao().insertAll(claimField.claimFieldOptions)
            }
        }
    }

    fun getClaimTypes(): LiveData<List<ClaimType>> {
        return appDatabase.claimTypeDao().getAllClaims()
    }

    fun getClaimFields(claimTypeId:Int):LiveData<List<ClaimField>>{
        return appDatabase.claimFieldDao().getClaimFieldsByType(claimTypeId)
    }

    fun getClaimFieldOptions(claimFieldId:Int):LiveData<List<ClaimFieldOption>>{
        return appDatabase.claimFieldOptionDao().getFieldOptions(claimFieldId)
    }

    suspend fun insertExpense(expense: Expense){
        appDatabase.expenseDao().insert(expense)
    }

    fun getExpenses(claimTypeId:Int):LiveData<List<Expense>>{
        return appDatabase.expenseDao().getExpenseByClaimType(claimTypeId)
    }
}