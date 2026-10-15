package ru.mtuci.drivenext

import android.app.Application
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import ru.mtuci.drivenext.di.AppContainer
import ru.mtuci.drivenext.di.ViewModelFactory

class DriveNextApp : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    /**
     * Общий загрузчик картинок для всего приложения (Coil берёт его автоматически).
     * Фото автомобилей и аватарки хранятся в кэше в памяти и на диске: при повторном открытии
     * вкладки или профиля они не скачиваются заново, а появляются мгновенно, в том числе без сети.
     */
    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(0.25) // до четверти доступной памяти приложения
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve(IMAGE_CACHE_DIR))
                .maxSizeBytes(IMAGE_CACHE_BYTES)
                .build()
        }
        // Адреса файлов в Storage уникальны (имя с меткой времени), поэтому заголовки сервера не нужны
        .respectCacheHeaders(false)
        .build()

    private companion object {
        const val IMAGE_CACHE_DIR = "image_cache"
        const val IMAGE_CACHE_BYTES = 150L * 1024 * 1024
    }
}

/** Фабрика ViewModel для фрагментов: by viewModels { appViewModelFactory() } */
fun Fragment.appViewModelFactory(): ViewModelFactory =
    ViewModelFactory((requireActivity().application as DriveNextApp).container)
