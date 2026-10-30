package com.duoc.silos.ui.screens.crear

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.duoc.silos.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSiloScreen(
    granjasExistentes: List<String>,
    onVolver: () -> Unit,
    onGuardarSilo: (
        id: String,
        granja: String,
        sector: String,
        capacidadKg: Double,
        nivelInicialKg: Double,
        tipoAlimento: String,
        consumoHora: Double,
        lat: Double,
        lon: Double,
        fotoSeccionUri: String?
    ) -> Unit
) {
    val context = LocalContext.current

    var idSilo by remember { mutableStateOf("") }
    var granjaSeleccionada by remember { mutableStateOf(granjasExistentes.firstOrNull() ?: "Plantel Melipilla Central") }
    var nuevaGranjaTexto by remember { mutableStateOf("") }
    var usarNuevaGranja by remember { mutableStateOf(false) }
    var sectorPabellon by remember { mutableStateOf("") }
    var capacidadTexto by remember { mutableStateOf("22000") }
    var nivelInicialTexto by remember { mutableStateOf("15000") }
    var tipoAlimento by remember { mutableStateOf("Iniciador Aves Broiler") }
    var consumoHoraTexto by remember { mutableStateOf("120") }

    // Registro Visual Obligatorio (Foto de la sección del silo físico)
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var errorFoto by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempCameraUri != null) {
            fotoUri = tempCameraUri
            errorFoto = false
        }
    }

    // Bitmap preview
    val bitmapPreview: Bitmap? = remember(fotoUri) {
        fotoUri?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // GPS
    var latitud by remember { mutableStateOf(-33.6882) }
    var longitud by remember { mutableStateOf(-71.2185) }
    var gpsFijado by remember { mutableStateOf(false) }

    var errorId by remember { mutableStateOf(false) }
    var errorCapacidad by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Registrar Nuevo Silo", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Registro Visual y Alta de Infraestructura", fontSize = 11.sp, color = RojoContenedor)
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
        containerColor = BlancoFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sección 1: Registro Visual del Silo (OBLIGATORIO)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.5.dp, if (errorFoto) RojoPrincipal else GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = RojoPrincipal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REGISTRO VISUAL DE SILO (OBLIGATORIO)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RojoPrincipal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Para certificar que el silo corresponde a un lugar físico real, es obligatorio capturar una fotografía de una sección del silo en faena.",
                        fontSize = 12.sp,
                        color = TextoSecundario
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (bitmapPreview != null) {
                        // Previsualización de la foto capturada
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, EstadoSiloNormal, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                bitmap = bitmapPreview.asImageBitmap(),
                                contentDescription = "Fotografía de sección física del silo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "✓ Lugar físico validado fotográficamente",
                                        color = BlancoPuro,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(
                                        onClick = {
                                            val archivo = File.createTempFile("silo_registro_", ".jpg", context.cacheDir)
                                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                                            tempCameraUri = uri
                                            cameraLauncher.launch(uri)
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Retomar", color = BlancoPuro, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // Recuadro para tomar foto
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (errorFoto) RojoContenedor.copy(alpha = 0.3f) else GrisFondoCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (errorFoto) RojoPrincipal else GrisBorde)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
                                    tint = if (errorFoto) RojoPrincipal else TextoSecundario,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (errorFoto) "⚠️ Debe tomar la fotografía del silo antes de guardar" else "Sin fotografía capturada",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (errorFoto) RojoPrincipal else TextoOscuro
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val archivo = File.createTempFile("silo_registro_", ".jpg", context.cacheDir)
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                                        tempCameraUri = uri
                                        cameraLauncher.launch(uri)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RojoPrincipal,
                                        contentColor = BlancoPuro
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tomar Fotografía de Sección del Silo")
                                }
                            }
                        }
                    }
                }
            }

            // Sección 2: Identificación del Silo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "IDENTIFICACIÓN DEL SILO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = idSilo,
                        onValueChange = {
                            idSilo = it.uppercase()
                            errorId = false
                        },
                        label = { Text("Código de Silo (Ej: SILO-07)") },
                        placeholder = { Text("SILO-07") },
                        isError = errorId,
                        supportingText = {
                            if (errorId) Text("El código no puede estar vacío", color = RojoPrincipal)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = RojoPrincipal)
                        }
                    )
                }
            }

            // Sección 3: Plantel y Sector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "UBICACIÓN Y PLANTEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!usarNuevaGranja && granjasExistentes.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = granjaSeleccionada,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Plantel / Granja") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                granjasExistentes.distinct().forEach { granja ->
                                    DropdownMenuItem(
                                        text = { Text(granja) },
                                        onClick = {
                                            granjaSeleccionada = granja
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = nuevaGranjaTexto,
                            onValueChange = { nuevaGranjaTexto = it },
                            label = { Text("Nombre del Nuevo Plantel / Fundo") },
                            placeholder = { Text("Ej: Plantel Las Araucarias") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { usarNuevaGranja = !usarNuevaGranja },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (usarNuevaGranja) Icons.Default.List else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = RojoPrincipal
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (usarNuevaGranja) "Seleccionar de planteles existentes" else "Registrar en un nuevo plantel",
                            fontSize = 12.sp,
                            color = RojoPrincipal,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = sectorPabellon,
                        onValueChange = { sectorPabellon = it },
                        label = { Text("Sector o Pabellón") },
                        placeholder = { Text("Ej: Pabellón Pavos 5 y 6") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Sección 4: Capacidad y Alimento
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CAPACIDAD Y ALIMENTO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = capacidadTexto,
                            onValueChange = {
                                capacidadTexto = it
                                errorCapacidad = false
                            },
                            label = { Text("Capacidad Total (kg)") },
                            isError = errorCapacidad,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = nivelInicialTexto,
                            onValueChange = { nivelInicialTexto = it },
                            label = { Text("Nivel Inicial (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tipoAlimento,
                        onValueChange = { tipoAlimento = it },
                        label = { Text("Tipo de Alimento") },
                        placeholder = { Text("Ej: Iniciador Aves Premium") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = consumoHoraTexto,
                        onValueChange = { consumoHoraTexto = it },
                        label = { Text("Consumo Promedio Estimado (kg/hora)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Sección 5: Ubicación GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "UBICACIÓN GPS EN TERRENO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Coordenadas: Lat: $latitud · Lon: $longitud",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextoOscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            latitud = -33.6800 + (Math.random() * 0.02)
                            longitud = -71.2100 + (Math.random() * 0.02)
                            gpsFijado = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (gpsFijado) EstadoSiloNormal else RojoPrincipal
                        ),
                        border = BorderStroke(1.dp, if (gpsFijado) EstadoSiloNormal else RojoPrincipal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (gpsFijado) "Actualizar Coordenadas GPS en Faena" else "Capturar Ubicación GPS de Terreno")
                    }
                }
            }

            // Botón Guardar
            Button(
                onClick = {
                    val capacidad = capacidadTexto.toDoubleOrNull()
                    val nivel = nivelInicialTexto.toDoubleOrNull() ?: 0.0
                    val consumo = consumoHoraTexto.toDoubleOrNull() ?: 120.0
                    val granjaFinal = if (usarNuevaGranja) nuevaGranjaTexto.ifBlank { "Plantel Nuevo" } else granjaSeleccionada

                    if (fotoUri == null) {
                        errorFoto = true
                    } else if (idSilo.isBlank()) {
                        errorId = true
                    } else if (capacidad == null || capacidad <= 0) {
                        errorCapacidad = true
                    } else {
                        onGuardarSilo(
                            idSilo,
                            granjaFinal,
                            sectorPabellon,
                            capacidad,
                            nivel,
                            tipoAlimento,
                            consumo,
                            latitud,
                            longitud,
                            fotoUri.toString()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoPrincipal,
                    contentColor = BlancoPuro
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar y Registrar Silo en Planta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
