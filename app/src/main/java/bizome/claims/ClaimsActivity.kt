package bizome.claims

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.setMargins
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bizome.claims.Database.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class ClaimsActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var ll_fields: LinearLayout
    private lateinit var appDatabase:AppDatabase
    private lateinit var repository: ClaimsRepository
    private lateinit var viewModelFactory: ClaimsDataViewModel.ClaimsDataViewModelFactory
    private lateinit var viewModel:ClaimsDataViewModel

    private lateinit var tl_claim_type:TextInputLayout
    private lateinit var av_claim_type:MaterialAutoCompleteTextView

    private lateinit var claimTypeAdatper:ArrayAdapter<ClaimType>

    private lateinit var claimTypeList:MutableList<ClaimType>

    private lateinit var selectedClaimType: ClaimType

    private lateinit var rv_expenseList:RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter

    private lateinit var expenseList:MutableList<Expense>

    private lateinit var dynamicFields:MutableList<ClaimField>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claims)

        ll_fields = findViewById(R.id.ll_fieldUI)

        tl_claim_type = findViewById(R.id.tl_claim_type)
        av_claim_type = findViewById(R.id.av_claim_type)

        claimTypeList = mutableListOf()
        expenseList = mutableListOf()
        dynamicFields = mutableListOf()

        initViewModel()

        setClaimTypeAdapter()

        initExpenseAdapter()

    }

    private fun initExpenseAdapter(){
        rv_expenseList = findViewById(R.id.rv_expense_list)
        expenseAdapter = ExpenseAdapter(expenseList)
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_expenseList.layoutManager = layoutManager
        rv_expenseList.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))

        rv_expenseList.adapter = expenseAdapter
    }

    private fun setClaimTypeAdapter() {

        claimTypeAdatper = ArrayAdapter<ClaimType>(this,android.R.layout.simple_dropdown_item_1line,claimTypeList)
        av_claim_type.setAdapter(claimTypeAdatper)

        av_claim_type.setOnItemClickListener { parent,view,position,_ ->
            selectedClaimType = claimTypeList.get(position)

            ll_fields.removeAllViews()

            viewModel.getClaimFields(claimTypeId = selectedClaimType.id).observe(this) {
                for (claimField in it) {
                    if (claimField.type.equals(Global.DropDown, true)) {
                        ll_fields.addView(createDropDownField(claimField))

                        claimField.view = createDropDownField((claimField))
                        dynamicFields.add(claimField)
                    }else{
                        ll_fields.addView(createSingleLineTextField(claimField))
                        claimField.view = createSingleLineTextField((claimField))
                        dynamicFields.add(claimField)
                    }
                }
            }

            expenseList.clear()
            expenseAdapter.notifyDataSetChanged()

            viewModel.getExpenses(claimTypeId = selectedClaimType.id).observe(this){
                expenseList.addAll(it)
                expenseAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initViewModel(){
        appDatabase = AppDatabase.getInstance(ClaimsActivity@ this)
        repository = ClaimsRepository(appDatabase)


        viewModelFactory = ClaimsDataViewModel.ClaimsDataViewModelFactory(repository)

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(ClaimsDataViewModel::class.java)


        viewModel.getAllClaimTypes().observe(this) {
            println("ItemsSize${it.size}")
            claimTypeList.addAll(it)
            claimTypeAdatper.notifyDataSetChanged()
        }
    }

    private fun createDropDownField(claimField: ClaimField): View {
        var layout: View = LayoutInflater.from(this).inflate(R.layout.layout_dropdown, null)
        val textInputLayout = layout.findViewById<TextInputLayout>(R.id.tl_dd)
        textInputLayout.hint = claimField.label
        val autoCompleteTextView = layout.findViewById<MaterialAutoCompleteTextView>(R.id.av_dd)
        if(claimField.required){
            textInputLayout.hint = claimField.label.plus( " * ")
        }
        val claimFieldOptions:MutableList<ClaimFieldOption> = mutableListOf()

        val arrayAdapter = ArrayAdapter<ClaimFieldOption>(this,android.R.layout.simple_dropdown_item_1line,claimFieldOptions)
        autoCompleteTextView.setAdapter(arrayAdapter)

        viewModel.getClaimFieldsOptions(claimFieldId = claimField.id).observe(this) {
            if (it!=null){
                claimFieldOptions.addAll(it)
                arrayAdapter.notifyDataSetChanged()
            }
        }
        return layout
    }


    private fun createSingleLineTextField(claimField: ClaimField): View {

        var layout: View = LayoutInflater.from(this).inflate(R.layout.layout_edit_text, null)

        val textInputLayout = layout.findViewById<TextInputLayout>(R.id.textInputLayout)

        textInputLayout.hint = claimField.label
        if(claimField.required){
            textInputLayout.hint = claimField.label.plus( " * ")
        }
        val textInputEditText = layout.findViewById<TextInputEditText>(R.id.textInputEditText)

        textInputEditText.setSingleLine()
        if (claimField.type.equals(Global.SingleLineTextAllCaps, true)) {
            textInputEditText.isAllCaps = true
        } else if (claimField.type.equals(Global.SingleLineTextNumeric, true)) {
            textInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        return layout
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_save->{
                saveForm()
            }
            R.id.btn_addClaim->{
                addExpense()
            }
        }
    }

    private fun addExpense() {
        val tl_expense = findViewById<TextInputLayout>(R.id.tl_expense)
        val et_expense = findViewById<TextInputEditText>(R.id.et_expense)

        if(et_expense.text.toString().trim().isNullOrEmpty()){
            tl_expense.error="Please enter the expense amount"
            return
        }else{
            tl_expense.error=null
        }

        if(!::selectedClaimType.isInitialized){
            Toast.makeText(ClaimsActivity@this,"Select Claim Type",Toast.LENGTH_LONG).show()
            return
        }

        val amt = et_expense.text.toString().toInt() * 2
        val dt = System.currentTimeMillis()

        val expense = Expense(0,selectedClaimType.id,selectedClaimType.name,dt,amt)
        viewModel.insert(expense)

        Toast.makeText(ClaimsActivity@this,"Expense added successfully",Toast.LENGTH_LONG).show()

        et_expense.text?.clear()
    }

    private fun saveForm() {

        if(!::selectedClaimType.isInitialized){
            Toast.makeText(ClaimsActivity@this,"Select Claim Type",Toast.LENGTH_LONG).show()
            return
        }

        var isFormValid = true

        for(claimField in dynamicFields){
            when(claimField.type){
                Global.SingleLineTextNumeric,
                Global.SingleLineTextAllCaps, Global.SingleLineText -> {
                    val view = claimField.view?.findViewById(R.id.textInputEditText) as TextInputEditText
                    val view1 = claimField.view?.findViewById(R.id.textInputLayout) as TextInputLayout
                    if(claimField.required && view.text.toString().isEmpty()){
                        isFormValid=false
                        view1.error = "Field is required"
                    }else{
                        view1.error =null
                    }
                }
                Global.DropDown->{
                    val view = claimField.view?.findViewById(R.id.av_dd) as MaterialAutoCompleteTextView
                    val view1 = claimField.view?.findViewById(R.id.tl_dd) as TextInputLayout
                    if(claimField.required &&  (view.text.toString().isEmpty() || !view.isSelected)){
                        isFormValid=false
                        view1.error = "Field is required"
                    }else{
                        view1.error =null
                    }
                }
            }
        }

        if(!isFormValid){
            return
        }

    }

}