package top.topsea.composetetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import top.topsea.composetetris.tetris.Model.*
import top.topsea.composetetris.tetris.Tetris
import top.topsea.composetetris.ui.theme.ComposeTetrisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTetrisTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Tetris()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val tetris = Array(Tetris.height + 1){ _ ->
        Array(Tetris.width){ 0 }
    }

    val changed = remember { mutableStateOf(0) }

    Column(modifier = Modifier) {
        Canvas(modifier = Modifier) {
//            val tetrisBoard = Tetris(tetris)
            val rectWidth = size.width / Tetris.width
            val rectHeight = size.height / Tetris.height

            val size = Size(rectWidth - 5, rectHeight - 5)
            for (x in tetris.indices) {
                for (y in tetris[x].indices) {
                    val color = if (tetris[x][y] != 0) {
                        Color.Gray
                    } else {
                        Color.LightGray
                    }
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            x = x * rectWidth,
                            y = y * rectHeight
                        ),
                        size = size
                    )
                }
            }
            println("gaohai::${changed}")
//            placeToTetris(model = MODEL_T.values, tetris, changed)
        }
    }
}


private fun placeToTetris(
    model: Array<Int>,
    tetris: Array<Array<Int>>,
    changed: MutableState<Int>
) {
    for (i in model.size - 1 downTo 0) {
        if (model[i] < 0) {
            continue
        }
        val x = model[i] / Tetris.width
        val y = model[i] % Tetris.width
        if (x > tetris.size - 1) {
            return
        }
        if (x > 0) {
            tetris[x - 1][y] = 0
            tetris[x][y] = 1
        }
    }
    changed.value ++
}