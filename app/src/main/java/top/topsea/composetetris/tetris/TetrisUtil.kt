package top.topsea.composetetris.tetris

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.math.max
import kotlin.math.min

const val WIDTH = 10
const val HEIGHT = 12

private const val ACTION_L = 100                //向左滑动
private const val ACTION_R = 101                //向右滑动
private const val ACTION_B = 102                //向下滑动
private const val ACTION_NB = 103                //模块的正常下落

fun normalDown(
    tetris: Array<Array<Int>>,
    curModel: SnapshotStateList<Int>,
    modelPlaced: MutableState<Boolean>
) {
    val step = canDoTheAction(curModel, tetris, ACTION_NB)

    //放置模块
    for (i in curModel.indices) {
        curModel[i] += step * WIDTH
        val x = curModel[i] / WIDTH
        val y = curModel[i] % WIDTH

        if (y < 0 || y > WIDTH || x < 0 || x > HEIGHT) {
            continue
        }
        tetris[x][y] = 1
    }
    if (step == 0) {
        modelPlaced.value = true
    }
}

fun moveLeft(tetris: Array<Array<Int>>, curModel: SnapshotStateList<Int>) {
    val step = canDoTheAction(curModel, tetris, ACTION_L)
    if (step > 0) {
        for (i in curModel.size - 1 downTo 0) {
            if (curModel[i] < 0) {
                curModel[i] += step
            } else {
                curModel[i] -= step
            }
        }
    }
}

fun moveRight(tetris: Array<Array<Int>>, curModel: SnapshotStateList<Int>) {
    val step = canDoTheAction(curModel, tetris, ACTION_R)
    if (step > 0) {
        for (i in curModel.indices) {
            if (curModel[i] < 0) {
                curModel[i] -= step
            } else {
                curModel[i] += step
            }
        }
    }
}

fun moveDown(
    tetris: Array<Array<Int>>,
    curModel: SnapshotStateList<Int>,
    modelPlaced: MutableState<Boolean>
) {
    val step = canDoTheAction(curModel, tetris, ACTION_B)

    if (step > 0) {
        //放置模块
        for (i in curModel.indices) {
            curModel[i] += step * WIDTH
            val x = curModel[i] / WIDTH
            val y = curModel[i] % WIDTH

            if (y < 0 || y > WIDTH || x < 0 || x > HEIGHT) {
                continue
            }
            tetris[x][y] = 1
        }
    }
    modelPlaced.value = true
}

fun rotateModel(
    tetris: Array<Array<Int>>,
    curModel: SnapshotStateList<Int>,
    modelType: Int
) {
    if (!canRotate(tetris, modelType, curModel)) {
        return
    }
    Log.d("", "GaoHai:::${curModel.toIntArray().contentToString()}")

    //原先的位置置0
    for (i in curModel.indices) {
        if (curModel[i] == Int.MAX_VALUE) {
            continue
        }
        val x = curModel[i] / WIDTH
        val y = curModel[i] % WIDTH

        if (y < 0 || y > WIDTH || x < 0 || x > HEIGHT) {
            continue
        }
        tetris[x][y] = 0
    }

    when (modelType) {
        Model.I -> {

        }
        else -> {
            for (i in curModel.indices) {
                curModel[i] = rotateOnePiece(curModel[1], curModel[i], WIDTH)
            }
        }
    }

}

private fun canRotate(
    tetris: Array<Array<Int>>,
    modelType: Int,
    curModel: SnapshotStateList<Int>,
): Boolean {
    var ones = 0

    val centerX = curModel[1] / WIDTH
    val centerY = curModel[1] % WIDTH
    when(modelType) {
        Model.J, Model.L, Model.T, Model.Z, Model.S -> {
            for (x in centerX - 1 .. centerX + 1) {
                if (x < 0) {
                    return false
                }
                for (y in centerY - 1 .. centerY + 1) {
                    if (y > WIDTH - 1 || y < 0) {
                        return false
                    }
                    if (tetris[x][y] != 0) {
                        ones++
                    }
                    //是否有位置旋转
                    if (x > HEIGHT || ones > 4) {
                        return false
                    }
                }
            }
        }
        Model.I -> {
            for (x in centerX - 1 .. centerX + 2) {
                if (x < 0) {
                    return false
                }
                for (y in centerY - 2 .. centerY + 1) {
                    if (y > WIDTH - 1 || y < 0) {
                        return false
                    }
                    if (tetris[x][y] != 0) {
                        ones++
                    }
                    //是否有位置旋转
                    if (x > HEIGHT || ones > 4) {
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


 /**
  * @method  canDoTheAction
  * @description canDoTheAction: 判断是否可以进行操作
  * @date: 2022/4/26 14:07
  * @author: TopSea
  * @param curModel: 当前模块
  * @param tetris: 游戏面板
  * @param action: 需要判断的操作
  * @return 等于0表示不能进行操作
  */
fun canDoTheAction(
    curModel: SnapshotStateList<Int>,
    tetris: Array<Array<Int>>,
    action: Int
) : Int {
    var start = 0
    var end = 0
    val map = HashMap<Int, Int>()

    when (action) {
        ACTION_B, ACTION_NB -> {
            //记录模块中边界的位置
            for (i in curModel.indices) {
                val x = curModel[i] / WIDTH
                val y = curModel[i] % WIDTH
                tetris[x][y] = 0
                if (map.contains(y)) {
                    map[y] = max(map[y]!!, x)
                } else {
                    map[y] = x
                }
            }
            if (action == ACTION_B) {
                val tree = map.toSortedMap()
                start = tree.firstKey()
                end = tree.lastKey()
                Log.d("", "GaoHai:::moveDown columnStart ${start} columnEnd ${end}")

                var closest = HEIGHT - 1
                for (y in start .. end) {
                    for (x in map[y]!! .. HEIGHT) {
                        if (tetris[x][y] != 0) {
                            closest = min(closest, x - (map[y]!! + 1))
                            Log.d("", "GaoHai:::moveDown x - map[y]!! ${x - map[y]!!}  x ${x}  y ${y}")
                            break
                        }
                    }
                }
                Log.d("", "GaoHai:::moveDown closest ${closest}")
                return closest
            } else {
                map.forEach { (x, y) ->
                    Log.d("", "GaoHai:::moveDown x ${x}  y ${y}")
                    if(y >= -1) {
                        if (tetris[y + 1][x] != 0) {
                            return 0
                        }
                    }
                }
                return 1
            }
        }
        ACTION_L -> {
            //记录模块中边界的位置
            for (i in curModel.indices) {
                val x = curModel[i] / WIDTH
                val y = curModel[i] % WIDTH
                tetris[x][y] = 0
                if (map.contains(x)) {
                    map[x] = min(map[x]!!, y)
                } else {
                    map[x] = y
                }
            }
            map.forEach { (x, y) ->
                if (y - 1 < 0 || tetris[x][y - 1] != 0) {
                    return 0
                }
            }
            return 1
        }
        ACTION_R -> {
            //记录模块中边界的位置
            for (i in curModel.indices) {
                val x = curModel[i] / WIDTH
                val y = curModel[i] % WIDTH
                tetris[x][y] = 0
                if (map.contains(x)) {
                    map[x] = max(map[x]!!, y)
                } else {
                    map[x] = y
                }
            }
            map.forEach { (x, y) ->
                if (y + 1 > WIDTH - 1 || tetris[x][y + 1] != 0) {
                    return 0
                }
            }
            return 1
        }
    }
    return 0
}