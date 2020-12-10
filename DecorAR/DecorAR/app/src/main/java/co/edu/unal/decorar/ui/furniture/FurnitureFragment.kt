package co.edu.unal.decorar.ui.furniture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import co.edu.unal.decorar.MainViewModel
import co.edu.unal.decorar.R
import co.edu.unal.decorar.models.Furniture
import co.edu.unal.decorar.repositories.FurnitureRepository
import co.edu.unal.decorar.ui.camera.CameraFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class FurnitureFragment :  Fragment(){
    private val args: FurnitureFragmentArgs by navArgs()
    private lateinit var furniture: Furniture
    private lateinit var mainViewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_furniture, container, false)
        val nameView: TextView = root.findViewById(R.id.furniture_name)
        val imageView: ImageView = root.findViewById(R.id.furniture_image)
        val priceView: TextView = root.findViewById(R.id.furniture_price)
        val descriptionView: TextView = root.findViewById(R.id.furniture_description)
        val materialView: TextView = root.findViewById(R.id.furniture_material)
        val brandView: TextView = root.findViewById(R.id.furniture_brand)

        furniture = FurnitureRepository.getSingleFurniture(args.furnitureID)
        nameView.text = furniture.nombre
        priceView.text = furniture.precio
        descriptionView.text = furniture.descripcion
        materialView.text = furniture.material
        brandView.text = furniture.marca

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(imageView.context)
            .applyDefaultRequestOptions(requestOptions)
            .load(furniture.foto)
            .into(imageView)

        activity?.run{
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        mainViewModel.updateActionBarTitle(furniture.nombre)
        val buttonFloor: Button = root.findViewById(R.id.colocar)
        buttonFloor.setOnClickListener{
            val cameraFragment = CameraFragment()
            val args = Bundle()
            furniture.modelo?.let { it1 -> args.putInt("modelo", it1) }
            furniture.nombre?.let { it1 -> args.putString("nombre", it1) }
            furniture.tipo?.let { it1 -> args.putInt("tipo", it1) }
            furniture.url?.let { it1 -> args.putString("url", it1) }
            cameraFragment.arguments = args
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, cameraFragment)
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit()
        }
        val buttonStore: Button = root.findViewById(R.id.donde_comprar)
        buttonStore.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(furniture.tiendas?.get(0)))
            startActivity(browserIntent)
        }
        return root
    }
}