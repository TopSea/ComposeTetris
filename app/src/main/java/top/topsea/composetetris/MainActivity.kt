package top.topsea.composetetris

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
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
import java.io.File
import kotlin.math.abs
import kotlin.math.max

class MainActivity : ComponentActivity() {
    private var activity: Activity? = null

    private val tetrisFile = "tetris.dat"
    private val modelFile = "model.dat"

    private var bestRecord = 0
    private var exit: (exitHow: String) -> Unit = {}
    private var modelBefore: List<Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this

        bestRecord = activity!!.getPreferences(Context.MODE_PRIVATE).getInt(
            getString(R.string.best_record_sp), 0
        )

        val tetrisData = File(activity!!.filesDir, tetrisFile)
        if (tetrisData.exists()) {
            val dataStr = readArray(applicationContext, tetrisFile)
            val tempArray = stringToArray(dataStr)

            for (x in 0 until HEIGHT) {
                for (y in 0 until WIDTH) {
                    tetris[x][y] = tempArray[x][y]
                }
            }
        }

        val modelData = File(activity!!.filesDir, modelFile)
        if (modelData.exists()) {
            modelBefore = readModel(applicationContext, modelFile)
        }

        setContent {
            ComposeTetrisTheme {
                val justComeIn = remember { mutableStateOf(true) }
                val gameOver = remember { mutableStateOf(false) }
                val currScore = remember { mutableStateOf(0) }
                val modelPlaced = remember { mutableStateOf(false) }

                var nxtModel by remember { mutableStateOf(Model.MODEL_O) }
                val currModel = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
                val currModelList = remember { mutableStateListOf(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE) }
                val currModelType = remember { mutableStateOf(Int.MAX_VALUE) }

                exit = { exitHow ->
                    bestRecord = max(bestRecord, currScore.value)
                    val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
                    with (sharedPref.edit()) {
                        putInt(getString(R.string.best_record_sp), bestRecord)
                        apply()
                    }
                    when (exitHow) {
                        "GAME_OVER" -> {
                            if (tetrisData.exists()) {
                                tetrisData.delete()
                            }
                            if (modelData.exists()) {
                                modelData.delete()
                            }
                            this.finish()
                        }
                        "ON_STOP" -> {
                            saveArray(applicationContext, tetris, tetrisFile)
                            saveModel(applicationContext, currModel, modelFile)
                        }
                    }
                }
                ComeInDialog(justComeIn)
                GameOverDialog(gameOver, modelPlaced, currScore.value, exit)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val listModel = listOf( Model.MODEL_O, Model.MODEL_T, Model.MODEL_S, Model.MODEL_Z, Model.MODEL_I, Model.MODEL_L, Model.MODEL_J)

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
                                text = stringResource(id = R.string.best_record, bestRecord),
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
                        ModelInfo(currModelList, nxtModel)
                        Tetris(
                            gameOver = gameOver,
                            modelPlaced = modelPlaced,
                            currModel = currModel,
                            currModelType = currModelType
                        )
                    }

                    LaunchedEffect(key1 = justComeIn.value, key2 = gameOver.value) {
                        if (modelBefore != null) {
                            modelBefore!!.forEachIndexed { index, before ->
                                currModel[index] = before
                                currModelList[index] = before
                                modelPlaced.value = false
                            }
                        } else {
                            nxtModel = listModel.random()
                            currModelType.value = nxtModel.type
                            nxtModel.values.forEachIndexed { index, curr ->
                                currModel[index] = curr
                                currModelList[index] = curr
                            }
                        }
                        nxtModel = listModel.random()

                        if (!justComeIn.value && !gameOver.value) {
                            while (true) {
                                delay(800)
                                if (!modelPlaced.value) {
                                    normalDown(currModel, modelPlaced)
                                }
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
                                        gameOver.value = true
                                    }
                                }
                                currModel[index] = curr
                                currModelList[index] = curr
                            }
                            if (!gameOver.value) {
                                nxtModel = listModel.random()
                                modelPlaced.value = false
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onStop() {
        exit("ON_STOP")
        super.onStop()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

@Composable
fun ModelInfo(
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
