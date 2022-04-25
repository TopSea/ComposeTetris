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
import top.topsea.composetetris.tetris.Model.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private val tetris = Array(Tetris.height + 1){ row ->
    if (row == Tetris.height) {
        Array(Tetris.width){ 1 }
    } else {
        Array(Tetris.width){ 0 }
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

            for (x in 0 .. Tetris.height) {
                Log.d("", "GaoHai:::${tetris[x].contentToString()}")
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
        val tempModel = listModel[6]
        curModelType = tempModel.type
        tempModel.values.forEachIndexed { index, curr ->
            curModel[index] = curr
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
    modelPlaced: MutableState<Boolean>
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
            map[y] = max(map[y]!!, x)
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
        for (x in map[y]!! .. Tetris.height) {
            if (tetris[x][y] != 0) {
                closest = min(closest, x - (map[y]!! + 1))
                Log.d("", "GaoHai:::moveDown x - map[y]!! ${x - map[y]!!}  x ${x}  y ${y}")
                break
            }
        }
    }
    Log.d("", "GaoHai:::moveDown closest ${closest}")

    //放置模块
    for (i in curModel.indices) {
        curModel[i] += closest * Tetris.width
        val x = curModel[i] / Tetris.width
        val y = curModel[i] % Tetris.width

        if (y < 0 || y > Tetris.width || x < 0 || x > Tetris.height) {
            continue
        }
        tetris[x][y] = 1
    }
    modelPlaced.value = true
}

private fun rotateModel(
    tetris: Array<Array<Int>>,
    curModel: SnapshotStateList<Int>,
    modelType: Int
) {
    val pose = modelPose(curModel)
    if (pose.isEmpty() || !canRotate(tetris, modelType, pose)) {
        return
    }
    Log.d("", "GaoHai:::${curModel.toIntArray().contentToString()}")

    //原先的位置置0
    for (i in curModel.indices) {
        if (curModel[i] == Int.MAX_VALUE) {
            continue
        }
        val x = curModel[i] / Tetris.width
        val y = curModel[i] % Tetris.width

        if (y < 0 || y > Tetris.width || x < 0 || x > Tetris.height) {
            continue
        }
        tetris[x][y] = 0
    }

    when (modelType) {
        Model.I -> {

        }
        else -> {
            for (i in curModel.indices) {
                curModel[i] = rotateOnePiece(curModel[1], curModel[i], Tetris.width)
            }
        }
    }

}

private fun modelPose(
    curModel: SnapshotStateList<Int>,
): List<Int> {
    if (curModel.first() == Int.MAX_VALUE) {
        return listOf()
    }
    val firstX: Double = curModel.first().toDouble() / Tetris.width
    Log.d("", "GaoHai:::firstX${firstX}")
    val firstY = curModel.first() % Tetris.width
    Log.d("", "GaoHai:::firstY${firstY}")
    val secondX = curModel[1] / Tetris.width
    val secondY = curModel[1] % Tetris.width

    return listOf(floor(firstX).toInt(), firstY, secondX, secondY)
}

private fun canRotate(
    tetris: Array<Array<Int>>,
    modelType: Int,
    pose: List<Int>
): Boolean {
    var ones = 0
    when(modelType) {
        Model.J, Model.L, Model.T, Model.Z, Model.S -> {
            for (x in pose[2] - 1 .. pose[2] + 1) {
                if (x < 0) {
                    continue
                }
                for (y in pose[3] - 1 .. pose[3] + 1) {
                    if (y > Tetris.width - 1 || y < 0) {
                        return false
                    }
                    if (tetris[x][y] != 0) {
                        ones++
                    }
                    //是否有位置旋转
                    if (x > Tetris.height || ones > 4) {
                        return false
                    }
                }
            }
        }
        Model.I -> {
            for (x in pose[2] - 2 .. pose[2] + 1) {
                if (x < 0) {
                    continue
                }
                for (y in pose[3] - 2 .. pose[3] + 1) {
                    if (y > Tetris.width - 1 || y < 0) {
                        return false
                    }
                    if (tetris[x][y] != 0) {
                        ones++
                    }
                    //是否有位置旋转
                    if (x > Tetris.height || ones > 4) {
                        return false
                    }
                }
            }
        }
        else -> {
            return false
        }
    }
    return true
}

private fun rotateOnePiece(
    center: Int,
    edge: Int,
    width: Int
): Int {
    when (center - edge) {
        width + 1 -> {
            return edge + 2
        }
        width -> {
            return edge + width + 1
        }
        width - 1 -> {
            return edge + width * 2
        }
        1 -> {
            return edge - width + 1
        }
        0 -> {
            return edge
        }
        -1 -> {
            return edge + width - 1
        }
        -width + 1 -> {
            return edge - width * 2
        }
        -width -> {
            return edge - width - 1
        }
        -width - 1 -> {
            return edge - 2
        }
    }
    return Int.MAX_VALUE
}