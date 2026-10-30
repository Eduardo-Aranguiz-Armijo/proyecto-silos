package com.duoc.silos.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.RolUsuario
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    cargando: Boolean,
    error: String?,
    onVolver: () -> Unit,
    onRegistrar: (
        nombre: String,
        email: String,
        contrasena: String,
        rol: RolUsuario,
        cargo: String
    ) -> Unit,
    onLimpiarError: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.OPERARIO) }
    var cargo by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorLocal by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Nuevo Usuario",
                        fontWeight = FontWeight.Bold,
                        color = BlancoPuro,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val mensajeError = error ?: errorLocal
            if (!mensajeError.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RojoContenedor),
                    border = BorderStroke(1.dp, RojoPrincipal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = RojoPrincipal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = mensajeError,
                            color = RojoOscuro,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Datos Personales
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. DATOS PERSONALES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            errorLocal = null
                            onLimpiarError()
                        },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("ej. Rodrigo Díaz P.") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RojoPrincipal) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoPrincipal,
                            focusedLabelColor = RojoPrincipal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorLocal = null
                            onLimpiarError()
                        },
                        label = { Text("Correo Electrónico Corporativo") },
                        placeholder = { Text("ej. usuario@silos.cl") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = RojoPrincipal) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoPrincipal,
                            focusedLabelColor = RojoPrincipal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cargo,
                        onValueChange = { cargo = it },
                        label = { Text("Cargo / Puesto de Faena") },
                        placeholder = { Text("ej. Operario Silos Melipilla") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = RojoPrincipal) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoPrincipal,
                            focusedLabelColor = RojoPrincipal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Seguridad
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "2. SEGURIDAD Y ACCESO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            errorLocal = null
                            onLimpiarError()
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RojoPrincipal) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoPrincipal,
                            focusedLabelColor = RojoPrincipal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirmarContrasena,
                        onValueChange = {
                            confirmarContrasena = it
                            errorLocal = null
                            onLimpiarError()
                        },
                        label = { Text("Confirmar Contraseña") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = RojoPrincipal) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RojoPrincipal,
                            focusedLabelColor = RojoPrincipal
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Asignación de Rol
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. ROL EN LA OPERACIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoPrincipal
                    )

                    RolUsuario.values().forEach { rol ->
                        val seleccionado = rolSeleccionado == rol
                        Surface(
                            onClick = { rolSeleccionado = rol },
                            shape = RoundedCornerShape(8.dp),
                            color = if (seleccionado) RojoContenedor else GrisFondoCard,
                            border = BorderStroke(1.dp, if (seleccionado) RojoPrincipal else GrisBorde),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = seleccionado,
                                    onClick = { rolSeleccionado = rol },
                                    colors = RadioButtonDefaults.colors(selectedColor = RojoPrincipal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = rol.titulo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (seleccionado) RojoOscuro else TextoOscuro
                                    )
                                    Text(
                                        text = rol.descripcion,
                                        fontSize = 11.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Botón Crear Cuenta
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorLocal = "Por favor ingrese su nombre completo"
                        return@Button
                    }
                    if (email.isBlank() || !email.contains("@")) {
                        errorLocal = "Por favor ingrese un correo válido"
                        return@Button
                    }
                    if (contrasena.length < 4) {
                        errorLocal = "La contraseña debe tener al menos 4 caracteres"
                        return@Button
                    }
                    if (contrasena != confirmarContrasena) {
                        errorLocal = "Las contraseñas no coinciden"
                        return@Button
                    }
                    onRegistrar(
                        nombre,
                        email,
                        contrasena,
                        rolSeleccionado,
                        if (cargo.isNotBlank()) cargo else rolSeleccionado.titulo
                    )
                },
                enabled = !cargando,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RojoPrincipal,
                    contentColor = BlancoPuro
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = BlancoPuro,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Crear Cuenta e Iniciar Sesión",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ya tengo cuenta • Volver a Iniciar Sesión",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }
        }
    }
}
