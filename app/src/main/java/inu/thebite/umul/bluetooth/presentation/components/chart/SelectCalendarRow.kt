package inu.thebite.umul.bluetooth.presentation.components.chart

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import inu.thebite.umul.R
import java.text.SimpleDateFormat
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCalendarRow(
    modifier : Modifier = Modifier,
    selectedTime : String,
    selectedDay : String,
    calendarShow : () -> Unit,
    setSelectedTime : (String) -> Unit
){
    var isExpanded by remember {
        mutableStateOf(false)
    }


    val timeList = listOf(
        "아침","점심","저녁"
    )
    Row(
        modifier = modifier,
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
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = {isExpanded = !isExpanded}
        ){
            TextField(
                value = selectedTime,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor= Color.Transparent,
                    unfocusedContainerColor= Color.Transparent,
                    disabledContainerColor= Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .menuAnchor()
                    .padding(0.dp)
                    .width(120.dp)
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {isExpanded = false},
            ){
                timeList.forEach { time ->
                    DropdownMenuItem(
                        text = {
                            Text(text = time)
                        },
                        onClick = {
                            isExpanded = false
                            setSelectedTime(time)
                        }
                    )
                }
            }

        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = Date()
    return dateFormat.format(currentDate)
}
