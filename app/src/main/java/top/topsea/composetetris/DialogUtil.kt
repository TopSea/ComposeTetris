package top.topsea.composetetris

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import top.topsea.composetetris.tetris.clearBoard

/**
 * @ProjectName:    ComposeTetris
 * @Package:        top.topsea.composetetris
 * @Description:    DialogUtil
 * @Author:         TopSea
 * @AboutAuthor:    https://github.com/TopSea
 * @CreateDate:     2022/4/29 14:09
 **/
@Composable
fun ComeInDialog(
    justComeIn: MutableState<Boolean>
) {
    if (justComeIn.value) {
        AlertDialog(
            onDismissRequest = { },
            text = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.play_now),
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                }
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            justComeIn.value = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.play_confirm),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun GameOverDialog(
    gameOver: MutableState<Boolean>,
    modelPlaced: MutableState<Boolean>,
    finalScore: Int,
    exit: (exitHow: String) -> Unit
) {
    if (gameOver.value) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over),
                        fontFamily = FontFamily.Default,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.final_score, finalScore),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameOver.value = false
                        modelPlaced.value = false
                        clearBoard()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over_again),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 20.sp,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        gameOver.value = true
                        clearBoard()
                        exit("GAME_OVER")
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over_quit),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 20.sp,
                    )
                }
            }
        )
    }
}
@Composable
fun ExitDialog(
    exitConfirm: MutableState<Boolean>
) {
    if (exitConfirm.value) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over),
                        fontFamily = FontFamily.Default,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        exitConfirm.value = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over_again),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 20.sp,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exitConfirm.value = true
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.game_over_quit),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Cursive,
                        fontSize = 20.sp,
                    )
                }
            }
        )
    }
}