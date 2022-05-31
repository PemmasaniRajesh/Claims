package myapp.claims

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapp.claims.Database.Expense
import myapp.claims.databinding.AdapterExpenseItemBinding

class ExpenseAdapter(expenseList: List<Expense>):
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    var mExpenseList:List<Expense> = expenseList

    inner class ExpenseViewHolder(var binding: AdapterExpenseItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = AdapterExpenseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        with(holder){
            with(mExpenseList[position]){
                binding.tvClaimDate.text=Utils().dateTime(this.claimDate)
                binding.tvClaimType.text = this.claimType
                binding.tvClaimExpense.text=this.amount.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return mExpenseList.size
    }
}