package myapp.claims

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import myapp.claims.Database.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import myapp.claims.databinding.ActivityClaimsBinding
import myapp.claims.databinding.LayoutDropdownBinding
import myapp.claims.databinding.LayoutEditTextBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class ClaimsActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var binding: ActivityClaimsBinding

    private val viewModel by viewModel<ClaimsDataViewModel>()

    private lateinit var claimTypeAdapter:ArrayAdapter<ClaimType>

    private lateinit var claimTypeList:MutableList<ClaimType>

    private lateinit var selectedClaimType: ClaimType

    private lateinit var expenseAdapter: ExpenseAdapter

    private lateinit var expenseList:MutableList<Expense>

    private lateinit var dynamicFields:MutableList<ClaimField>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClaimsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        claimTypeList = mutableListOf()
        expenseList = mutableListOf()
        dynamicFields = mutableListOf()

        initViewModel()

        setClaimTypeAdapter()

        initExpenseAdapter()

    }

    private fun initExpenseAdapter(){
        expenseAdapter = ExpenseAdapter(expenseList)
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvExpenseList.layoutManager = layoutManager
        binding.rvExpenseList.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))

        binding.rvExpenseList.adapter = expenseAdapter
    }

    private fun setClaimTypeAdapter() {

        claimTypeAdapter = ArrayAdapter<ClaimType>(this,android.R.layout.simple_dropdown_item_1line,claimTypeList)
        binding.avClaimType.setAdapter(claimTypeAdapter)

        binding.avClaimType.setOnItemClickListener { parent,view,position,_ ->
            selectedClaimType = claimTypeList.get(position)

            binding.llDynamicField.removeAllViews()
            expenseList.clear()
            expenseAdapter.notifyItemRangeRemoved(0,0)

            viewModel.getClaimFields(claimTypeId = selectedClaimType.id).observe(this) {
                for (claimField in it) {
                    if (claimField.type.equals(Global.DROPDOWN, true)) {
                        binding.llDynamicField.addView(createDropDownField(claimField))

                        claimField.view = createDropDownField((claimField))
                        dynamicFields.add(claimField)
                    }else{
                        binding.llDynamicField.addView(createSingleLineTextField(claimField))
                        claimField.view = createSingleLineTextField((claimField))
                        dynamicFields.add(claimField)
                    }
                }
            }

            viewModel.getExpenses(claimTypeId = selectedClaimType.id).observe(this){
                /*this is not a recommended way...we can use latest listAdapter inplace of recycler adapter..*/
                val currentSize = expenseList.size
                expenseList.clear()
                expenseList.addAll(it)
                expenseAdapter.notifyItemRangeRemoved(0,currentSize)
                expenseAdapter.notifyItemRangeInserted(0,it.size)
            }
        }
    }

    private fun initViewModel(){
        viewModel.getAllClaimTypes().observe(this) {
            println("ItemsSize${it.size}")
            claimTypeList.addAll(it)
            claimTypeAdapter.notifyDataSetChanged()
        }
    }

    private fun createDropDownField(claimField: ClaimField): View {
        val binding = LayoutDropdownBinding.inflate(layoutInflater);
        var layout: View = binding.root

        val textInputLayout = binding.tlDropDown
        textInputLayout.hint = claimField.label
        textInputLayout.tag = claimField.id
        val autoCompleteTextView = binding.avDropDown
        if(claimField.required){
            textInputLayout.hint = claimField.label.plus( " * ")
        }
        val claimFieldOptions:MutableList<ClaimFieldOption> = mutableListOf()
        val tag = "dd"+claimField.rowId
        autoCompleteTextView.tag = tag
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

        val binding = LayoutEditTextBinding.inflate(layoutInflater)

        var layout: View =binding.root

        val textInputLayout = binding.textInputLayout

        textInputLayout.hint = claimField.label
        textInputLayout.tag = claimField.id
        if(claimField.required){
            textInputLayout.hint = claimField.label.plus( " * ")
        }
        val textInputEditText = binding.textInputEditText
        val tag="et"+claimField.rowId
        textInputEditText.tag = tag
        textInputEditText.setSingleLine()
        if (claimField.type.equals(Global.SINGLE_LINE_TEXT_ALL_CAPS, true)) {
            textInputEditText.isAllCaps = true
        } else if (claimField.type.equals(Global.SINGLE_LINE_TEXT_NUMERIC, true)) {
            textInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        return layout
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnSave->{
                saveForm()
            }
            R.id.btnAddClaim->{
                addExpense()
            }
        }
    }

    private fun addExpense() {

        if(binding.etExpense.text.toString().trim().isNullOrEmpty()){
            binding.tlExpense.error="Please enter the expense amount"
            return
        }else{
            binding.tlExpense.error=null
        }

        if(!::selectedClaimType.isInitialized){
            Toast.makeText(ClaimsActivity@this,"Select Claim Type",Toast.LENGTH_LONG).show()
            return
        }

        val amt = binding.etExpense.text.toString().toInt() * 2
        val dt = System.currentTimeMillis()

        val expense = Expense(0,selectedClaimType.id,selectedClaimType.name,dt,amt)
        viewModel.insert(expense)

        Toast.makeText(ClaimsActivity@this,"Expense added successfully",Toast.LENGTH_LONG).show()

        binding.etExpense.text?.clear()
    }

    private fun saveForm() {

        if(!::selectedClaimType.isInitialized){
            Toast.makeText(ClaimsActivity@this,"Select Claim Type",Toast.LENGTH_LONG).show()
            return
        }

        var isFormValid = true

        for(claimField in dynamicFields){
            when(claimField.type){
                Global.SINGLE_LINE_TEXT_NUMERIC,
                Global.SINGLE_LINE_TEXT_ALL_CAPS, Global.SINGLE_LINE_TEXT -> {
                    val tag = "et"+claimField.rowId
                    val view:TextInputEditText = binding.llDynamicField.findViewWithTag(tag) as TextInputEditText
                    val view1:TextInputLayout = binding.llDynamicField.findViewWithTag(claimField.id) as TextInputLayout
                    if(claimField.required && view.text.toString().isEmpty()){
                        isFormValid=false
                        view1.error = "Field is required"
                    }else{
                        view1.error =null
                    }
                }
                Global.DROPDOWN->{
                    val tag = "dd"+claimField.rowId
                    val view:MaterialAutoCompleteTextView = binding.llDynamicField.findViewWithTag(tag) as MaterialAutoCompleteTextView
                    val view1:TextInputLayout = binding.llDynamicField.findViewWithTag(claimField.id) as TextInputLayout
                    if(claimField.required &&  (view.text.toString().isEmpty())){
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