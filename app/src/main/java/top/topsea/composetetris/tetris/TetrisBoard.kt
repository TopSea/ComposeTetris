package top.topsea.composetetris.tetris

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import top.topsea.composetetris.tetris.Model.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private val tetris = Array(HEIGHT + 1){ row ->
    if (row == HEIGHT) {
        Array(WIDTH){ 1 }
    } else {
        Array(WIDTH){ 0 }
    }
}

@Composable
fun Tetris() {
    val curModel = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
    var curModelType by remember {
        mutableStateOf(Int.MAX_VALUE)
    }

    val modelPlaced = remember { mutableStateOf(false) }

    val listModel = listOf(MODEL_O, MODEL_T, MODEL_S, MODEL_Z, MODEL_I, MODEL_L, MODEL_J)

    val horState = rememberScrollState()
    val verState = rememberScrollState()

    val horFling = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            if (abs(initialVelocity) == 0f) {
                return initialVelocity
            }
            //左正右负
            if (initialVelocity > 0) {
                moveLeft(tetris, curModel)
            } else {
                moveRight(tetris, curModel)
            }
            Log.d("", "GaoHai:::horFling ${initialVelocity}")
            return initialVelocity
        }
    }
    val verFling = object : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            if (abs(initialVelocity) == 0f) {
                return initialVelocity
            }
            //上正下负
            if (initialVelocity > 0) {
                rotateModel(tetris, curModel, curModelType)
            } else {
                moveDown(tetris, curModel, modelPlaced)
            }
            Log.d("", "GaoHai:::verFling ${initialVelocity}")
            return initialVelocity
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(
                state = horState,
                flingBehavior = horFling
            )
            .verticalScroll(
                state = verState,
                flingBehavior = verFling
            )
    ) {
        Canvas(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
        ) {
            val rectWidth = size.width / WIDTH
            val rectHeight = size.height / HEIGHT

            val size = Size(rectWidth - 5, rectHeight - 5)

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

            for (x in 0 .. HEIGHT) {
                for (y in 0 until WIDTH) {
                    val color = if (tetris[x][y] != 0) {
                        Color.Gray
                    } else {
                        Color.LightGray
                    }
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = y * rectWidth,
                            y = x * rectHeight
                        ),
                        size = size
                    )
                }
            }
        }
    }


    LaunchedEffect(key1 = Unit) {
        val tempModel = listModel[6]
        curModelType = tempModel.type
        tempModel.values.forEachIndexed { index, curr ->
            curModel[index] = curr
        }

        while (true) {
            delay(500)
            if (!modelPlaced.value) {
                normalDown(tetris, curModel, modelPlaced)
            }
        }
    }


    LaunchedEffect(key1 = modelPlaced.value) {
        if (modelPlaced.value) {
            val tempModel = listModel.random()
            curModelType = tempModel.type
            tempModel.values.forEachIndexed { index, curr ->
                curModel[index] = curr
            }
            modelPlaced.value = false
        }
    }
}

