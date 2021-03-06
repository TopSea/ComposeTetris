package top.topsea.composetetris

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.topsea.composetetris.tetris.clearBoard
import top.topsea.composetetris.ui.theme.dialogBtn
import top.topsea.composetetris.ui.theme.dialogText
import top.topsea.composetetris.ui.theme.dialogTitle

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
    hasRecord: Boolean,
    confirm: () -> Unit,
    dismiss: () -> Unit,
) {
    var showThis by remember { mutableStateOf(true) }
    if (showThis) {
        AlertDialog(
            onDismissRequest = { showThis = false },
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (hasRecord) stringResource(id = R.string.game_continue) else stringResource(
                            id = R.string.play_now
                        ),
                        fontFamily = dialogTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showThis = false
                        confirm()
                    }
                ) {
                    Text(
                        text = if (hasRecord) stringResource(id = R.string.continue_before) else stringResource(
                            id = R.string.play_confirm
                        ),
                        fontFamily = dialogBtn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
            },
            dismissButton = if (hasRecord) {
                {
                    TextButton(
                        onClick = {
                            showThis = false
                            dismiss()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.game_new),
                            fontFamily = dialogBtn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    }
                }
            } else null
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
                        fontFamily = dialogTitle,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                }
            },
            text = {
                Text(
                    text = stringResource(id = R.string.final_score, finalScore),
                    fontFamily = dialogText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.offset(y = 10.dp)
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
                        fontFamily = dialogBtn,
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
                        text = stringResource(id = R.string.quit_game),
                        fontWeight = FontWeight.Bold,
                        fontFamily = dialogBtn,
                        fontSize = 20.sp,
                    )
                }
            }
        )
    }
}

@Composable
fun ExitDialog(
    exitConfirm: MutableState<Boolean>,
    confirm: () -> Unit,
    dismiss: () -> Unit,
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
                        text = stringResource(id = R.string.quit_game_confirm),
                        fontFamily = dialogTitle,
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
                        confirm()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.quit_game),
                        fontWeight = FontWeight.Bold,
                        fontFamily = dialogBtn,
                        fontSize = 20.sp,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exitConfirm.value = false
                        dismiss()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.quit_later),
                        fontWeight = FontWeight.Bold,
                        fontFamily = dialogBtn,
                        fontSize = 20.sp,
                    )
                }
            }
        )
    }
}