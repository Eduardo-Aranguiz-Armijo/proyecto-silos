package com.duoc.silos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SilosColorScheme = lightColorScheme(
    primary = RojoPrincipal,
    onPrimary = TextoSobreRojo,
    primaryContainer = RojoContenedor,
    onPrimaryContainer = RojoOscuro,
    secondary = RojoClaro,
    onSecondary = TextoSobreRojo,
    background = BlancoFondo,
    onBackground = TextoOscuro,
    surface = BlancoSuperficie,
    onSurface = TextoOscuro,
    surfaceVariant = BlancoPuro,
    onSurfaceVariant = TextoSecundario,
    outline = GrisBorde
)

@Composable
fun MonitoreoSilosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SilosColorScheme,
        content = content
    )
}
