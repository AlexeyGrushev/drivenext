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
 * Выбор нескольких фото (ЛР5):
 *  - из галереи за один раз: контракт PickMultipleVisualMedia возвращает СПИСОК Uri;
 *  - с камеры серией: TakePicture за один запуск делает один снимок, поэтому для каждого
 *    снимка заранее готовится свой Uri (FileProvider), а после успешного кадра предлагается следующий.
 * Создавать нужно как свойство фрагмента, до его старта.
 */
class MultiPhotoPicker(private val fragment: Fragment, private val maxCount: Int) {

    private var onPicked: ((List<Uri>) -> Unit)? = null
    private var remaining = 0
    private var pendingCameraUri: Uri? = null

    // maxItems — ограничение на число выбранных изображений, его задаёт сам контракт
    private val galleryLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxCount)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) onPicked?.invoke(uris.take(remaining))
    }

    private val cameraLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        // Неиспользованный (отменённый) Uri в список не попадает
        val uri = pendingCameraUri ?: return@registerForActivityResult
        if (success) {
            onPicked?.invoke(listOf(uri))
            remaining--
            if (remaining > 0) askForNextShot()
        }
    }

    /** Предлагает источник: галерея (несколько фото) или камера (серия снимков). */
    fun pick(remaining: Int, onPicked: (List<Uri>) -> Unit) {
        if (remaining <= 0) return
        this.onPicked = onPicked
        this.remaining = remaining
        val items = arrayOf(
            fragment.getString(R.string.car_photos_from_gallery),
            fragment.getString(R.string.car_photos_from_camera),
        )
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(fragment.getString(R.string.car_photos_source_title, remaining))
            .setItems(items) { _, which ->
                if (which == 0) {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    takeNextShot()
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    private fun takeNextShot() {
        val context = fragment.requireContext()
        val directory = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File.createTempFile("car_", ".jpg", directory)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingCameraUri = uri
        cameraLauncher.launch(uri)
    }

    private fun askForNextShot() {
        MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(R.string.car_photos_more_title)
            .setMessage(fragment.getString(R.string.car_photos_more_text, maxCount - remaining, maxCount))
            .setPositiveButton(R.string.car_photos_more_yes) { _, _ -> takeNextShot() }
            .setNegativeButton(R.string.car_photos_more_no, null)
            .show()
    }
}
