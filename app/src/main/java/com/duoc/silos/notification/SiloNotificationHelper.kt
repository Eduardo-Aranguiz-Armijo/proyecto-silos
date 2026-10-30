package com.duoc.silos.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.duoc.silos.data.model.Silo

class SiloNotificationHelper(private val context: Context) {

    companion object {
        const val CANAL_ALERTAS_ID = "canal_silos_alertas"
        const val CANAL_ALERTAS_NOMBRE = "Alertas de Silos Críticos"
        const val CANAL_ALERTAS_DESC = "Notificaciones prioritarias cuando un silo requiere reposición de alimento"
    }

    init {
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ALERTAS_ID,
                CANAL_ALERTAS_NOMBRE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CANAL_ALERTAS_DESC
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    fun dispararAlertaSilo(silo: Silo) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CANAL_ALERTAS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ ALERTA: ${silo.granja} - ${silo.id}")
            .setContentText("Nivel crítico: ${silo.porcentajeInt}% (${silo.nivelActualKg} kg restantes). Requiere reposición inmediata.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(silo.id.hashCode(), notification)
    }
}
