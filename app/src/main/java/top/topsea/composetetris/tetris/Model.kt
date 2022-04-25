package top.topsea.composetetris.tetris

enum class Model {
    MODEL_O {
        override val type: Int
            get() = O
        override val values: List<Int>
            get() = listOf(4, 5, 14, 15)
    },
    MODEL_T {
        override val type: Int
            get() = T
        override val values: List<Int>
            get() = listOf(3, 4, 5, 14)
    },
    MODEL_S {
        override val type: Int
            get() = S
        override val values: List<Int>
            get() = listOf(14, 15, 5, 6, )
    },
    MODEL_Z {
        override val type: Int
            get() = Z
        override val values: List<Int>
            get() = listOf(5, 15, 4, 16)
    },
    MODEL_I {
        override val type: Int
            get() = I
        override val values: List<Int>
            get() = listOf(5, 15, 25, 35)
    },
    MODEL_L {
        override val type: Int
            get() = L
        override val values: List<Int>
            get() = listOf(5, 15, 25, 26)
    },
    MODEL_J {
        override val type: Int
            get() = J
        override val values: List<Int>
            get() = listOf(5, 15, 24, 25)
    };

    abstract val type: Int
    abstract val values: List<Int>

    companion object {
        const val O = 70
        const val T = 71
        const val S = 72
        const val Z = 73
        const val I = 74
        const val L = 75
        const val J = 76
    }
}