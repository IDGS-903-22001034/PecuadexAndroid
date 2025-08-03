package com.zurie.pecuadexproject.mqtt

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.zurie.pecuadexproject.Data.Model.GpsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

class MqttManager(private val context: Context) {

    private val serverUri = "tcp://broker.hivemq.com:1883"
    private val clientId = "AndroidClient_${UUID.randomUUID()}"
    private val topic = "esp32/gps/data"

    private var mqttClient: MqttClient? = null
    private val gson = Gson()

    private val _gpsDataFlow = MutableStateFlow<GpsData?>(null)
    val gpsDataFlow: StateFlow<GpsData?> = _gpsDataFlow

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    fun connect() {
        try {
            val persistence = MemoryPersistence()
            mqttClient = MqttClient(serverUri, clientId, persistence)

            val options = MqttConnectOptions().apply {
                isCleanSession = true
                connectionTimeout = 30
                keepAliveInterval = 60
                isAutomaticReconnect = true
            }

            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.e("MQTT", "Conexión perdida", cause)
                    _connectionStatus.value = false
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    message?.let {
                        try {
                            val payload = String(it.payload)
                            val gpsData = gson.fromJson(payload, GpsData::class.java)
                            _gpsDataFlow.value = gpsData
                            Log.d("MQTT", "Datos GPS recibidos: $gpsData")
                        } catch (e: Exception) {
                            Log.e("MQTT", "Error al parsear datos GPS: ${e.message}")
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }
            })

            mqttClient?.connect(options)
            _connectionStatus.value = true
            Log.d("MQTT", "Conectado al broker MQTT")
            subscribeToTopic()

        } catch (e: Exception) {
            Log.e("MQTT", "Error al conectar", e)
            _connectionStatus.value = false
        }
    }

    private fun subscribeToTopic() {
        try {
            mqttClient?.subscribe(topic, 1)
            Log.d("MQTT", "Suscrito al topic: $topic")
        } catch (e: Exception) {
            Log.e("MQTT", "Error al suscribirse al topic", e)
        }
    }

    fun disconnect() {
        try {
            mqttClient?.disconnect()
            Log.d("MQTT", "Desconectado del broker MQTT")
        } catch (e: Exception) {
            Log.e("MQTT", "Error al desconectar", e)
        } finally {
            _connectionStatus.value = false
        }
    }

    fun publishMessage(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray()).apply {
                qos = 1
            }
            mqttClient?.publish(topic, mqttMessage)
            Log.d("MQTT", "Mensaje publicado exitosamente")
        } catch (e: Exception) {
            Log.e("MQTT", "Error al publicar mensaje", e)
        }
    }
}