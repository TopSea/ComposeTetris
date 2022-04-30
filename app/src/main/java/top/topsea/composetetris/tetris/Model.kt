package top.topsea.composetetris.tetris

import android.content.Context
import java.io.FileOutputStream
import java.io.ObjectOutputStream

enum class Model {
    MODEL_O {
        override val type: Int
            get() = O
        override val values: List<Int>
            get() = listOf(-5, -6, 4, 5)
    },
    MODEL_T {
        override val type: Int
            get() = T
        override val values: List<Int>
            get() = listOf(-5, -6, -7, 4)
    },
    MODEL_S {
        override val type: Int
            get() = S
        override val values: List<Int>
            get() = listOf(4, 5, -4, -5)
    },
    MODEL_Z {
        override val type: Int
            get() = Z
        override val values: List<Int>
            get() = listOf(-6, 5, -5, 6)
    },
    MODEL_I {
        override val type: Int
            get() = I
        override val values: List<Int>
            get() = listOf(-25, -15, -5, 5)
    },
    MODEL_L {
        override val type: Int
            get() = L
        override val values: List<Int>
            get() = listOf(-16, -6, 4, 5)
    },
    MODEL_J {
        override val type: Int
            get() = J
        override val values: List<Int>
            get() = listOf(-15, -5, 4, 5)
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

        fun returnModel(type: Int): Model {
            return when (type) {
                O -> {
                    MODEL_O
                }
                T -> {
                    MODEL_T
                }
                S -> {
                    MODEL_S
                }
                Z -> {
                    MODEL_Z
                }
                L -> {
                    MODEL_L
                }
                I -> {
                    MODEL_I
                }
                else -> {
                    MODEL_J
                }
            }
        }
    }
}