import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.LocalAbsoluteElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun filepathui() {
    val context = LocalContext.current

    val resultPermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = activityResult.data?.data
            uri?.let {
                // Handle the URI here, for example, display its path using Toast
                val filePath = uri.toString()
                Toast.makeText(context, "Selected file URI: $filePath", Toast.LENGTH_LONG).show()
            } ?: Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun getfile()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // You can specify the type of files to be picked here (e.g., image/*, audio/*, etc.)
        intent.type = "*/*"

        launcher.launch(intent)
    }




    LaunchedEffect(Unit )
    {
        resultPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    var key by remember {
        mutableStateOf(0)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(){
            Button(onClick = {
                getfile()

            }) {
                // Button content here
            }



        }
    }


}
@Composable
fun openFilePicker(context: Context) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    // You can specify the type of files to be picked here (e.g., image/*, audio/*, etc.)
    intent.type = "*/*"
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = activityResult.data?.data
            uri?.let {
                // Handle the URI here, for example, display its path using Toast
                val filePath = uri.toString()
                Toast.makeText(context, "Selected file URI: $filePath", Toast.LENGTH_LONG).show()
            } ?: Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
    launcher.launch(intent)
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun createdirectory() {

    var context = LocalContext.current
    var requestpermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission())
    {
        isgranted ->
        if(isgranted)
        {
            Toast.makeText(context,"creating directory",Toast.LENGTH_SHORT).show()
            setdirectory(context)
        }
        else
        {
            Toast.makeText(context,"permission denied",Toast.LENGTH_SHORT).show()
        }
    }

    FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.child("audio").child("key").value == 0)
            {
                Toast.makeText(context,"responce",Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })


    var fileopenlauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(), onResult = {})

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
    {
        Column()
        {
            Button(onClick = { requestpermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) }) {

            }

            Button(onClick = {
                var directoryname = "IVR_AUDIO_LOCATION"

                var externaldir = File(context.getExternalFilesDir(null),directoryname)
                var diruri = Uri.parse(externaldir.absolutePath.toUri().toString())

                var intent = Intent(Intent.ACTION_PICK)
//                intent.data = diruri
                intent.setDataAndType(diruri,"*/*")
                intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK


                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }

            }) {

            }

            Button(onClick = {
                var directoryname = "IVR_AUDIO_LOCATION"

                var externaldir = File(context.getExternalFilesDir(null),directoryname)
                var diruri = Uri.parse(externaldir.absolutePath.toUri().toString())

                var subdirname = "hello_world"
                var subdir = File(externaldir,subdirname)

                if(subdir.mkdir())
                {
                    Toast.makeText(context,"file created",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(context,"file already exist",Toast.LENGTH_SHORT).show()
                }
            }) {

            }
            var mediaPlayer : MediaPlayer?  = null
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release()
                FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(2)
                mediaPlayer = null
            }

            Button(onClick = {
                var maindirname = "IVR_AUDIO_LOCATION"
                var subdirname = "hello_world"

                var externaldir = File(context.getExternalFilesDir(null),maindirname)
                var subdir = File(externaldir,subdirname)
                if (subdir.exists() && subdir.isDirectory) {
                    val audiofile = File(subdir, "HINDI.mp3") // Replace "HINDI.mp3" with your audio file name
                    if (audiofile.exists()) {
                        try {
                            mediaPlayer?.release() // Release any previously used MediaPlayer
                            mediaPlayer = MediaPlayer.create(context, Uri.fromFile(audiofile))
                            mediaPlayer?.setOnCompletionListener {
                                mediaPlayer?.release()
                                FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(0)
                                mediaPlayer = null
                            }
                            Toast.makeText(context, "Playing audio", Toast.LENGTH_LONG).show()
                            mediaPlayer?.start()
                            FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(1)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error playing audio", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Audio file does not exist", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Directory does not exist", Toast.LENGTH_LONG).show()
                }
            }) {

            }

            FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("call").child("key").value == 1 && snapshot.child("audio").child("key").value == 0)
                    {
                        var maindirname = "IVR_AUDIO_LOCATION"
                        var subdirname = "hello_world"

                        var externaldir = File(context.getExternalFilesDir(null),maindirname)
                        var subdir = File(externaldir,subdirname)
                        if (subdir.exists() && subdir.isDirectory) {
                            val audiofile = File(subdir, "HINDI.mp3") // Replace "HINDI.mp3" with your audio file name
                            if (audiofile.exists()) {
                                try {
                                    mediaPlayer?.release() // Release any previously used MediaPlayer
                                    mediaPlayer = MediaPlayer.create(context, Uri.fromFile(audiofile))
//                                    mediaPlayer?.setOnCompletionListener {
//                                        mediaPlayer?.release()
//                                        FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(0)
//                                        mediaPlayer = null
//                                    }
                                    Toast.makeText(context, "Playing audio", Toast.LENGTH_LONG).show()
                                    mediaPlayer?.start()
                                    FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(1)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error playing audio", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Audio file does not exist", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Directory does not exist", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

}



fun setdirectory(context: Context)
{
    var directoryname = "IVR_AUDIO_LOCATION"

    var internaldir = File(context.getExternalFilesDir(null),directoryname)
    var subdirectory = File(internaldir,"hello_world")

    try {
        internaldir.mkdir()
        subdirectory.mkdir()
        Toast.makeText(context,"directory created",Toast.LENGTH_SHORT).show()
        Toast.makeText(context,internaldir.absolutePath,Toast.LENGTH_LONG).show()
    }
    catch (e:Exception)
    {
        Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
    }


}

@Composable
fun responceui() {
    var key by remember {
        mutableStateOf(false)
    }
    var mediaPlayer: MediaPlayer? = null
    var context = LocalContext.current
    LaunchedEffect(Unit){
        FirebaseDatabase.getInstance().reference.child("call").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child("key")
                        .getValue(Long::class.java) == 1L
                ) {
                    key = true
                    Toast.makeText(context, snapshot.toString(), Toast.LENGTH_SHORT).show()
//                    var mediaPlayer: MediaPlayer? = null
//               mediaPlayer?.setOnCompletionListener {
//                   mediaPlayer?.release()
//                   FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(2)
//                   mediaPlayer = null
//               }

                    var maindirname = "IVR_AUDIO_LOCATION"
                    var subdirname = "hello_world"

                    var externaldir = File(context.getExternalFilesDir(null), maindirname)
                    var subdir = File(externaldir, subdirname)
                    if (subdir.exists() && subdir.isDirectory) {
                        val audiofile = File(
                            subdir,
                            "HINDI.mp3"
                        )
                        if (audiofile.exists()) {
                            try {
                                mediaPlayer?.release() // Release any previously used MediaPlayer
                                mediaPlayer = MediaPlayer.create(context, Uri.fromFile(audiofile))
                                mediaPlayer?.setOnCompletionListener {
                                    mediaPlayer?.release()
                                    FirebaseDatabase.getInstance().reference.child("audio")
                                        .child("key").setValue(2)

                                    mediaPlayer = null
                                    key = false

                                }
                                Toast.makeText(context, "Playing audio", Toast.LENGTH_LONG).show()
                                mediaPlayer?.start()
                                FirebaseDatabase.getInstance().reference.child("audio").child("key")
                                    .setValue(1)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error playing audio", Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, "Audio file does not exist", Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Directory does not exist", Toast.LENGTH_LONG)
                            .show()
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })


        FirebaseDatabase.getInstance().reference.child("audio").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("key").getValue(Long::class.java) == 3L)
                {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    FirebaseDatabase.getInstance().reference.child("audio").child("key").setValue(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




    }
}
