package co.edu.unal.decorar.ui.camera

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import co.edu.unal.decorar.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import java.util.function.Function
import kotlin.collections.ArrayList

class CameraFragment : Fragment(), Scene.OnUpdateListener {

    private lateinit var cameraViewModel: CameraViewModel
    private var arFragment: ArFragment? = null
    private var renderableObject: ModelRenderable? = null
    private var nodesList: ArrayList<TransformableNode>? = null

    var originalMaterial: Material? = null
    var planeRenderable: ModelRenderable? = null
    var materialTexture: Texture? = null


    /**
     * Load camera fragment component's
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cameraViewModel =
            ViewModelProviders.of(this).get(CameraViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_camera, container, false)
        if (!checkIsSupportedDeviceOrFinish(activity as Activity)) {
            Toast.makeText(activity?.applicationContext, "Device not supported", Toast.LENGTH_LONG)
                .show()
        }
        nodesList = ArrayList<TransformableNode>()
        val id: Int?
        val Titulo: String
        val tipo: Int?
        val url: String
        if (arguments != null) {
            id = arguments?.getSerializable("modelo") as Int
            Titulo = arguments?.getSerializable("nombre") as String
            tipo = arguments?.getSerializable("tipo") as Int
            url = arguments?.getSerializable("url") as String

        } else {
            id = 1
            Titulo = "Camara Test"
            tipo = 1
            url = "https://github.com/yudiz-solutions/runtime_ar_android/raw/master/model/model.gltf"
        }
        arFragment = childFragmentManager.findFragmentById(R.id.arView) as ArFragment?
        if (tipo == 0) {
            loadModel(url)
        }
        if (tipo == 1) {
             addPlaneWallToModel(url)

        }
        if (tipo == 2) {
           addPlaneFloorToModel(url)
        }

        setArFragmentListener(Titulo)


        return root
    }



    /**
     * add plane with floor texture to model
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addPlaneFloorToModel(url: String) {
        val sampler: Texture.Sampler = Texture.Sampler.builder()
            .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
            .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
            .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build()

        Texture.builder()
            .setSampler(sampler)
            .setSource(context,  Uri.parse(url))
            .build()
            .thenCompose { texture ->
                MaterialFactory.makeOpaqueWithTexture(context, texture)
            }
            .thenAccept { material ->
                val cubeSize = Vector3(1f, 0f, 1f)
                val cubePosition = Vector3(0f, 0.5f, 0f)
                renderableObject =
                    ShapeFactory.makeCube(cubeSize, cubePosition, material)
                renderableObject!!.isShadowCaster = false
                renderableObject!!.isShadowReceiver = false
            }

    }

    /**
     * add plane with floor texture to model
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun addPlaneWallToModel(url: String) {
        if (url != null) {
            val sampler: Texture.Sampler = Texture.Sampler.builder()
                .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build()

            Texture.builder()
                .setSampler(sampler)
                .setSource(context,  Uri.parse(url))
                .build()
                .thenCompose { texture ->
                    MaterialFactory.makeOpaqueWithTexture(context, texture)
                }
                .thenAccept { material ->
                    val cubeSize = Vector3(1f, 2f, 0f)
                    val cubePosition = Vector3(0f, 1f, 0f)
                    renderableObject =
                        ShapeFactory.makeCube(cubeSize, cubePosition, material)
                    renderableObject!!.isShadowCaster = false
                    renderableObject!!.isShadowReceiver = false


                }

        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setArFragmentListener(text: String) {
        arFragment!!.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            if (renderableObject != null) {

                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment!!.arSceneView.scene)
                val node = TransformableNode(arFragment!!.transformationSystem)

                node.renderable = renderableObject
                node.setParent(anchorNode)
                placeObject(arFragment!!, anchorNode, text)
                if (nodesList?.size!! == 2) {
                    clearAnchors()
                }
                node.scaleController.maxScale = 0.9f;
                node.scaleController.minScale = 0.2f;

                arFragment!!.arSceneView.scene.addChild(anchorNode)
                node.select()
                if (nodesList!!.size < 2) {
                    nodesList!!.add(node)
                    arFragment!!.arSceneView.scene.addOnUpdateListener(this)
                }
            }


        }
    }

    /**
     * Load the 3D model bu id
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadModel(modelLink: String) {
        MaterialFactory.makeOpaqueWithColor(
            context,
            com.google.ar.sceneform.rendering.Color(Color.RED)
        )
            .thenAccept { material ->
                ModelRenderable.builder() // To load as an asset from the 'assets' folder ('src/main/assets/andy.sfb'):
                    .setSource(context,  RenderableSource.builder().setSource(
                        context,
                        Uri.parse(modelLink),
                        RenderableSource.SourceType.GLTF2).build())
                    .setRegistryId(modelLink)
                    .build()
                    .thenAccept(Consumer { renderable: ModelRenderable ->
                        renderableObject = renderable
                    })
                    .exceptionally(
                        Function<Throwable, Void?> { throwable: Throwable? ->
                            Log.e(TAG, "Unable to load Renderable.", throwable)
                            null
                        }
                    )

                originalMaterial = material

                renderableObject!!.isShadowCaster = false
                renderableObject!!.isShadowReceiver = false

            }
    }

    /**
     * Check If the device have OpenGL
     */
    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {

        val openGlVersionString =
            (Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Toast.makeText(
                activity,
                "Sceneform requires OpenGL ES 3.0 or later",
                Toast.LENGTH_LONG
            )
                .show()
            activity.finish()
            return false
        }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun placeObject(fragment: ArFragment, anchor: AnchorNode, title: String) {
        ViewRenderable.builder()
            .setView(fragment.context, R.layout.controls_layout)
            .build()
            .thenAccept {
                val text = it.view.findViewById<TextView>(R.id.titleHeader)
                text.text = title
                it.isShadowCaster = false
                it.isShadowReceiver = false
                it.view.findViewById<ImageButton>(R.id.info_button).setOnClickListener {
                    Toast.makeText(context, "Return to view", Toast.LENGTH_LONG)
                        .show()

                }
                it.view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
                    clearAnchors()
                }
                addControlsToScene(fragment, anchor, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }


    private fun addControlsToScene(
        fragment: ArFragment,
        anchor: AnchorNode,
        renderable: Renderable
    ) {
        val node = TransformableNode(fragment.transformationSystem)
        node.worldPosition = Vector3(0f, 1f, 0f)
        node.renderable = renderable
        node.setParent(anchor)
    }

    /**
     * Load Floor Texture by id
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadFloor(id: Int) {
        // Build texture sampler
        val sampler: Texture.Sampler = Texture.Sampler.builder()
            .setMinFilter(Texture.Sampler.MinFilter.LINEAR)
            .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
            .setWrapMode(Texture.Sampler.WrapMode.REPEAT).build()

        // Build texture with sampler
        val trigrid: CompletableFuture<Texture> = Texture.builder()
            .setSource(context, id)
            .setSampler(sampler).build()

        // Set plane texture
        arFragment?.arSceneView?.planeRenderer?.material?.thenAcceptBoth(trigrid)
        { material, texture ->
            material.setTexture(
                PlaneRenderer.MATERIAL_TEXTURE,
                texture
            )
        }
    }

    /**
     * Clear all anchors on the nodeList
     */
    private fun clearAnchors() {
        for (nodeElement in nodesList!!) {
            arFragment!!.arSceneView.scene.removeChild(nodeElement!!.parent!!)
        }
        nodesList = ArrayList<TransformableNode>()
    }

    override fun onUpdate(frameTime: FrameTime) {

    }

    companion object {
        private val MIN_OPENGL_VERSION = 3.0
    }


}