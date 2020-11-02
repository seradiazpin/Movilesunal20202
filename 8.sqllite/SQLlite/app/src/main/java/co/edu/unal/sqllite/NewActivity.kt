package co.edu.unal.sqllite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewActivity : AppCompatActivity() {
    private var nameEditText: EditText? = null
    private var urlEditText: EditText? = null
    private var phoneEditText: EditText? = null
    private var mailEditText: EditText? = null
    private var productsEditText: EditText? = null
    private val classificationEditText: EditText? = null
    private var addUpdateButton: Button? = null
    private var consultoriaCheckBox: CheckBox? = null
    private  var desarolloCheckBox:CheckBox? = null
    private  var fabricacionCheckBox:CheckBox? = null
    private var newEnterprise: Enterprise? = null
    private var oldEnterprise: Enterprise? = null
    private var mode: String? = null
    private var entrepriseData: EnterpriseOperations? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        var element = intent.getSerializableExtra("MyClass") as Enterprise?;


        nameEditText = findViewById<EditText>(R.id.edit_text_name)
        urlEditText = findViewById<EditText>(R.id.edit_text_url)
        phoneEditText = findViewById<EditText>(R.id.edit_text_phone)
        mailEditText = findViewById<EditText>(R.id.edit_text_mail)
        productsEditText = findViewById<EditText>(R.id.edit_text_products)
        consultoriaCheckBox = findViewById<CheckBox>(R.id.checkbox_consultoria)
        desarolloCheckBox = findViewById<CheckBox>(R.id.checkbox_desarollo)
        fabricacionCheckBox = findViewById<CheckBox>(R.id.checkbox_fabricacion)
        addUpdateButton = findViewById<Button>(R.id.button_add_update_enterprise)

        newEnterprise = Enterprise()
        oldEnterprise = Enterprise()
        entrepriseData = EnterpriseOperations(this)
        entrepriseData!!.open()
        if(element == null){
            mode = "Add"
        }else{
            mode = "Update"
            addUpdateButton!!.text = "Actualizar"
            initializeEnterprise(element.id)

        }

        addUpdateButton!!.setOnClickListener {
            if (mode == "Add") {
                newEnterprise!!.name = nameEditText!!.text.toString()
                newEnterprise!!.url = urlEditText!!.text.toString()
                newEnterprise!!.phone = phoneEditText!!.text.toString()
                newEnterprise!!.email = mailEditText!!.text.toString()
                newEnterprise!!.products =productsEditText!!.text.toString()
                newEnterprise!!.classification  = getClassification()
                entrepriseData!!.addEnterprise(newEnterprise)

                val t = Toast.makeText(
                    this,
                    "Enterprise " + newEnterprise!!.name
                        .toString() + " agregado !",
                    Toast.LENGTH_SHORT
                )
                t.show()
                val i = Intent(this@NewActivity, SessionList::class.java)
                startActivity(i)
                finish();
            } else {
                oldEnterprise!!.name = nameEditText!!.text.toString()
                oldEnterprise!!.url = urlEditText!!.text.toString()
                oldEnterprise!!.phone =phoneEditText!!.text.toString()
                oldEnterprise!!.email = mailEditText!!.text.toString()
                oldEnterprise!!.products = productsEditText!!.text.toString()
                oldEnterprise!!.classification = getClassification()
                entrepriseData!!.updateEnterprise(oldEnterprise)
                val t = Toast.makeText(
                    this,
                    "Enterprise " + oldEnterprise!!.name
                        .toString() + " fue actualizado !",
                    Toast.LENGTH_SHORT
                )
                t.show()
                val i = Intent(this@NewActivity, DetailsEnterprise::class.java)
                i.putExtra("MyClass", oldEnterprise);
                startActivity(i)
                finish();
            }
        }

    }
    fun onCheckboxClicked(view: View?) {}
    private fun initializeEnterprise(entId: Long) {
        oldEnterprise = entrepriseData!!.getEnterprise(entId) as Enterprise?
        nameEditText?.setText(oldEnterprise?.name)
        urlEditText?.setText(oldEnterprise?.url)
        phoneEditText?.setText(oldEnterprise?.phone)
        mailEditText?.setText(oldEnterprise?.email)
        productsEditText?.setText(oldEnterprise?.products)
        initializeCheckbox(oldEnterprise)
    }
    private fun initializeCheckbox(ent: Enterprise?) {
        val classif: String? = ent?.classification
        if (classif?.contains("consultoria")!!) consultoriaCheckBox!!.isChecked = true
        if (classif.contains("desarollo")) desarolloCheckBox!!.isChecked = true
        if (classif.contains("fabricacion")) fabricacionCheckBox!!.isChecked = true
    }
    fun getClassification(): String? {
        var classification = ""
        if (consultoriaCheckBox?.isChecked!!) classification += "consultoria;"
        if (desarolloCheckBox?.isChecked!!) classification += "desarollo;"
        if (fabricacionCheckBox?.isChecked!!) classification += "fabricacion;"
        return classification
    }
}