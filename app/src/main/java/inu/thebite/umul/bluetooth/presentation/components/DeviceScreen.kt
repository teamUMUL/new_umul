package inu.thebite.umul.bluetooth.presentation.components

import android.util.Log
import android.widget.ImageButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import inu.thebite.umul.R
import inu.thebite.umul.bluetooth.domain.BluetoothDevice
import inu.thebite.umul.bluetooth.presentation.BluetoothUiState
import inu.thebite.umul.bluetooth.presentation.ChartViewModel
import inu.thebite.umul.room.chart.ChartEntity
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
    selectedChart: ChartEntity?
) {
    val (gameOn, setGameOn) = rememberSaveable {
        mutableStateOf(false)
    }
    val (gameStart, setGameStart) = rememberSaveable {
        mutableStateOf(false)
    }
    val selectedDay = rememberSaveable {
        mutableStateOf(getCurrentDate())
    }
    val selectedItem = rememberSaveable {
        mutableStateOf("당근게임")
    }

    if(gameOn){

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
                when(selectedItem.value){
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
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.15f)
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
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.clickable {
                            setGameStart(true)
                        },
                        painter = painterResource(id = R.drawable.game_start), contentDescription = null
                    )
                }
            }

        }
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
                    selectedDay = selectedDay.value,
                    calendarShow = {calendarState.show()}
                )
                GameButtonsRow(
                    selectedItem = selectedItem.value,
                    setSelectedItem = {selectedItem.value = it},
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
                    ChewBarChart(
                        dataMap = mapOf(
                            "사용자" to 360f,
                            "비만군" to 600f
                    ))
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
                    selectedChart?.let {

                    }
                    TimeBarChart(
                        dataMap = mapOf(
                            "사용자" to 360f,
                            "비만군" to 600f
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }

        item {
            Text(
                text = "Scanned Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}
@Composable
fun ChewBarChart(
    dataMap: Map<String, Float>
){
    val dataList = dataMap.values.toList()
    val chewList = mutableListOf<String>()
    for (data in dataList){
        chewList.add(data.toString().substringBefore(".")+"회")
    }
    val xLabelList = dataMap.keys.toList()
    val chartEntryModelProducer = ChartEntryModelProducer(entriesOf(dataList[0], dataList[1]))
    val columnChart = columnChart(
        columns = listOf(LineComponent(
            color = R.color.teal_200,
            thicknessDp = 50f,
            shape = Shapes.roundedCornerShape(allPercent = 20)
        )

        )
    )
    Chart(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        chart = remember(columnChart) { columnChart },
        chartModelProducer = chartEntryModelProducer,
        topAxis = rememberTopAxis(
            label = axisLabelComponent(color = Color.Black, textSize = 22.sp),
            valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Top> { value, _ ->
                if (value == 0f){
                    chewList[0]
                } else {
                    chewList[1]
                }
            },
            sizeConstraint = Axis.SizeConstraint.Auto()
        ),
        bottomAxis = rememberBottomAxis(
            label = axisLabelComponent(color = Color.Black, textSize = 22.sp),
            valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                if (value == 0f){
                    xLabelList[0]
                } else {
                    xLabelList[1]
                }
            },
        ),
    )
}
@Composable
fun TimeBarChart(
    dataMap : Map<String, Float>
){
    val dataList = dataMap.values.toList()
    val timeList = mutableListOf<String>()
    for (data in dataList){
        val totalSeconds = data.toInt()
        val minutes = totalSeconds/60
        val seconds = totalSeconds%60
        timeList.add(minutes.toString()+"분 "+seconds.toString()+"초")
    }
    val xLabelList = dataMap.keys.toList()
    val chartEntryModelProducer = ChartEntryModelProducer(entriesOf(dataList[0], dataList[1]))
    val columnChart = columnChart(
        columns = listOf(LineComponent(
            color = R.color.teal_200,
            thicknessDp = 50f,
            shape = Shapes.roundedCornerShape(allPercent = 20)
            )

        )
    )
    Chart(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        chart = remember(columnChart) { columnChart },
        chartModelProducer = chartEntryModelProducer,
        topAxis = rememberTopAxis(
            label = axisLabelComponent(color = Color.Black, textSize = 22.sp),
            valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Top> { value, _ ->
                if (value == 0f){
                    timeList[0]
                } else {
                    timeList[1]
                }
            },
            sizeConstraint = Axis.SizeConstraint.Auto()
        ),
        bottomAxis = rememberBottomAxis(
            label = axisLabelComponent(color = Color.Black, textSize = 22.sp),
            valueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                if (value == 0f){
                    xLabelList[0]
                } else {
                    xLabelList[1]
                }
            },
        ),
    )
}
@Composable
fun SelectCalendarRow(
    selectedDay : String,
    calendarShow : () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(start = 10.dp),
            text = selectedDay,
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(
            onClick = {
                calendarShow()
            }
        ) {
            Icon(painter = painterResource(id = R.drawable.icon_calendar), contentDescription = null)
        }
    }
}

@Composable
fun GameButtonsRow(
    selectedItem : String,
    setSelectedItem: (String) -> Unit,
    setGameOn : (Boolean) -> Unit,
){
    Row(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Buttons(
            items = listOf("당근게임", "풍선게임"),
            selectedItem = selectedItem,
            setSelectedItem = { setSelectedItem(it) }
        )
        OutlinedButton(
            onClick = {
                setGameOn(true)
            },
            shape = RoundedCornerShape(10),
            border = BorderStroke(2.dp, Color.Gray),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Gray
            ),
        ) {
            Icon(painter = painterResource(id = R.drawable.icon_play), contentDescription = null)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "게임시작",
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
fun Buttons(
    items: List<String>,
    selectedItem: String,
    setSelectedItem: (String) -> Unit,
    useFixedWidth: Boolean = false,
    itemWidth: Dp = 120.dp,
    cornerRadius: Int = 10,
){
    Row(
        modifier = Modifier
    ) {
        items.forEachIndexed{ index, item ->
            OutlinedButton(
                modifier = when(index){
                    0 -> {
                        if (useFixedWidth) {
                            Modifier
                                .width(itemWidth)
                                .offset(0.dp, 0.dp)
                                .zIndex(if (selectedItem == item) 1f else 0f)
                        } else {
                            Modifier
                                .wrapContentSize()
                                .offset(0.dp, 0.dp)
                                .zIndex(if (selectedItem == item) 1f else 0f)
                        }
                    }
                    else -> {
                        if(useFixedWidth){
                            Modifier
                                .width(itemWidth)
                                .offset((-1 * index).dp, 0.dp)
                                .zIndex(if (selectedItem == item) 1f else 0f)
                        } else {
                            Modifier
                                .wrapContentSize()
                                .offset((-1 * index).dp, 0.dp)
                                .zIndex(if (selectedItem == item) 1f else 0f)
                        }
                    }
                },
                onClick = {
                    setSelectedItem(item)
                },
                shape = when(index) {
                    //왼쪽 바깥
                    0 -> RoundedCornerShape(
                        topStartPercent = cornerRadius,
                        topEndPercent = 0,
                        bottomStartPercent = cornerRadius,
                        bottomEndPercent = 0
                    )
                    //오른쪽 끝
                    items.size - 1 -> RoundedCornerShape(
                        topStartPercent = 0,
                        topEndPercent = cornerRadius,
                        bottomStartPercent = 0,
                        bottomEndPercent = cornerRadius
                    )
                    //가운데 버튼들
                    else -> RoundedCornerShape(
                        topStartPercent = 0,
                        topEndPercent = 0,
                        bottomStartPercent = 0,
                        bottomEndPercent = 0
                    )
                },
                border = BorderStroke(
                    1.dp, if (selectedItem == item){
                        Color.Gray
                    } else {
                        Color.Gray.copy(alpha = 0.75f)
                    }
                ),
                colors = if (selectedItem == item) {
                    ButtonDefaults.outlinedButtonColors(
                        //선택된 경우 색
                        containerColor = Color.Gray
                    )
                } else {
                    ButtonDefaults.outlinedButtonColors(
                        //선택 안된 경우 색
                        containerColor = Color.Transparent
                    )
                },
            ) {
                Text(
                    text = item,
                    fontWeight = FontWeight.Normal,
                    color = if (selectedItem == item) {
                        Color.White
                    } else {
                        Color.Gray.copy(alpha = 0.9f)
                    }
                )
            }

        }
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Date()
    return dateFormat.format(currentDate)
}