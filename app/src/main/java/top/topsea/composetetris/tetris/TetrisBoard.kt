package top.topsea.composetetris.tetris

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.topsea.composetetris.tetris.Model.*
import kotlin.math.abs
import kotlin.math.min

@Composable
fun Tetris() {
    var curModel = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }

    val tetris = Array(Tetris.height + 1){ row ->
        if (row == Tetris.height) {
            Array(Tetris.width){ 1 }
        } else {
            Array(Tetris.width){ 0 }
        }
    }


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
//                moveLeft(tetris, curModel)
            } else {
                moveDown(tetris, curModel)
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
            val rectWidth = size.width / Tetris.width
            val rectHeight = size.height / Tetris.height

            val size = Size(rectWidth - 5, rectHeight - 5)

            for (i in curModel.indices) {
                if (curModel[i] == Int.MAX_VALUE) {
                    continue
                }
                val x = curModel[i] / Tetris.width
                val y = curModel[i] % Tetris.width

                if (y < 0 || y > Tetris.width || x < 0 || x > Tetris.height) {
                    continue
                }
                tetris[x][y] = 1
            }

            for (x in 0 until Tetris.height) {
                for (y in 0 until Tetris.width) {
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
        delay(2000)
        val tempModel = listModel.random().values
        tempModel.forEachIndexed { index, curr ->
            curModel[index] = curr
        }
    }
}


private fun moveLeft(tetris: Array<Array<Int>>, curModel: SnapshotStateList<Int>) {
    val tempModel = Array(curModel.size) { i ->
        if (curModel[i] < 0) {
            curModel[i] + 1
        } else {
            curModel[i] - 1
        }
    }
    for (i in tempModel.size - 1 downTo 0) {
        val x = tempModel[i] / Tetris.width
        val y = tempModel[i] % Tetris.width
        //已经靠边
        if (abs(y) == 9) {
            return
        }
        if (x >= 0) {
            tetris[x][y + 1] = 0
            tetris[x][y] = 1
        }
    }
    for (i in curModel.size - 1 downTo 0) {
        if (curModel[i] < 0) {
            curModel[i] += 1
        } else {
            curModel[i] -= 1
        }
    }
}

private fun moveRight(tetris: Array<Array<Int>>, curModel: SnapshotStateList<Int>) {
    val tempModel = Array(curModel.size) { i ->
        if (curModel[i] < 0) {
            curModel[i] - 1
        } else {
            curModel[i] + 1
        }
    }
    for (i in tempModel.size - 1 downTo 0) {
        val x = tempModel[i] / Tetris.width
        val y = tempModel[i] % Tetris.width
        //已经靠边
        if (abs(y) == 0) {
            return
        }
        if (x >= 0) {
            tetris[x][y - 1] = 0
            tetris[x][y] = 1
        }
    }
    for (i in curModel.size - 1 downTo 0) {
        if (curModel[i] < 0) {
            curModel[i] -= 1
        } else {
            curModel[i] += 1
        }
    }
}

private fun moveDown(
    tetris: Array<Array<Int>>,
    curModel: SnapshotStateList<Int>,
) {
    var columnStart = 0
    var columnEnd = 0

    val map = HashMap<Int, Int>()

    //从最大的开始，也就是最下面的一行
    for (i in curModel.indices.reversed()) {
        val x = curModel[i] / Tetris.width
        val y = curModel[i] % Tetris.width
        tetris[x][y] = 0
        if (map.contains(y)) {
            continue
        } else {
            map[y] = x
        }
    }
    val tree = map.toSortedMap()

    columnStart = tree.firstKey()
    columnEnd = tree.lastKey()
    Log.d("", "GaoHai:::moveDown columnStart ${columnStart} columnEnd ${columnEnd}")

    var closest = Tetris.height - 1
    for (y in columnStart .. columnEnd) {
        for (x in Tetris.height - 1 downTo 0) {
            if (tetris[x][y] == 0) {
                closest = min(closest, x - map[y]!!)
                Log.d("", "GaoHai:::moveDown x - map[y]!! ${x - map[y]!!}  y ${y}")
                break
            }
        }
    }
    Log.d("", "GaoHai:::moveDown closest ${closest}")


    for (i in curModel.indices.reversed()) {
        curModel[i] += closest * Tetris.width
    }
}