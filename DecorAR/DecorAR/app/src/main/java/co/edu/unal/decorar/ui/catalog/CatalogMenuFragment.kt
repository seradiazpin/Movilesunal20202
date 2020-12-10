package co.edu.unal.decorar.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import co.edu.unal.decorar.MainViewModel
import co.edu.unal.decorar.R

class CatalogMenuFragment : Fragment() {

    private lateinit var catalogViewModel: CatalogViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(

            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        catalogViewModel =
                ViewModelProviders.of(this).get(CatalogViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_catalog_menu, container, false)

        val buttonFurniture: Button = root.findViewById(R.id.furniture_catalog)
        buttonFurniture.setOnClickListener{
            val action = CatalogMenuFragmentDirections.catalogMenuToCatalogFragment(0)
            it.findNavController().navigate(action)
        }
        val buttonWall: Button = root.findViewById(R.id.wall_catalog)
        buttonWall.setOnClickListener{
            val action = CatalogMenuFragmentDirections.catalogMenuToCatalogFragment(1)
            it.findNavController().navigate(action)
        }
        val buttonFloor: Button = root.findViewById(R.id.floor_catalog)
        buttonFloor.setOnClickListener{
            val action = CatalogMenuFragmentDirections.catalogMenuToCatalogFragment(2)
            it.findNavController().navigate(action)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run{
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        mainViewModel.updateActionBarTitle(getString(R.string.title_catalog))
    }



}