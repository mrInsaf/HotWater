package com.example.mynfc.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.mynfc.R
import com.example.mynfc.misc.createPointsList
import com.example.mynfc.misc.pointsListToData
import com.example.mynfc.misc.retrieveTransactionData
import com.example.mynfc.ui.voda.listOfTransactions
import kotlin.math.roundToInt


const val steps = 4

@Composable
fun TransactionsGraphic(transactionsList: List<Map<String, Any>>) {
    val transactionsData = retrieveTransactionData(transactionsList)
    println("transactionsData: $transactionsData")

    val pointsList = createPointsList(transactionsData)
    println("pointsList: $pointsList")

    val pointsData = pointsListToData(pointsList)
    println("pointsData: $pointsData")

    val minY = pointsData.minByOrNull { it.y }?.y
    val maxY = pointsData.maxByOrNull { it.y }?.y

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.White)
        .steps(pointsData.size - 1)
//        .labelData { i -> (pointsList[i]["date"]).toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(if (pointsData.size < steps) pointsData.size else steps)
        .backgroundColor(Color.White)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val nSteps = if (pointsData.size < steps) pointsData.size else steps
            val stepY = if (minY != null && maxY != null) {
                (maxY - minY) / nSteps
            }
            else {
                0f
            }
            ((i.toFloat() * stepY) + (minY ?: 0f)).roundToInt().toString()
            }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        width = 3f,
                        alpha = 0.5f
                    ),
                    IntersectionPoint(
                        radius = 3.dp,
                        alpha = 0.25f
                    ),
                    SelectionHighlightPoint(
                        color = Color.Black,
                        radius = 1.dp
                    ),
                    ShadowUnderLine(),
                    SelectionHighlightPopUp(
                        popUpLabel = { x, y ->
                            val xLabel = "${pointsList[x.toInt()]["date"]}"
                            val yLabel = "¥$y"
                            "$yLabel | $xLabel"
                        }
                    )
                )
            ),
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.White,
        paddingRight = 0.dp,
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}


@Composable
fun TransactionsHistoryGraphicBlock(
    transactionsList: List<Map<String, Any>>,
    modifier: Modifier = Modifier
) {
    CustomBlock(title = stringResource(R.string.statistics)) {
        if (transactionsList.isNotEmpty()) {
            TransactionsGraphic(transactionsList)
        }
    }
}

@Preview
@Composable
fun TransactionsHistoryGraphicBlockPreview(modifier: Modifier = Modifier) {
    TransactionsHistoryGraphicBlock(listOfTransactions)
}