package inu.thebite.umul.bluetooth.presentation.components.chart

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import inu.thebite.umul.R
import kotlinx.coroutines.delay

@Composable
fun CheckGameModeAndChewCount(
    gameMode : String,
    chewCount : Int
){
    var pullCarrotFrame by remember { mutableStateOf(0) }
    var successCarrotFrame by remember { mutableStateOf(0) }
    val image1 = remember { mutableIntStateOf(R.drawable.carrot_game_carrot_1_1) }
    val image2 = remember { mutableIntStateOf(R.drawable.carrot_game_carrot_1_2) }
    val context = LocalContext.current
    LaunchedEffect(key1 = gameMode, key2 = chewCount) {
        if (gameMode == "당근게임") {
            image1.intValue = CheckChewCount(gameMode,chewCount, context).first
            image2.intValue = CheckChewCount(gameMode,chewCount, context).second
            if(chewCount == 8){
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
            } else {
                pullCarrotFrame = 0
                delay(100)
                pullCarrotFrame = 1
                delay(400)
                pullCarrotFrame = 2
                delay(100)
                pullCarrotFrame = 0
            }
        } else {
            image1.intValue = CheckChewCount(gameMode,chewCount, context).first
            image2.intValue = CheckChewCount(gameMode,chewCount, context).second
            if(chewCount == 8){
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
                delay(200)
                successCarrotFrame = 1
                delay(200)
                successCarrotFrame = 2
                delay(100)
                successCarrotFrame = 0
            } else {
                pullCarrotFrame = 0
                delay(100)
                pullCarrotFrame = 1
                delay(400)
                pullCarrotFrame = 2
                delay(100)
                pullCarrotFrame = 0
            }
        }

    }

    val pullCarrotAnimation = when (pullCarrotFrame) {
        0 -> image1.intValue
        1 -> image2.intValue
        else -> image1.intValue
    }
    val successAnimation = when (successCarrotFrame) {
        0 -> image1.intValue
        1 -> image2.intValue
        else -> image1.intValue
    }
    if(gameMode == "당근게임"){
        Image(
            modifier = Modifier
                .fillMaxHeight(0.75f),
            painter = painterResource(id =
            if(chewCount == 8){
                successAnimation
            } else {
                pullCarrotAnimation
            }
            ),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(0.5f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                modifier = Modifier
                    .fillMaxHeight(0.8f),
                painter = painterResource(id =
                if(chewCount == 8){
                    successAnimation
                } else {
                    pullCarrotAnimation
                }
                ),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                modifier = Modifier
                    .fillMaxHeight(0.05f * chewCount + 0.2f)
                    .padding(bottom = 10.dp),
                painter = painterResource(id = R.drawable.balloon_game_balloon),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
    }

}

fun CheckChewCount(gameMode: String,chewCount: Int, context: Context): Pair<Int, Int> {
    val result = when (chewCount) {
        8 -> {
            if (gameMode == "당근게임"){
                Pair(getResourceIdByName(context = context, imageName = "carrot_game_success_1"), getResourceIdByName(context = context, imageName = "carrot_game_success_2"))
            } else {
                Pair(getResourceIdByName(context = context, imageName = "balloon_game_success_1"), getResourceIdByName(context = context, imageName = "balloon_game_success_1"))
            }
        }
        0 -> {
            if (gameMode == "당근게임"){
                Pair(getResourceIdByName(context = context, imageName = "carrot_game_carrot_1_1"), getResourceIdByName(context = context, imageName = "carrot_game_carrot_1_1"))
            } else {
                Pair(getResourceIdByName(context = context, imageName = "balloon_game_1"), getResourceIdByName(context = context, imageName = "balloon_game_1"))
            }
        }
        else -> {
            if (gameMode == "당근게임"){
                Pair(getResourceIdByName(context = context, imageName = "carrot_game_carrot_${chewCount}_1"), getResourceIdByName(context = context, imageName = "carrot_game_carrot_${chewCount}_2"))
            } else {
                Pair(getResourceIdByName(context = context, imageName = "balloon_game_1"), getResourceIdByName(context = context, imageName = "balloon_game_2"))
            }
        }
    }

    return result
}

fun getResourceIdByName(imageName: String, context: Context): Int {
    // 이 함수는 이미지 리소스 이름을 리소스 ID로 변환합니다.
    Log.d("images", imageName)
    val packageName = context.packageName
    return context.resources.getIdentifier(imageName, "drawable", packageName)
}