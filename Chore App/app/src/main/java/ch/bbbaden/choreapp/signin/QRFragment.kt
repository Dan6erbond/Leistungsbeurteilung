package ch.bbbaden.choreapp.signin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ch.bbbaden.choreapp.R
import ch.bbbaden.choreapp.child.ChildActivity
import ch.bbbaden.choreapp.models.Child
import ch.bbbaden.choreapp.models.ChildDAO
import ch.bbbaden.choreapp.models.Parent
import ch.bbbaden.choreapp.models.ParentDAO
import ch.bbbaden.choreapp.parent.ParentActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.android.synthetic.main.fragment_qr.*

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class QRFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    @androidx.camera.core.ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            // Request camera permissions
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        qr_progressBar.isVisible = false

        auth = FirebaseAuth.getInstance()
        functions = FirebaseFunctions.getInstance()
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    activity!!,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity!!, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val options = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(
            FirebaseVisionBarcode.FORMAT_QR_CODE,
            FirebaseVisionBarcode.FORMAT_AZTEC
        )
        .build()

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    private fun getToken(uid: String): Task<String> {
        val data = hashMapOf(
            "uid" to uid
        )
        return functions
            .getHttpsCallable("getToken")
            .call(data)
            .continueWith { task ->
                val d = task.result?.data as Map<*, *>
                d["token"] as String
            }
    }

    private fun startParentActivity(parent: Parent) {
        activity?.let {
            val intent = Intent(it, ParentActivity::class.java)
            intent.putExtra("user", parent)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            it.startActivity(intent)
        }
    }

    private fun startChildActivity(child: Child) {
        activity?.let {
            val intent = Intent(it, ChildActivity::class.java)
            intent.putExtra("user", child)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            it.startActivity(intent)
        }
    }

    private fun signInQR(rawValue: String?, callback: ((success: Boolean) -> Unit)? = null) {
        val regex = Regex("(parentuid|childuid):(\\w+)")

        rawValue?.let { rv ->
            val matchResult = regex.find(rv)

            matchResult?.let { mr ->
                getToken(mr.groupValues[2]).addOnSuccessListener { t ->
                    auth.signInWithCustomToken(t).addOnSuccessListener {
                        when (matchResult.groupValues[1]) {
                            "parentuid" -> {
                                ParentDAO().getParent(mr.groupValues[2]) {
                                    if (it != null) {
                                        Toast.makeText(
                                            activity!!,
                                            "Successfully logged in as ${auth.currentUser?.email}.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        callback?.invoke(true)
                                        startParentActivity(it)
                                    } else {
                                        callback?.invoke(false)
                                    }
                                }
                            }
                            "childuid" -> {
                                ChildDAO().getChild(mr.groupValues[2]) {
                                    if (it != null) {
                                        Toast.makeText(
                                            activity!!,
                                            "Successfully logged in as child ${auth.currentUser?.uid}.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        callback?.invoke(true)
                                        startChildActivity(it)
                                    } else {
                                        callback?.invoke(false)
                                    }
                                }
                            }
                        }
                    }.addOnFailureListener {
                        Log.e(this::class.simpleName, it.message ?: it.toString())
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera() {
        context?.let {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
            val cameraSelector = CameraSelector.Builder().requireLensFacing(LensFacing.BACK).build()

            val preview = Preview.Builder().setTargetResolution(Size(320, 400)).build()
            preview.previewSurfaceProvider = viewFinder.previewSurfaceProvider

            var processing = false

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.BackpressureStrategy.KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(
                { command -> command.run() },
                { image: ImageProxy, rotationDegrees: Int ->
                    val mediaImage = image.image
                    val imageRotation = degreesToFirebaseRotation(rotationDegrees)

                    if (mediaImage != null && !processing) {
                        val img = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                        val detector =
                            FirebaseVision.getInstance().getVisionBarcodeDetector(options)

                        detector.detectInImage(img)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull { barcode ->
                                    val rawValue = barcode.rawValue

                                    rawValue?.let { rv ->
                                        val regex = Regex("(?:parentuid|childuid):(\\w+)")
                                        val matchResult = regex.find(rv)

                                        matchResult?.let { mr ->
                                            processing = true

                                            qr_progressBar.isVisible = true
                                            viewFinder.isVisible = false

                                            signInQR(rawValue) { success ->
                                                if (!success) {
                                                    processing = false

                                                    qr_progressBar.isVisible = false
                                                    viewFinder.isVisible = true

                                                    Toast.makeText(
                                                        activity!!,
                                                        "Couldn't login with given QR-Code.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } // in else case we would be in another activity
                                            }
                                        }
                                    }
                                    true
                                }
                            }
                            .addOnFailureListener {
                                Log.e(this::class.simpleName, it.message ?: it.toString())
                            }
                            .addOnCompleteListener {
                                image.close()
                            }
                    }
                })

            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
            }, ContextCompat.getMainExecutor(it))
        }
    }
}
