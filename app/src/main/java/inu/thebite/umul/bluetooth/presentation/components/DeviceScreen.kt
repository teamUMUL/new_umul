package inu.thebite.umul.bluetooth.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import inu.thebite.umul.R
import inu.thebite.umul.bluetooth.presentation.ChartViewModel
import inu.thebite.umul.bluetooth.presentation.components.chart.ChewBarChart
import inu.thebite.umul.bluetooth.presentation.components.chart.GameButtonsRow
import inu.thebite.umul.bluetooth.presentation.components.chart.GameScreen
import inu.thebite.umul.bluetooth.presentation.components.chart.NoChartData
import inu.thebite.umul.bluetooth.presentation.components.chart.SelectCalendarRow
import inu.thebite.umul.bluetooth.presentation.components.chart.TimeBarChart
import inu.thebite.umul.bluetooth.presentation.components.chart.getCurrentDate
import inu.thebite.umul.room.chart.ChartEntity
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    chartViewModel: ChartViewModel,
    onClick: () -> Unit,
    isConnected: Boolean
) {
    val selectedChart by chartViewModel.selectedChart.collectAsState()
    val allCharts by chartViewModel.allCharts.collectAsState()

    val (gameOn, setGameOn) = rememberSaveable {
        mutableStateOf(false)
    }
    val (gameStart, setGameStart) = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedDay = rememberSaveable {
        mutableStateOf(getCurrentDate())
    }
    val selectedGameMode = rememberSaveable {
        mutableStateOf("당근게임")
    }
    val selectedTime = rememberSaveable {
        mutableStateOf("아침")
    }

    val timerValue = remember { mutableStateOf(0) }
    val timerRunning = remember { mutableStateOf(false) }
    val realChewCount = rememberSaveable {
        mutableIntStateOf(0)
    }
    val chewCount = rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit){
        chartViewModel.getChartByDateAndGameMode(
            date = selectedDay.value,
            time = selectedTime.value,
            gameMode = selectedGameMode.value
        )
    }

    LaunchedEffect(selectedDay.value, selectedTime.value, selectedGameMode.value){
        chartViewModel.getChartByDateAndGameMode(
            date = selectedDay.value,
            time = selectedTime.value,
            gameMode = selectedGameMode.value
        )
    }


    LaunchedEffect(gameStart) {
        if (gameStart) {
            timerRunning.value = true
            timerValue.value = 0 // 타이머 초기화
            while (timerRunning.value) {
                delay(1000) // 1초마다
                timerValue.value++ // 타이머 증가
            }
        } else {
            timerRunning.value = false
            if(timerValue.value != 0 && realChewCount.intValue != 0){
                if (allCharts.any { it.time == selectedTime.value && it.gameMode == selectedGameMode.value && it.date == selectedDay.value }){
                    selectedChart?.let { selectedChart ->
                        chartViewModel.updateChart(
                            chartEntity = ChartEntity(
                                id = selectedChart.id,
                                chewCount = realChewCount.intValue.toFloat(),
                                date = selectedDay.value,
                                gameMode = selectedGameMode.value,
                                time = selectedTime.value,
                                timeFloat = timerValue.value.toFloat()
                            )
                        )
                    }
                } else {
                    chartViewModel.addChart(
                        ChartEntity(
                            chewCount = realChewCount.intValue.toFloat(),
                            date = selectedDay.value,
                            gameMode = selectedGameMode.value,
                            time = selectedTime.value,
                            timeFloat = timerValue.value.toFloat()
                        )
                    )
                }

            }
            Log.d("timer", timerValue.value.toString())
            realChewCount.intValue = 0
            chewCount.intValue = 0
            delay(1000)
            chartViewModel.getChartByDateAndGameMode(
                date = selectedDay.value,
                time = selectedTime.value,
                gameMode = selectedGameMode.value
            )
        }
    }

    if (gameOn) {
        GameScreen(
            selectedGameMode = selectedGameMode.value,
            gameStart = gameStart,
            setGameOn = { setGameOn(it) },
            setGameStart = { setGameStart(it) },
            chewCount = chewCount,
            addRealChewCount = { realChewCount.intValue += 1 },
        )
    }

    val calendarState = rememberSheetState()
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            style = CalendarStyle.WEEK
        ),
        selection = CalendarSelection.Date{ date ->
            Log.e("선택한 날짜", date.toString())
            selectedDay.value = date.toString()
        },
    )


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, color = Color.Black, RoundedCornerShape(8.dp)),

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SelectCalendarRow(
                    modifier = Modifier.weight(0.5f),
                    selectedDay = selectedDay.value,
                    selectedTime = selectedTime.value,
                    calendarShow = {calendarState.show()},
                    setSelectedTime = {selectedTime.value = it}
                )
                IconButton(
                    onClick = { onClick() },
                    modifier = Modifier
                        .background(color = if(isConnected) Color.Blue else Color.Gray, shape = CircleShape)
                        .weight(0.05f)
                ) {
                    Icon(painter = painterResource(id = R.drawable.icon_bluetooth), contentDescription = null)
                }
                GameButtonsRow(
                    modifier = Modifier.weight(0.5f),
                    selectedItem = selectedGameMode.value,
                    setSelectedItem = {selectedGameMode.value = it},
                    setGameOn = {setGameOn(it)}
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "씹은 횟수",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider(color = Color.Black, thickness = 2.dp)
                    selectedChart?.let {selectedChart ->
                        ChewBarChart(
                            dataMap = mapOf(
                                "사용자" to selectedChart.chewCount,
                                "비만군" to 600f
                            )
                        )
                    } ?: NoChartData()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "식사 시간",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider(color = Color.Black, thickness = 2.dp)
                    selectedChart?.let {selectedChart ->
                        TimeBarChart(
                            dataMap = mapOf(
                                "사용자" to selectedChart.timeFloat,
                                "비만군" to 600f
                            )
                        )
                    } ?: NoChartData()



                }
            }
        }
    }

}




