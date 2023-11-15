package inu.thebite.umul.bluetooth.presentation.components.chart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        columns = listOf(
            LineComponent(
            color = R.color.black,
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

