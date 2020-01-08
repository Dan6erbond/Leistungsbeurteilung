package ch.bbbaden.choreapp.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import ch.bbbaden.choreapp.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import kotlinx.android.synthetic.main.dialog_fragment_qr_scan.view.*

class ScanQRDialogFragment(private val listener: ScanQRDialogListener, private val title: String) :
    DialogFragment() {

    var qrProcessed = false

    interface ScanQRDialogListener {
        fun onScanQRResult(dialog: DialogFragment, rawValue: String)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_fragment_qr_scan, null)

            view.title.text = title
            view.viewFinder.post { startCamera(view) }

            builder.setView(view)
                .setNegativeButton(
                    R.string.fui_cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    private val options = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(
            FirebaseVisionBarcode.FORMAT_QR_CODE,
            FirebaseVisionBarcode.FORMAT_AZTEC
        )
        .build()

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera(view: View) {
        context?.let {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
            val cameraSelector = CameraSelector.Builder().requireLensFacing(LensFacing.BACK).build()

            val preview = Preview.Builder().setTargetResolution(Size(320, 400)).build()
            preview.previewSurfaceProvider = view.viewFinder.previewSurfaceProvider

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.BackpressureStrategy.KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(
                { command -> command.run() },
                { image: ImageProxy, rotationDegrees: Int ->
                    val mediaImage = image.image
                    val imageRotation = degreesToFirebaseRotation(rotationDegrees)

                    if (mediaImage != null && !qrProcessed) {
                        val img = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                        val detector =
                            FirebaseVision.getInstance().getVisionBarcodeDetector(options)

                        detector.detectInImage(img)
                            .addOnSuccessListener { barcodes ->
                                barcodes.firstOrNull { barcode ->
                                    qrProcessed = true
                                    val rawValue = barcode.rawValue
                                    listener.onScanQRResult(this, rawValue!!)
                                    dialog?.cancel()
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