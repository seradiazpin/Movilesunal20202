package co.edu.unal.sqllite

import java.io.Serializable

class Enterprise:Serializable {
    var id: Long = 0
    var icon: Int? = null
    var name: String? = null
    var url: String? = null
    var phone: String? = null
    var email: String? = null
    var products: String? = null
    var classification: String? = null

    constructor(
        id: Long,
        icon : Int?,
        name: String?,
        url: String?,
        phone: String?,
        email: String?,
        products: String?,
        classification: String?
    ) {
        this.id = id
        this.icon = icon
        this.name = name
        this.url = url
        this.phone = phone
        this.email = email
        this.products = products
        this.classification = classification
    }

    constructor() {}

    override fun toString(): String {

        //affichage des classification
        val tabClassif =
            classification!!.split(";".toRegex()).toTypedArray()
        var stringClassif = ""
        for (i in tabClassif.indices) stringClassif += tabClassif[i] + " | "

        //affichage des produits et services
        val tabProducts = products!!.split(" ".toRegex()).toTypedArray()
        var stringProducts = ""
        for (j in tabProducts.indices) stringProducts += """     - ${tabProducts[j]}
"""
        return """Empresa $id
  Nombre : $name
  Url : $url
  Teléfono : $phone
  Email : $email
  Productos y servicios : 
$stringProducts  Clasificacion : | $stringClassif"""
    }
}