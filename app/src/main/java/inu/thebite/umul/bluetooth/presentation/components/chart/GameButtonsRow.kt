package inu.thebite.umul.bluetooth.presentation.components.chart

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import inu.thebite.umul.R


@Composable
fun GameButtonsRow(
    modifier : Modifier = Modifier,
    selectedItem : String,
    setSelectedItem: (String) -> Unit,
    setGameOn : (Boolean) -> Unit,
){
    Row(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Buttons(
            items = listOf("당근게임", "풍선게임"),
            selectedItem = selectedItem,
            setSelectedItem = { setSelectedItem(it) }
        )
        Spacer(modifier = Modifier.width(10.dp))
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
