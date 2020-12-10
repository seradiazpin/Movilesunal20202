package co.edu.unal.decorar.ui.catalog

import android.content.ContentValues
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.edu.unal.decorar.MainViewModel
import co.edu.unal.decorar.R
import co.edu.unal.decorar.ui.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_catalog.*


class CatalogFragment : Fragment(), CatalogRecyclerViewAdapter.OnFurnitureListener{
    private val args: CatalogFragmentArgs by navArgs()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var catalogViewModel: CatalogViewModel

    private lateinit var catalogAdapter: CatalogRecyclerViewAdapter
    private val NUM_COLUMNS = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_catalog, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.run{
            mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Throwable("invalid activity")
        mainViewModel.updateActionBarTitle(resources.getStringArray(R.array.types)[args.choosedType])
        catalogViewModel = ViewModelProviders.of(this).get(CatalogViewModel::class.java)
        catalogViewModel.init(args.choosedType)
        catalogViewModel.furnitures.observe(viewLifecycleOwner){
            catalogAdapter.submitList(catalogViewModel.furnitures.value!!)
            catalogAdapter.notifyDataSetChanged()
        }
        initRecyclerView()
        Handler().postDelayed({
            addDataSet()
        }, 2000)
        search_name.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    catalogViewModel.filter(filtro_precio.text.toString(), filtro_material.text.toString(), filtro_marca.text.toString(), query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    catalogViewModel.filter(filtro_precio.text.toString(), filtro_material.text.toString(), filtro_marca.text.toString(), newText)
                }
                return false
            }
        })
        filter_button.setOnClickListener {
            if(filter_section.isVisible){
                catalogViewModel.filter(filtro_precio.text.toString(), filtro_material.text.toString(), filtro_marca.text.toString(), search_name.query.toString())
                val view = view?.findFocus()
                view?.let{v ->
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }else{
                expand(filter_section)
                filter_button.text = "Filtrar Busqueda"
                filter_clean_button.visibility = View.VISIBLE
            }
        }
        filter_clean_button.setOnClickListener {
            filtro_precio.setText("")
            filtro_material.setText("")
            filtro_marca.setText("")
            search_name.setQuery("",true)
            collapse(filter_section)
            filter_clean_button.visibility = View.GONE
            filter_button.text = "Expandir Filtros"
        }
    }

    private fun addDataSet(){
        val data = catalogViewModel.furnitures.value!!
        Log.d(ContentValues.TAG, data.toString())
        catalogAdapter.submitList(data)
        catalogAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView(){
        catalog_recycler_view.layoutManager = StaggeredGridLayoutManager(
            NUM_COLUMNS,
            LinearLayoutManager.VERTICAL
        )
        val topSpacingItemDecoration = TopSpacingItemDecoration(30)
        catalog_recycler_view.addItemDecoration(topSpacingItemDecoration)
        catalogAdapter = CatalogRecyclerViewAdapter(this)
        catalog_recycler_view.adapter = catalogAdapter
    }

    override fun onFurnitureClick(position: Int) {
        val action = CatalogFragmentDirections.catalogToFurnitureFragment(position)
        this.findNavController().navigate(action)
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = ((targetHeight / v.context.resources.displayMetrics.density).toLong())
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = ((initialHeight / v.context.resources.displayMetrics.density).toLong())
        v.startAnimation(a)
    }

}