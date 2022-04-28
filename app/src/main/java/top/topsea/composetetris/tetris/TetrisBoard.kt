package top.topsea.composetetris.tetris

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs
import kotlin.math.max

val tetris = Array(HEIGHT + 1){ row ->
    if (row == HEIGHT) {
        IntArray(WIDTH){ 1 }
    } else {
        IntArray(WIDTH){ 0 }
    }
}

const val SIZE = 80f

@Composable
fun Tetris(
    gameOver: MutableState<Boolean>,
    modelPlaced: MutableState<Boolean>,
    currModel: SnapshotStateList<Int>,
    currModelType: MutableState<Int>
) {
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
                            if (!gameOver.value) {
                                val x = abs(offsetX)
                                val y = abs(offsetY)
                                if (x > y) {
                                    if (offsetX < 0) {
                                        moveLeft(currModel)
                                    } else {
                                        moveRight(currModel)
                                    }
                                } else {
                                    if (offsetY < 0) {
                                        rotateModel(currModel, currModelType)
                                    } else {
                                        moveDown(currModel, modelPlaced)
                                    }
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
        ) {
            val curSize = max(size.width / WIDTH, SIZE)

            for (i in currModel.indices) {
                if (currModel[i] == Int.MAX_VALUE || currModel[i] < 0) {
                    continue
                }
                val x = currModel[i] / WIDTH
                val y = currModel[i] % WIDTH

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
                        size = Size(curSize - 3, curSize - 3)
                    )
                }
            }
        }
    }
}
