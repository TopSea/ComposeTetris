package top.topsea.composetetris.tetris

import androidx.compose.runtime.snapshots.SnapshotStateList
import top.topsea.composetetris.tetris.Model.*
import kotlin.math.abs

class Tetris(
    private val tetris: SnapshotStateList<SnapshotStateList<Int>>
) : ITetrisActionListener {
    private val playBoard: Array<Array<Int>>


    private var curModel = arrayOf(0, 0, 0, 0)


    companion object {
        const val width = 10
        const val height = 12
    }

    init {
        playBoard = Array(height + 5) { row ->
            if (row == height + 4) {
                Array(width) { 1 }
            } else {
                Array(width) { 0 }
            }
        }
    }

    fun play() {
        val listModel = listOf(MODEL_O, MODEL_T, MODEL_S, MODEL_Z, MODEL_I, MODEL_L, MODEL_J)
        val gameOver = false

        while (!gameOver) {
//            placeModelToBoard(listModel.random().values)
        }
    }

    fun placeModelToBoard(model: Array<Int>) {
        var stop = false
        //放置模块
        for (i in model.indices) {
            val x = model[i] / width
            val y = model[i] % width
            playBoard[x][y] = 1
        }
        curModel = Array(model.size) { i ->
            model[i] - 40
        }

        while (!stop) {
            var canPlace = true
            for (y in 0 until width) {
                //从下往上扫，扫到一个就判断，判断完就可以扫下一列
                for (x in (playBoard.size - 2) downTo 0) {
                    if (playBoard[x][y] != 0) {
//                        println("gaohai:::$x")
                        if (x < 4) {                //没有进入用户视野的不管
                            playBoard[x][y] = 0
                            playBoard[x + 1][y] = 1
                        } else {
                            val temp = x - 3
                            println("gaohai:::$temp")
                            if (tetris[temp][y] != 0) {
                                canPlace = false
                                stop = true
                            } else {
                                playBoard[x][y] = 0
                                playBoard[x + 1][y] = 1
                            }
                        }
                        break
                    }
                }
                if (!canPlace) {
                    break
                }
            }
            Thread.sleep(2000)
            if (canPlace) {
                for (i in curModel.indices) {
                    curModel[i] += 10
                }

                stop = placeToTetris(curModel)
            }
        }
        clearPlayBoard()
    }

    private fun placeToTetris(model: Array<Int>): Boolean {
        for (i in model.size - 1 downTo 0) {
            if (model[i] < 0) {
                continue
            }
            val x = model[i] / width
            val y = model[i] % width
            if (x > tetris.size - 1) {
                return true
            }
            if (x > 0) {
                tetris[x - 1][y] = 0
            }
            tetris[x][y] = 1
        }
        printTetris()
        return false
    }

    private fun clearPlayBoard() {
        for (i in playBoard.indices) {
            for (j in playBoard[i].indices) {
                playBoard[i][j] = 0
            }
        }
    }

    fun printTetris() {
        for (i in tetris.indices) {
            println("GaoHai:::Tetris ${tetris[i]}")
        }
        println("GaoHai:::Tetris ------------------------------")
    }

    fun printPlayBoard() {
        for (i in playBoard.indices) {
            println("GaoHai:::Tetris ${playBoard[i].contentToString()}")
        }
        println("GaoHai:::Tetris ------------------------------")
    }

    override fun onAction(dir: Int) {
        when (dir) {
            ITetrisActionListener.DIR_T -> {
                onRotate()
            }
            ITetrisActionListener.DIR_D -> {
                moveDown()
            }
            ITetrisActionListener.DIR_L -> {
                moveLeft()
            }
            ITetrisActionListener.DIR_R -> {
                moveRight()
            }
        }
    }

    override fun onRotate() {
        TODO("Not yet implemented")
    }

    override fun moveRight() {
        val tempModel = Array(curModel.size) { i ->
            if (curModel[i] < 0) {
                curModel[i] - 1
            } else {
                curModel[i] + 1
            }
        }
        for (i in tempModel.size - 1 downTo 0) {
            val x = tempModel[i] / width
            val y = tempModel[i] % width
            //已经靠边
            if (abs(y) == 0) {
                return
            }
            if (x > 0) {
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
        printTetris()
    }

    override fun moveLeft() {
        val tempModel = Array(curModel.size) { i ->
            if (curModel[i] < 0) {
                curModel[i] + 1
            } else {
                curModel[i] - 1
            }
        }
        for (i in tempModel.size - 1 downTo 0) {
            val x = tempModel[i] / width
            val y = tempModel[i] % width
            //已经靠边
            if (abs(y) == 9) {
                return
            }
            if (x > 0) {
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
        printTetris()
    }

    override fun moveDown() {
        TODO("Not yet implemented")
    }
}