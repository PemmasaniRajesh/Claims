package bizome.claims

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import bizome.claims.Database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ClaimsDataViewModel(private val repository: ClaimsRepository):ViewModel() {


    fun insert(claimtype:ClaimType) {
        viewModelScope.launch {
            repository.insertClaimTrans(claimtype)
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

    class ClaimsDataViewModelFactory(private val repository: ClaimsRepository):ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ClaimsDataViewModel::class.java)){
                return ClaimsDataViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown Model Class")
        }

    }
}