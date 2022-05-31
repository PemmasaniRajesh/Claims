package myapp.claims

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import myapp.claims.Database.*
import kotlinx.coroutines.launch

class ClaimsDataViewModel(private val repository: ClaimsRepository):ViewModel() {


    fun insert(claimType:ClaimType) {
        viewModelScope.launch {
            repository.insertClaimTrans(claimType)
        }
    }

    fun getAllClaimTypes(): LiveData<List<ClaimType>>{
        return repository.getClaimTypes()
    }

    fun getClaimFields(claimTypeId:Int):LiveData<List<ClaimField>>{
        return repository.getClaimFields(claimTypeId)
    }

    fun getClaimFieldsOptions(claimFieldId:Int):LiveData<List<ClaimFieldOption>>{
        return repository.getClaimFieldOptions(claimFieldId)
    }

    fun insert(expense: Expense){
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    fun getExpenses(claimTypeId: Int):LiveData<List<Expense>>{
        return repository.getExpenses(claimTypeId)
    }
}