package top.topsea.composetetris.tetris

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.topsea.composetetris.tetris.Model.*
import kotlin.math.abs
import kotlin.math.max
import androidx.compose.ui.platform.LocalDensity

private val tetris = Array(HEIGHT + 1){ row ->
    if (row == HEIGHT) {
        IntArray(WIDTH){ 1 }
    } else {
        IntArray(WIDTH){ 0 }
    }
}

private const val SIZE = 80f

@Composable
fun Tetris() {
    val listModel = listOf(MODEL_O, MODEL_T, MODEL_S, MODEL_Z, MODEL_I, MODEL_L, MODEL_J)

    val curModel = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
    var curModelType by remember {
        mutableStateOf(Int.MAX_VALUE)
    }
    val modelPlaced = remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0F) }
    var offsetY by remember { mutableStateOf(0F) }


    val requireSize = LocalDensity.current.run { SIZE.toDp() }


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
    ) {
        Canvas(
            modifier = Modifier
                .requiredHeight(requireSize * HEIGHT)
                .requiredWidth(requireSize * WIDTH)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            val x = abs(offsetX)
                            val y = abs(offsetY)
                            if (x > y) {
                                if (offsetX < 0) {
                                    moveLeft(tetris, curModel)
                                } else {
                                    moveRight(tetris, curModel)
                                }
                            } else {
                                if (offsetY < 0) {
                                    rotateModel(tetris, curModel, curModelType)
                                } else {
                                    moveDown(tetris, curModel, modelPlaced)
                                }
                            }
                            offsetX = 0f
                            offsetY = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .background(Color.Yellow)
        ) {
            val curSize = max(size.width / WIDTH, SIZE)

            for (i in curModel.indices) {
                if (curModel[i] == Int.MAX_VALUE) {
                    continue
                }
                val x = curModel[i] / WIDTH
                val y = curModel[i] % WIDTH

                if (y < 0 || y > WIDTH || x < 0 || x > HEIGHT) {
                    continue
                }
                tetris[x][y] = 1
            }

            for (x in 0 until HEIGHT) {
                for (y in 0 until WIDTH) {
                    val color = if (tetris[x][y] != 0) {
                        Color.Gray
                    } else {
                        Color.LightGray
                    }
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = y * curSize,
                            y = x * curSize
                        ),
                        size = Size(curSize - 5, curSize - 5)
                    )
                }
            }
        }
    }


    LaunchedEffect(key1 = Unit) {
        val firstModel = listModel[4]
        curModelType = firstModel.type
        firstModel.values.forEachIndexed { index, curr ->
            curModel[index] = curr
        }
        while (true) {
            delay(800)
            if (!modelPlaced.value) {
                normalDown(tetris, curModel, modelPlaced)
            }
        }
    }


    LaunchedEffect(key1 = modelPlaced.value) {
        if (modelPlaced.value) {
            eraseLines(tetris = tetris)
            val tempModel = listModel[4]
            curModelType = tempModel.type
            tempModel.values.forEachIndexed { index, curr ->
                curModel[index] = curr
            }
            modelPlaced.value = false
        }
    }
}
