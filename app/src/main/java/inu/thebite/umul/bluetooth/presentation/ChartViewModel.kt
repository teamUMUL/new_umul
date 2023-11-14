package inu.thebite.umul.bluetooth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import inu.thebite.umul.room.chart.ChartDatabase
import inu.thebite.umul.room.chart.ChartEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    val chartDatabase : ChartDatabase
): ViewModel() {
    private val chartDao = chartDatabase.chartDao

    private val _allCharts : MutableStateFlow<List<ChartEntity>> = MutableStateFlow(emptyList())
    val allCharts = _allCharts.asStateFlow()

    private val _selectedChart : MutableStateFlow<ChartEntity?> = MutableStateFlow(null)
    val selectedChart = _selectedChart.asStateFlow()

    init {
        getAllCharts()
    }
    fun getAllCharts(){
        viewModelScope.launch(Dispatchers.IO) {
            chartDao.getAllCharts().collect{data ->
                _allCharts.update { data }
            }
        }
    }

    fun getChartByDateAndGameMode(
        date : String,
        gameMode : String
    ){
        _selectedChart.update {
            allCharts.value.find {
                it.date == date &&
                it.gameMode == gameMode
            }
        }
    }

    fun addChart(
        chartEntity: ChartEntity
    ){
        viewModelScope.launch(Dispatchers.IO) {
            chartDao.addChart(chartEntity = chartEntity)
        }
    }

    fun updateChart(
        chartEntity: ChartEntity
    ){
        viewModelScope.launch(Dispatchers.IO) {
            chartDao.updateChart(chartEntity = chartEntity)
        }
    }

    fun deleteChart(
        chartEntity: ChartEntity
    ){
        viewModelScope.launch(Dispatchers.IO) {
            chartDao.deleteChart(chartEntity = chartEntity)
        }
    }
}