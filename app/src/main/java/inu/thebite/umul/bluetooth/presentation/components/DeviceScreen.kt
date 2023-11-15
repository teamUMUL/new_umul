package inu.thebite.umul.bluetooth.presentation.components

import android.content.Context
import android.util.Log
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import inu.thebite.umul.R
import inu.thebite.umul.bluetooth.domain.BluetoothDevice
import inu.thebite.umul.bluetooth.presentation.BluetoothUiState
import inu.thebite.umul.bluetooth.presentation.BluetoothViewModel
import inu.thebite.umul.bluetooth.presentation.ChartViewModel
import inu.thebite.umul.bluetooth.presentation.components.bluetooth.BluetoothDeviceList
import inu.thebite.umul.bluetooth.presentation.components.chart.Buttons
import inu.thebite.umul.bluetooth.presentation.components.chart.CheckGameModeAndChewCount
import inu.thebite.umul.bluetooth.presentation.components.chart.ChewBarChart
import inu.thebite.umul.bluetooth.presentation.components.chart.GameButtonsRow
import inu.thebite.umul.bluetooth.presentation.components.chart.GameScreen
import inu.thebite.umul.bluetooth.presentation.components.chart.NoChartData
import inu.thebite.umul.bluetooth.presentation.components.chart.SelectCalendarRow
import inu.thebite.umul.bluetooth.presentation.components.chart.TimeBarChart
import inu.thebite.umul.bluetooth.presentation.components.chart.getCurrentDate
import inu.thebite.umul.room.chart.ChartEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit,
    selectedChart: ChartEntity?,
    chartViewModel: ChartViewModel,
    bluetoothViewModel : BluetoothViewModel
) {
    val selectedChart by chartViewModel.selectedChart.collectAsState()

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
            Log.d("timer", timerValue.value.toString())
            realChewCount.intValue = 0
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
            addRealChewCount = { realChewCount.intValue += 1 }
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
                .fillMaxWidth(0.2f)
                .fillMaxHeight()
                .border(2.dp, color = Color.Black, RoundedCornerShape(8.dp)),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when {
                state.isConnecting -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(text = "Connecting...")
                    }
                }

                state.isConnected -> {
                    ChatScreen(
                        state = state,
                        onDisconnect = bluetoothViewModel::disconnectFromDevice,
                        onSendMessage = bluetoothViewModel::sendMessage
                    )
                }

                else -> {
                    BluetoothDeviceList(
                        pairedDevices = state.pairedDevices,
                        scannedDevices = state.scannedDevices,
                        onClick = onDeviceClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onStartScan
                        ) {
                            Text(text = "Start scan")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onStopScan
                        ) {
                            Text(text = "Stop scan")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onStartServer
                        ) {
                            Text(text = "Start server")
                        }
                    }
                }
            }

        }
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



