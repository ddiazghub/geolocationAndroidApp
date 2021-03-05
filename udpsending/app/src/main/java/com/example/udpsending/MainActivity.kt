package com.example.udpsending

import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class SoftOptions {
    var RemoteHost: String = "10.90.149.123"
    var RemotePort: Int = 50000

    constructor()
}


// Global
val Settings = SoftOptions()


open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("Create Runnable example.")
        val threadWithRunnable = Thread(udp_DataArrival())
        threadWithRunnable.start()

        // Add text to textView1.
        val textView = findViewById<TextView>(R.id.textView1)
        textView.setText("Hello World from main!\n")

        println("MainActivity onCreate success.")
    }

    fun clickButtonSend(view: View) {
        // Do something in response to button
        // Send editText1 Text thru UDP.
        val editText = findViewById<EditText>(R.id.editText1)
        var message = editText.text.toString()
        val editTextIP = findViewById<EditText>(R.id.editTextIP)
        val editTextPort = findViewById<EditText>(R.id.editTextPort)
        Settings.RemoteHost = editTextIP.text.toString()
        Settings.RemotePort = editTextPort.text.toString().toInt()
        sendUDP(message)
        // Add text to textView1.
//        val textView = findViewById<TextView>(R.id.textView1)
//        var chat = textView.text.toString()
//        textView.setText(chat + message + "\n")
        // Clear editText1 after all sent.
        editText.setText("")// Clear Input text.
    }

    fun sendUDP(messageStr: String) {
        // Hack Prevent crash (sending should be done using an async task)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            //Open a port to send the package
            val socket = DatagramSocket()
            val sendData = messageStr.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName(Settings.RemoteHost), Settings.RemotePort)
            socket.send(sendPacket)
            println("fun sendBroadcast: packet sent to: " + InetAddress.getByName(Settings.RemoteHost) + ":" + Settings.RemotePort)
        } catch (e: IOException) {
            //            Log.e(FragmentActivity.TAG, "IOException: " + e.message)
        }
    }

    open fun receiveUDP() {
        val buffer = ByteArray(2048)
        var socket: DatagramSocket? = null
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = DatagramSocket(Settings.RemotePort, InetAddress.getByName(Settings.RemoteHost))
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            println("open fun receiveUDP packet received = " + packet.data)

        } catch (e: Exception) {
            println("open fun receiveUDP catch exception." + e.toString())
            e.printStackTrace()
        } finally {
            socket?.close()
        }
    }

}



class udp_DataArrival: Runnable, MainActivity() {
    public override fun run() {
        println("${Thread.currentThread()} Runnable Thread Started.")
        while (true){
            receiveUDP()
        }
    }
}