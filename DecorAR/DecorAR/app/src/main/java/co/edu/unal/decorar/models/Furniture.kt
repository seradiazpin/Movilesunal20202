package co.edu.unal.decorar.models
enum class Type{
    FURNITURE, WALL,FLOOR
}
data class Furniture (
    var id: Int,
    var nombre: String,
    var foto: String,
    var precio: String?,
    var descripcion: String?,
    var material: String?,
    var marca: String?,
    var modelo: Int?,
    var tiendas: List<String>?,
    var tipo: Int?,
    var url: String?
){

}