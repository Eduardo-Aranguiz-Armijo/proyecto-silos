package com.duoc.silos.ui.screens.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.silos.data.model.Usuario
import com.duoc.silos.ui.components.ImagenInteligente
import com.duoc.silos.ui.theme.*
import com.duoc.silos.ui.viewmodel.PerfilViewModel

/**
 * Pantalla de Perfil de Usuario con integración nativa de Cámara y Galería (Guía 13 - Parte 4).
 * Utiliza ActivityResultLauncher con TakePicture() y GetContent().
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuarioActual: Usuario?,
    onVolver: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Lanzador para seleccionar foto desde la Galería (GetContent())
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.actualizarDesdeGaleria(uri)
    }

    // Lanzador para capturar foto nueva con la Cámara (TakePicture())
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        viewModel.actualizarDesdeCamara(exito)
    }

    // Mostrar feedback en snackbar cuando se cargue la imagen
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Perfil de Usuario",
                            fontWeight = FontWeight.Bold,
                            color = BlancoPuro,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Guía 13 · Recursos Nativos (Cámara y Galería)",
                            color = RojoContenedor,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlancoPuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RojoPrincipal,
                    titleContentColor = BlancoPuro
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BlancoFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Componente ImagenInteligente (Parte 3) centrado en pantalla
            ImagenInteligente(
                imagenUri = uiState.imagenUri,
                tamano = 160.dp
            )

            // Información del usuario activo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = usuarioActual?.nombre ?: "Usuario del Sistema",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = usuarioActual?.cargo ?: "Operador Agroalimentario",
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = RojoPrincipal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, RojoBorde)
                ) {
                    Text(
                        text = usuarioActual?.rol?.titulo ?: "Operario Terreno",
                        color = RojoPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                color = GrisBorde.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "Selecciona o captura una imagen de perfil usando los recursos nativos del dispositivo Android:",
                fontSize = 13.sp,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            // Botón 1: Abrir Galería (GetContent())
            Button(
                onClick = {
                    galeriaLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoPrincipal,
                    contentColor = BlancoPuro
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Galería",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Seleccionar desde Galería",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Botón 2: Activar Cámara (TakePicture())
            OutlinedButton(
                onClick = {
                    val uri = viewModel.prepararUriCamara()
                    camaraLauncher.launch(uri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, RojoPrincipal),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = RojoPrincipal
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Cámara",
                    modifier = Modifier.size(20.dp),
                    tint = RojoPrincipal
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tomar Foto con la Cámara",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RojoPrincipal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tarjeta informativa de la Guía 13 para la evaluación
            Surface(
                color = BlancoSuperficie,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GrisBorde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📋 Verificación de Indicadores de Logro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextoOscuro
                    )
                    Text(
                        text = "• Contrato Galería: ActivityResultContracts.GetContent()\n• Contrato Cámara: ActivityResultContracts.TakePicture()\n• Almacenamiento seguro: FileProvider (${'\$'}applicationId.fileprovider)\n• Gestión reactiva: StateFlow con PerfilViewModel",
                        fontSize = 11.sp,
                        color = TextoSecundario,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
