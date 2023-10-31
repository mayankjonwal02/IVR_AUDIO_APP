package com.example.ivr_audio_app_20.android

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ivr_audio_app.android.UI


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun bottomSheet() {

    val sheetstate = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetstate)
    BottomSheetScaffold(
        modifier = Modifier
        ,
        sheetContent = {


            Card(shape = RoundedCornerShape(30.dp), modifier = Modifier
                .padding(15.dp)
                .shadow(30.dp, shape = RoundedCornerShape(30.dp)), border = BorderStroke(3.dp,
                Color.Green)
            ) {
                connection()
            }


        },
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 0.dp,




        ) {
        UI()
//        Text(text = "hello")
    }
}