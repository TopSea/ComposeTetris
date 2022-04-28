package top.topsea.composetetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import top.topsea.composetetris.tetris.*
import top.topsea.composetetris.ui.theme.ComposeTetrisTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTetrisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val listModel = listOf( Model.MODEL_O, Model.MODEL_T, Model.MODEL_S, Model.MODEL_Z, Model.MODEL_I, Model.MODEL_L, Model.MODEL_J)

                    var gameOver by remember { mutableStateOf(false) }
                    var nxtModel by remember { mutableStateOf(listModel.random()) }
                    val currModel = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
                    val currModelList = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
                    val currModelType = remember { mutableStateOf(Int.MAX_VALUE) }
                    val currScore = remember { mutableStateOf(0) }
                    val modelPlaced = remember { mutableStateOf(false) }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            contentAlignment = Alignment.TopStart,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 20.dp, start = 20.dp),
                                text = stringResource(id = R.string.best_record, currScore.value),
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            modifier = Modifier.padding(bottom = 24.dp),
                            text = stringResource(id = R.string.curr_score, currScore.value),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                        GameInfo(currModelList, nxtModel)
                        Tetris(
                            modelPlaced = modelPlaced,
                            currModel = currModel,
                            currModelType = currModelType
                        )
                    }

                    LaunchedEffect(key1 = Unit) {
                        currModelType.value = nxtModel.type
                        nxtModel.values.forEachIndexed { index, curr ->
                            currModel[index] = curr
                            currModelList[index] = curr
                        }
                        nxtModel = listModel.random()
                        while (true) {
                            delay(800)
                            if (!modelPlaced.value) {
                                normalDown(currModel, modelPlaced)
                            }
                        }
                    }

                    LaunchedEffect(key1 = modelPlaced.value) {
                        if (modelPlaced.value) {
                            currScore.value += 5
                            eraseLines(currScore)
                            currModelType.value = nxtModel.type
                            nxtModel.values.forEachIndexed { index, curr ->
                                if (curr > 0) {
                                    val x = curr / WIDTH
                                    val y = curr % WIDTH
                                    if (tetris[x][y] != 0) {
                                        gameOver = true
                                    }
                                }
                                currModel[index] = curr
                                currModelList[index] = curr
                            }
                            if (!gameOver) {
                                nxtModel = listModel.random()
                                modelPlaced.value = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameInfo(
    currModel: List<Int>,
    nxtModel: Model,
) {
    val requireSize = LocalDensity.current.run { (SIZE * 2.5).toInt().toDp() }
    Row(
        modifier = Modifier.padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.curr_model),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )
        Canvas(modifier = Modifier
            .requiredSize(requireSize)
            .padding(8.dp)
        ) {
            val upSteps = abs(currModel.minOrNull()!! / WIDTH) + 1
            currModel.forEachIndexed { _, v ->
                val up = v - 4 + upSteps * WIDTH
                val x = up / WIDTH
                val y = up % WIDTH

                drawRect(
                    Color.Gray,
                    topLeft = Offset(
                        x = y * SIZE * 0.5f,
                        y = x * SIZE * 0.5f
                    ),
                    size = Size( (SIZE - 3) * 0.5f,  (SIZE - 3) * 0.5f)
                )
            }
        }
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.nxt_model),
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        )
        Canvas(modifier = Modifier
            .requiredSize(requireSize)
            .padding(8.dp)
        ) {
            val upSteps = abs(nxtModel.values.minOrNull()!! / WIDTH) + 1
            nxtModel.values.forEachIndexed { _, v ->
                val up = v - 4 + upSteps * WIDTH
                val x = up / WIDTH
                val y = up % WIDTH

                drawRect(
                    Color.Gray,
                    topLeft = Offset(
                        x = y * SIZE * 0.5f,
                        y = x * SIZE * 0.5f
                    ),
                    size = Size( (SIZE - 3) * 0.5f,  (SIZE - 3) * 0.5f)
                )
            }
        }
    }
}