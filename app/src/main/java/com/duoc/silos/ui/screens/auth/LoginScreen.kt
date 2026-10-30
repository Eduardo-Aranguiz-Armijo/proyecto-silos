package com.duoc.silos.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.R
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    cargando: Boolean,
    error: String?,
    onIniciarSesion: (email: String, contrasena: String) -> Unit,
    onAccesoRapido: (rol: RolUsuario) -> Unit,
    onIrARegistro: () -> Unit,
    onLimpiarError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var mostrarDialogoSoporte by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BlancoFondo
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Encabezado con Logo y Marca
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BlancoSuperficie,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, RojoBorde),
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_silos),
                            contentDescription = "Logo Silos AgroFeed",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = RojoPrincipal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, RojoBorde)
                ) {
                    Text(
                        text = "AGROFEED 360",
                        color = RojoPrincipal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Monitoreo de Silos",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )

                Text(
                    text = "Control de Tolvas, Telemetría y Stock en Tiempo Real",
                    fontSize = 12.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje de Error
                if (!error.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = RojoContenedor),
                        border = BorderStroke(1.dp, RojoPrincipal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = RojoPrincipal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = error,
                                color = RojoOscuro,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onLimpiarError,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = RojoOscuro,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Tarjeta de Formulario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, GrisBorde)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Acceso a la Plataforma",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )

                        // Campo Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (error != null) onLimpiarError()
                            },
                            label = { Text("Correo Electrónico") },
                            placeholder = { Text("ej. operario@silos.cl") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = RojoPrincipal
                                )
                            },
                            trailingIcon = {
                                if (email.isNotEmpty()) {
                                    IconButton(onClick = { email = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RojoPrincipal,
                                focusedLabelColor = RojoPrincipal,
                                cursorColor = RojoPrincipal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Campo Contraseña
                        OutlinedTextField(
                            value = contrasena,
                            onValueChange = {
                                contrasena = it
                                if (error != null) onLimpiarError()
                            },
                            label = { Text("Contraseña") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = RojoPrincipal
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (email.isNotBlank() && contrasena.isNotBlank()) {
                                        onIniciarSesion(email, contrasena)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RojoPrincipal,
                                focusedLabelColor = RojoPrincipal,
                                cursorColor = RojoPrincipal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botón Ingresar
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onIniciarSesion(email, contrasena)
                            },
                            enabled = !cargando && email.isNotBlank() && contrasena.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RojoPrincipal,
                                contentColor = BlancoPuro,
                                disabledContainerColor = RojoPrincipal.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (cargando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = BlancoPuro,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Iniciar Sesión",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        // Enlace Registrarse
                        OutlinedButton(
                            onClick = onIrARegistro,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, RojoBorde),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoPrincipal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Registrar Nuevo Usuario",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Acceso Rápido por Rol (Para Evaluación y Pruebas Rápidas)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                    border = BorderStroke(1.dp, RojoBorde)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = RojoPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Acceso Rápido de Prueba (1 Toque)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RojoPrincipal
                            )
                        }

                        Text(
                            text = "Presione un perfil para ingresar automáticamente sin escribir contraseña:",
                            fontSize = 11.sp,
                            color = TextoSecundario,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // 3 Perfiles de acceso rápido
                        RolQuickAccessItem(
                            titulo = "Operario de Terreno",
                            usuario = "operario@silos.cl (Rodrigo Díaz)",
                            icono = Icons.Default.Engineering,
                            colorFondo = Color(0xFFFBE9E7),
                            colorTexto = Color(0xFFD84315),
                            onClick = {
                                email = "operario@silos.cl"
                                contrasena = "operario123"
                                onAccesoRapido(RolUsuario.OPERARIO)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RolQuickAccessItem(
                            titulo = "Supervisor de Planta",
                            usuario = "supervisor@silos.cl (Carolina Morales)",
                            icono = Icons.Default.Sensors,
                            colorFondo = Color(0xFFE8F5E9),
                            colorTexto = Color(0xFF2E7D32),
                            onClick = {
                                email = "supervisor@silos.cl"
                                contrasena = "supervisor123"
                                onAccesoRapido(RolUsuario.SUPERVISOR)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RolQuickAccessItem(
                            titulo = "Jefatura de Logística",
                            usuario = "jefatura@silos.cl (Gonzalo Valenzuela)",
                            icono = Icons.Default.Insights,
                            colorFondo = Color(0xFFE3F2FD),
                            colorTexto = Color(0xFF1565C0),
                            onClick = {
                                email = "jefatura@silos.cl"
                                contrasena = "jefatura123"
                                onAccesoRapido(RolUsuario.JEFATURA)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Soporte
                TextButton(
                    onClick = { mostrarDialogoSoporte = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextoSecundario
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "¿Problemas para ingresar o recuperar clave?",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AgroFeed 360 v2.4 • Duoc UC Melipilla",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }

    // Diálogo de Soporte y Ayuda
    if (mostrarDialogoSoporte) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSoporte = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = RojoPrincipal,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Soporte de Planta & Accesos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Para restablecer credenciales corporativas o solicitar acceso a nuevos silos, contacte al administrador de turno:",
                        fontSize = 13.sp,
                        color = TextoOscuro
                    )
                    Divider(color = GrisBorde)
                    Text(
                        text = "📞 Mesa de Control Planta: +56 9 8841 2901",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "✉️ Correo TI: soporte@agrofeed360.cl",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "📍 Base de Datos: SQLite local cifrada en terminal faena.",
                        fontSize = 11.sp,
                        color = TextoSecundario
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoSoporte = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoPrincipal)
                ) {
                    Text("Entendido", color = BlancoPuro)
                }
            }
        )
    }
}

@Composable
private fun RolQuickAccessItem(
    titulo: String,
    usuario: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorFondo: Color,
    colorTexto: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = GrisFondoCard,
        border = BorderStroke(1.dp, GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorTexto,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = usuario,
                    fontSize = 11.sp,
                    color = TextoSecundario
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
