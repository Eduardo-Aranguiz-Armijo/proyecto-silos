package com.duoc.silos.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

/**
 * Estado UI reactivo para la pantalla de Perfil (Guía 13).
 */
data class PerfilUiState(
    val imagenUri: Uri? = null,
    val tempCameraUri: Uri? = null,
    val mensaje: String? = null
)

/**
 * ViewModel que gestiona la selección y captura de imágenes nativas (Guía 13 - Parte 2).
 * Expone StateFlow para garantizar la reactividad en Jetpack Compose.
 */
class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    /**
     * Prepara un archivo temporal en caché y obtiene su Uri seguro mediante FileProvider
     * para suministrarlo al ActivityResultLauncher de la cámara (TakePicture()).
     */
    fun prepararUriCamara(): Uri {
        val context = getApplication<Application>().applicationContext
        val archivoTemporal = File.createTempFile("captura_perfil_", ".jpg", context.cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivoTemporal
        )

        _uiState.update { it.copy(tempCameraUri = uri) }
        return uri
    }

    /**
     * Actualiza la imagen cuando el usuario selecciona una foto de la galería (GetContent()).
     */
    fun actualizarDesdeGaleria(uri: Uri?) {
        if (uri != null) {
            _uiState.update {
                it.copy(
                    imagenUri = uri,
                    mensaje = "Imagen seleccionada desde la galería correctamente"
                )
            }
        }
    }

    /**
     * Actualiza la imagen cuando la cámara finaliza la captura exitosamente (TakePicture()).
     */
    fun actualizarDesdeCamara(exito: Boolean) {
        if (exito && _uiState.value.tempCameraUri != null) {
            val uriCapturada = _uiState.value.tempCameraUri
            _uiState.update {
                it.copy(
                    imagenUri = uriCapturada,
                    mensaje = "Fotografía capturada con la cámara correctamente"
                )
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
