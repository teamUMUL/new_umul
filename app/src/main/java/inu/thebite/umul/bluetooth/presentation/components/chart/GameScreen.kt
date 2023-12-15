package inu.thebite.umul.bluetooth.presentation.components.chart

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import inu.thebite.umul.R
import inu.thebite.umul.bluetooth.service.BluetoothService

@Composable
fun GameScreen(
    selectedGameMode : String,
    gameStart : Boolean,
    setGameOn : (Boolean) -> Unit,
    setGameStart : (Boolean) -> Unit,
    chewCount: MutableIntState,
    addRealChewCount : () -> Unit,
){
    Dialog(
        onDismissRequest = {
            setGameOn(false)
            setGameStart(false)
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when(selectedGameMode){
                "당근게임" -> {
                    Image(
                        painter = painterResource(id = R.drawable.carrot_game_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )
                }
                "풍선게임" -> {
                    Image(
                        painter = painterResource(id = R.drawable.balloon_game_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
        if(gameStart){
            GameStartScreen(
                selectedGameMode = selectedGameMode,
                chewCount = chewCount,
                addRealChewCount = { addRealChewCount() },
                setGameOn = {setGameOn(it)},
                setGameStart = {setGameStart(it)},
            )

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .clickable {
                            setGameStart(true)
                        }
                        .size(400.dp),
                    painter = painterResource(id = R.drawable.game_start), contentDescription = null
                )
            }
        }

    }
}

@SuppressLint("UnspecifiedRegisterReceiverFlag")
@Composable
fun GameStartScreen(
    selectedGameMode: String,
    chewCount: MutableIntState,
    addRealChewCount: () -> Unit,
    setGameOn: (Boolean) -> Unit,
    setGameStart: (Boolean) -> Unit,
){
    val context = LocalContext.current
    val bluetoothReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothService.ACTION_DATA_RECEIVED) {
                    // 데이터 수신 시 로직 처리
                    chewCount.intValue =
                        if (chewCount.intValue < 8) chewCount.intValue + 1 else 1
                    addRealChewCount()

                }
            }
        }
    }

    DisposableEffect(Unit){
        val filter = IntentFilter(BluetoothService.ACTION_DATA_RECEIVED)
        context.registerReceiver(bluetoothReceiver, filter)
        onDispose {
                context.unregisterReceiver(bluetoothReceiver)
        }
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
        ) {
            Image(
                modifier = Modifier
                    .clickable {
                        setGameOn(false)
                        setGameStart(false)
                    }
                    .fillMaxHeight(),
                painter = painterResource(id = R.drawable.game_end_btn), contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
            Image(
                modifier = Modifier
                    .clickable {
                        chewCount.intValue =
                            if (chewCount.intValue < 8) chewCount.intValue + 1 else 1
                        addRealChewCount()
                    }
                    .fillMaxHeight(),
                painter = painterResource(id = R.drawable.temp_balloon_inflate), contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.85f),
            horizontalArrangement =
            if(selectedGameMode == "풍선게임" && chewCount.intValue != 8){
                Arrangement.SpaceBetween
            } else {
                Arrangement.Center
            }
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(selectedGameMode == "풍선게임" && chewCount.intValue == 8){
                Image(
                    modifier = Modifier
                        .fillMaxHeight(),
                    painter = painterResource(id = R.drawable.balloon_game_success),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
            } else {
                CheckGameModeAndChewCount(selectedGameMode, chewCount.intValue)
            }
        }
    }
}