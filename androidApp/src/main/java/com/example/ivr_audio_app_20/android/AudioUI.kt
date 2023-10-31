package com.example.ivr_audio_app.android

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivr_call_app_20.android.Bluetooth.msgupdate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import java.time.LocalTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun UI() {
    var servicescheduled by remember {
        mutableStateOf(false)
    }
    var power by remember {
        mutableStateOf("OFF")
    }

    var context = LocalContext.current

    val message by mybluetooth.mymessage.collectAsState()
//    var context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White), contentAlignment = Alignment.Center)
    {


        Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
            Text(text = "Audio App", fontSize = 50.sp, fontWeight = FontWeight.ExtraBold,color = Color.Black)
            Spacer(modifier = Modifier.height(40.dp))
                Card(
                    Modifier
                        .wrapContentSize()
                        .clickable {

                        }, backgroundColor = if(power == "OFF")
                {
                    Color.Gray
                }
                else
                {
                    Color.Green
                }) {
                    Text(text = power, fontSize = 40.sp,modifier = Modifier.padding(20.dp), color = Color.Black)
                }


        }

    }

    LaunchedEffect(key1 = message)
    {
        when(message)
        {
            msgupdate(1,"ON") ->
            {
                power = "ON"
                mybluetooth.shareit.write(msgupdate(1,"ON"))
                if(!servicescheduled)
                {
                    Toast.makeText(context,"system started",Toast.LENGTH_SHORT).show()
                    servicescheduled = true
                    try {
                        scheduleservice(context,"ACTION_START_SERVICE")


                    }
                    catch (e: Exception)
                    {
                        Toast.makeText(context,e.message.toString(),Toast.LENGTH_LONG).show()
                        Log.v("broadcasterror",e.message.toString())
                    }
                }
            }
            msgupdate(1,"OFF") ->
            {
                power = "OFF"
                mybluetooth.shareit.write(msgupdate(1,"OFF"))
                if( servicescheduled)
                {
                    Toast.makeText(context,"system stopped",Toast.LENGTH_SHORT).show()
                    context.stopService(Intent(context,myaudioservice::class.java))
                    mybluetooth._mymessage.value = msgupdate(-1,"")
                    servicescheduled = false
//                    if(context is ComponentActivity)
//                    {
//                        context.finish()
//                        var newintent = Intent(context,context.javaClass)
//                        newintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//
//                        context.startActivity(newintent)
//                    }
                }
            }
        }
    }



}


@RequiresApi(Build.VERSION_CODES.O)
fun scheduleservice(context: Context,action:String)
{
    val desiredtime = if(action == "ACTION_START_SERVICE") {
        LocalTime.of(9, 0,0)
    }
    else
    {
        LocalTime.of(17,0,0)
    }

    var schedulingtime  = Calendar.getInstance()
    val currenttime = LocalTime.now()
    var startdelaymillis = 0L
    if(action == "ACTION_START_SERVICE")
    {
        if(currenttime.isBefore(desiredtime))
        {
            schedulingtime.set(Calendar.HOUR_OF_DAY,desiredtime.hour)
            schedulingtime.set(Calendar.MINUTE,desiredtime.minute)
            schedulingtime.set(Calendar.SECOND,desiredtime.second)
        }
        else if ((currenttime.isAfter(desiredtime) or currenttime.equals(desiredtime)) and currenttime.isBefore(
                LocalTime.of(17,0,0)))
        {
            schedulingtime.set(Calendar.HOUR_OF_DAY,currenttime.hour)
            schedulingtime.set(Calendar.MINUTE,currenttime.minute)
            schedulingtime.set(Calendar.SECOND,currenttime.second)
        }
        else if(currenttime.isAfter(LocalTime.of(17,0,0)))
        {
            schedulingtime.add(Calendar.DAY_OF_YEAR,1)
            schedulingtime.set(Calendar.HOUR_OF_DAY,desiredtime.hour)
            schedulingtime.set(Calendar.MINUTE,desiredtime.minute)
            schedulingtime.set(Calendar.SECOND,desiredtime.second)
        }
    }
    else
    {
        if(currenttime.isBefore(desiredtime))
        {
            schedulingtime.set(Calendar.HOUR_OF_DAY,desiredtime.hour)
            schedulingtime.set(Calendar.MINUTE,desiredtime.minute)
            schedulingtime.set(Calendar.SECOND,desiredtime.second)
        }
        else
        {
            schedulingtime.set(Calendar.HOUR_OF_DAY,currenttime.hour)
            schedulingtime.set(Calendar.MINUTE,currenttime.minute+1)
            schedulingtime.set(Calendar.SECOND,currenttime.second)
        }
    }

    startdelaymillis = schedulingtime.timeInMillis - System.currentTimeMillis()
    schedulealarm(context,startdelaymillis,action)
}



fun schedulealarm(context: Context,delaymillis : Long , action : String)
{
    var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context,MyAlarmreceiver::class.java)
    intent.action = action
    var pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_IMMUTABLE)

    alarmManager.setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + delaymillis,pendingIntent)

}


class MyAlarmreceiver : BroadcastReceiver()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        if(context != null)
        {
            if (action == "ACTION_START_SERVICE") {
                Toast.makeText(context, "Broadcast: service start", Toast.LENGTH_SHORT).show()
                if(!isServiceRunning(context,myaudioservice::class.java)){
                    val startServiceIntent = Intent(context, myaudioservice::class.java)
                    context.startService(startServiceIntent)
                    scheduleservice(context, "ACTION_STOP_SERVICE")
                }
            } else {
                Toast.makeText(context, "Broadcast: service stop", Toast.LENGTH_SHORT).show()
                val stopServiceIntent = Intent(context, myaudioservice::class.java)
                context.stopService(stopServiceIntent)
                scheduleservice(context, "ACTION_START_SERVICE")
            }
        }

    }

}


fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services = activityManager.getRunningServices(Integer.MAX_VALUE)
    for (service in services) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
