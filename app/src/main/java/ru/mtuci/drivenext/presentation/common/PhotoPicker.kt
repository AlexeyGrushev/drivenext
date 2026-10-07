package ru.mtuci.drivenext.presentation.common

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import ru.mtuci.drivenext.R

/**
 * Выбор фото: из галереи (Photo Picker, разрешения не нужны) или снимок камерой.
 * Результат приходит в виде Uri — адреса файла, а не самого файла.
 * Создавать нужно как свойство фрагмента: контракты регистрируются до его старта.
 */
class PhotoPicker(private val fragment: Fragment) {

    private var onPicked: ((Uri) -> Unit)? = null
    private var pendingCameraUri: Uri? = null

    private val galleryLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) onPicked?.invoke(uri)
    }

    private val cameraLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) pendingCameraUri?.let { onPicked?.invoke(it) }
    }

    fun pick(onPicked: (Uri) -> Unit) {
        this.onPicked = onPicked
        val items = arrayOf(
            fragment.getString(R.string.photo_source_gallery),
            fragment.getString(R.string.photo_source_camera),
        )
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(R.string.photo_source_title)
            .setItems(items) { _, which ->
                if (which == 0) openGallery() else openCamera()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun openCamera() {
        val context = fragment.requireContext()
        val directory = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File.createTempFile("photo_", ".jpg", directory)
        // Камера записывает снимок только по Uri из FileProvider
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }
}
