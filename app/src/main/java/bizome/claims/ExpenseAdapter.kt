package bizome.claims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bizome.claims.Database.ClaimType
import bizome.claims.Database.Expense

class ExpenseAdapter(expenseList: List<Expense>):
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    var mExpenseList:List<Expense> = expenseList

    inner class ExpenseViewHolder(view: View):RecyclerView.ViewHolder(view){
         var tv_claim_type:TextView = view.findViewById(R.id.tv_claim_type)
        var tv_claim_date:TextView = view.findViewById(R.id.tv_claim_date)
        var tv_claim_amt:TextView = view.findViewById(R.id.tv_claim_expense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.tv_claim_amt.text = mExpenseList.get(position).amount.toString()
        holder.tv_claim_type.text=mExpenseList.get(position).claimType
        holder.tv_claim_date.text=Utils().dateTime(mExpenseList.get(position).claimDate)
    }

    override fun getItemCount(): Int {
        return mExpenseList.size
    }
}