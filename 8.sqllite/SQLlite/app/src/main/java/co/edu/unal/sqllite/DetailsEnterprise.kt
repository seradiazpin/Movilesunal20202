package co.edu.unal.sqllite


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.nio.file.Files.delete


class DetailsEnterprise : AppCompatActivity() {
    private var entrepriseData: EnterpriseOperations? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_enterprise)
        var element =  intent.getSerializableExtra("MyClass") as Enterprise;
        val textViewName = findViewById<TextView>(R.id.text_name)
        val textViewUrl = findViewById<TextView>(R.id.text_url)
        val textviewPhone = findViewById<TextView>(R.id.text_phone)
        val textviewEmail = findViewById<TextView>(R.id.text_mail)
        val textviewProcucts = findViewById<TextView>(R.id.text_products)
        val textviewClassification = findViewById<TextView>(R.id.text_classification)
        val editbutton = findViewById<Button>(R.id.button_edit_enterprise)
        val deletebutton = findViewById<Button>(R.id.button_delete_enterprise)
        textViewName.text = element.name
        textViewUrl.text = element.url
        textviewPhone.text = element.phone
        textviewEmail.text = element.email
        textviewProcucts.text = element.products
        textviewClassification.text = element.classification
        entrepriseData = EnterpriseOperations(this)
        entrepriseData!!.open()
        editbutton!!.setOnClickListener {
            val intent = Intent(this@DetailsEnterprise, NewActivity::class.java)
            intent.putExtra("MyClass", element);
            startActivity(intent)
            finish();
        }
        deletebutton!!.setOnClickListener {
            var alert = AlertDialog.Builder(this@DetailsEnterprise) // set message, title, and icon
                .setTitle("Borrar")
                .setMessage("Confirmar eliminar")
                .setIcon(R.drawable.delete)
                .setPositiveButton("Borrar",
                    DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
                        dialog.dismiss()
                        entrepriseData!!.removeEnterprise(element)
                        val intent = Intent(this@DetailsEnterprise, SessionList::class.java)
                        startActivity(intent)
                        finish();
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()
            alert.show()
        }
    }
}
