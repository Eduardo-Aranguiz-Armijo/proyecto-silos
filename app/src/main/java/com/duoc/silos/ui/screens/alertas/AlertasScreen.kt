package com.duoc.silos.ui.screens.alertas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.silos.data.model.Alerta
import com.duoc.silos.data.model.EstadoSilo
import com.duoc.silos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    alertas: List<Alerta>,
    onVolver: () -> Unit,
    onAtenderAlerta: (String) -> Unit,
    onDespachoTolva: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Centro de Alertas & Riesgos", fontWeight = FontWeight.Bold, color = BlancoPuro)
                        Text("Monitoreo predictivo de desabastecimiento", fontSize = 11.sp, color = RojoContenedor)
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
        if (alertas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EstadoSiloNormal,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sin Alertas de Desabastecimiento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro
                    )
                    Text(
                        text = "Todos los silos operan sobre los umbrales de seguridad.",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Silos en Estado Crítico o Advertencia (${alertas.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro
                    )
                }

                items(alertas, key = { it.id }) { alerta ->
                    AlertaCardItem(
                        alerta = alerta,
                        onAtenderClick = { onAtenderAlerta(alerta.id) },
                        onDespachoClick = { onDespachoTolva(alerta.siloId) }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertaCardItem(
    alerta: Alerta,
    onAtenderClick: () -> Unit,
    onDespachoClick: () -> Unit
) {
    val esCritica = alerta.estado == EstadoSilo.CRITICO
    val colorBorde = if (esCritica) RojoPrincipal else EstadoSiloAdvertencia
    val colorBadge = if (esCritica) RojoPrincipal else EstadoSiloAdvertencia

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlancoSuperficie),
        border = BorderStroke(1.5.dp, colorBorde),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = colorBadge,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${alerta.granja} · ${alerta.siloId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextoOscuro
                    )
                }

                Surface(
                    color = colorBadge.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = alerta.estado.name,
                        color = colorBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alerta.mensaje,
                fontSize = 13.sp,
                color = TextoOscuro,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Registrada: ${alerta.fechaHora}",
                    fontSize = 11.sp,
                    color = TextoSecundario
                )
                Text(
                    text = "Agotamiento estimado: ~${alerta.horasEstimadasAgotamiento}h",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorBadge
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDespachoClick,
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RojoPrincipal,
                        contentColor = BlancoPuro
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Despachar Tolva", fontSize = 11.sp)
                }

                if (!alerta.atendida) {
                    OutlinedButton(
                        onClick = onAtenderClick,
                        modifier = Modifier.weight(0.8f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoOscuro),
                        border = BorderStroke(1.dp, GrisBorde),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("Atendida", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
