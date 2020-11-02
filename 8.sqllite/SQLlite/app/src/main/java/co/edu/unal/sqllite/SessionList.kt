package co.edu.unal.sqllite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text


class SessionList : AppCompatActivity() {

    private var listView: ListView? = null
    private var enterprises = mutableListOf<Enterprise>()
    private var operations: EnterpriseOperations? = null

    private var consultoriaCheckBox: CheckBox? = null
    private var desarolloCheckBox: CheckBox? = null
    private var fabricacionCheckBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_list)
        val button: (FloatingActionButton) = findViewById(R.id.create_session)
        val filter = findViewById<Button>(R.id.filte)
        operations = EnterpriseOperations(this)
        operations!!.open()
        enterprises = operations!!.allEnterprises
        operations!!.close()
        consultoriaCheckBox = findViewById<CheckBox>(R.id.checkbox_consultoria)
        desarolloCheckBox = findViewById<CheckBox>(R.id.checkbox_desarollo)
        fabricacionCheckBox = findViewById<CheckBox>(R.id.checkbox_fabricacion)

        createList()
        button.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, "New", Toast.LENGTH_SHORT)
            val intent = Intent(baseContext, NewActivity::class.java)
            startActivity(intent)
        })
        filter.setOnClickListener {
            operations!!.open()
            enterprises = operations!!.allEnterprises
            operations!!.close()
            val name = findViewById<TextView>(R.id.edit_text_filtro)
            var clasi = getClassification()
            if (name.text.isNotEmpty() || clasi?.length != 0) {

                if (name.text.isNotEmpty()) {
                    enterprises = enterprises.filter {
                        it.name?.contains(name.text) ?: false
                    } as MutableList<Enterprise>
                }
                if (clasi?.length != 0) {
                    enterprises = enterprises.filter {
                        it.classification?.let { it1 -> clasi.toString().contains(it1) } ?: false
                    } as MutableList<Enterprise>
                    println(enterprises)
                }

            }
            createList()
        }
    }

    fun getClassification(): String? {
        var classification = ""
        if (consultoriaCheckBox?.isChecked!!) classification += "consultoria;"
        if (desarolloCheckBox?.isChecked!!) classification += "desarollo;"
        if (fabricacionCheckBox?.isChecked!!) classification += "fabricacion;"
        return classification
    }

    fun createList() {
        val customList = CustomList(this, enterprises)

        listView = findViewById(R.id.listView)
        listView!!.adapter = customList

        listView!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                getApplicationContext(),
                "Selecciono " + enterprises[i].name ,
                Toast.LENGTH_SHORT
            ).show();
            val intent = Intent(baseContext, DetailsEnterprise::class.java)
            intent.putExtra("MyClass", enterprises[i]);
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        operations?.open()
        enterprises = operations!!.allEnterprises
        operations!!.close()
        createList()
    }

    override fun onPause() {
        super.onPause()
        operations?.close()

    }

    fun onCheckboxClicked(view: View?) {}
}