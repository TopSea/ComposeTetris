package top.topsea.composetetris.tetris

interface ITetrisActionListener {
    companion object {
        const val DIR_L = 40            //左
        const val DIR_R = 41            //右
        const val DIR_T = 42            //上
        const val DIR_D = 43            //下
    }

    fun onAction(dir: Int)

    fun onRotate()

    fun moveLeft()

    fun moveRight()

    fun moveDown()
}