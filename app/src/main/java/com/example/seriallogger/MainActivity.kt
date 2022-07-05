package com.example.seriallogger

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class MainActivity : AppCompatActivity() {

//    UsbDevice device
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    var counter = 0
    val textArray = arrayOf<String>("Boo", "Spooks", "OoooOOoooOOOo", "Gotcha!")
    var open = false;
    lateinit var timerVar : CountDownTimer;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        yourTextView.setMovementMethod(ScrollingMovementMethod())
        val txtView = findViewById<View>(R.id.textView) as TextView
        txtView.movementMethod = ScrollingMovementMethod()
    }

    open fun closePort(button: Button, port: UsbSerialPort){
        button.text = "Begin Read"
        open = false
        port.close()
        timerVar.cancel()
    }

    fun sendMessage(view: View) {
        onClick(view)
        // Find all available drivers from attached devices.
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            updateStatus("NO DRIVER FOUND")
            return
        }

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]

        // Allows USB connected device to interact with app.
        val mPermissionIntent = PendingIntent.getBroadcast(this, 0,  Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
        manager.requestPermission(driver.device, mPermissionIntent)
        val connection = manager.openDevice(driver.device)

        val port = driver.ports[0] // Most devices have just one port (port 0)


        // Rename button!
        val button = findViewById<View>(R.id.button_id) as Button
        button.text = "Stop Read"

        // If Open, close. Otherwise normal funtion.
        if (open){
            closePort(button, port)
            return
        }
        port.open(connection)
        open = true

        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        var data = ByteArray(256)
        var len = 0;

        timerVar = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                len = port.read(data, 0);
                if (len != 0){
                    updateStatus("Len of Data: " + len.toString())
                    updateStatus(String(data!!))
                }
            }

            override fun onFinish() {
                len = port.read(data, 0);
                if (len != 0){
                    updateStatus("Len of Data: " + len.toString())
                    updateStatus(String(data!!))
                }
                button.text = "Begin Read"
                open = false
                port.close()
            }
        }.start()

//        val listen = MutableLiveData<Int>()
//
//        listen.setValue(0) //Initilize with a value
//
//
//        listen.observe(MainActivity.this) {
//            //Do something with the changed value
//        }
//
//        //Listener for listening the changes
//        //Listener for listening the changes
//        listen.observe(this@MainActivity,
//            Observer<Int> {
//                Toast.makeText(this@MainActivity, listen.value!!, Toast.LENGTH_LONG).show()
//            })
//        onNewData(data)

    }
//
//    open fun onNewData(data: ByteArray?) {
////        val txtView = findViewById<View>(R.id.text_id) as TextView
////        runOnUiThread { txtView.append(String(data!!)) }
//
//        runOnUiThread {  updateStatus(String(data!!)) }
//    }

    fun updateStatus(status: String?) {
        val txtView = findViewById<View>(R.id.textView) as TextView
        var resString: String = txtView.getText().toString()
        resString += "\n\t"
        resString += status
        txtView.text = resString
        val scrollAmount: Int =
            txtView.getLayout().getLineTop(txtView.getLineCount()) - txtView.getHeight()
        txtView.scrollTo(0, scrollAmount)
    }

    fun onClick(view: View?) {
        val txtView = findViewById<View>(R.id.text_id) as TextView
        txtView.text = textArray[counter];
        counter += 1
        if (counter > 3){
            counter = 0;
        }
    }
}

//fun printStarter(view: View?) {
//    val text = findViewById(R.id.fullscreen_content) as TextView
//    text.text = "Some text...."
//}
//
//class MyActivity : Activity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val button: Button = findViewById(R.id.button_id)
//        button.setOnClickListener(object : OnClickListener() {
//            fun onClick(v: View?) {
//                // Code here executes on main thread after user presses button
//            }
//        })
//    }
//}