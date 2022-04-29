package top.topsea.composetetris

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * @ProjectName:    ComposeTetris
 * @Package:        top.topsea.composetetris
 * @Description:    ArrayUtil
 * @Author:         TopSea
 * @AboutAuthor:    https://github.com/TopSea
 * @CreateDate:     2022/4/29 8:17
 **/

 /**
  * @method arrayToString
  * @description arrayToString: 第一二个有效字符分别为数组的长和宽
  * @date: 2022/4/29 8:36
  * @author: TopSea
  */
fun arrayToString(array: Array<IntArray>): String {
    val result = StringBuilder()
    result.append(array.size).append(" ")
    result.append(array[0].size).append(" ")
    for (ints in array) {
        for (j in 0 until array[0].size) {
            result.append(ints[j]).append(" ")
        }
    }
    return result.toString()
}

fun stringToArray(str: String): Array<IntArray> {
    val strArray = str.split(" ")
    val result = Array(strArray[0].toInt()) {
        IntArray(
            strArray[1].toInt()
        )
    }
    var sign = 2
    for (i in result.indices) {
        for (j in 0 until result[0].size) {
            result[i][j] = strArray[sign].toInt()
            sign++
        }
    }
    return result
}


fun saveArray(context: Context, array: Array<IntArray>, fileName: String) {
    try {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
            fos.write(
                arrayToString(
                    array
                ).toByteArray()
            )
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun saveModel(context: Context, model: List<Int>, fileName: String) {
    val strBuilder = StringBuilder("")
    model.forEach {
        strBuilder.append(it).append(" ")
    }
    try {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
            fos.write(strBuilder.toString().toByteArray())
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun readArray(context: Context, fileName: String): String {
    val fis: FileInputStream = context.openFileInput(fileName)
    val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
    val stringBuilder = java.lang.StringBuilder()
    try {
        BufferedReader(inputStreamReader).use { reader ->
            val line: String = reader.readLine()
            if (line.isNotEmpty()) {
                stringBuilder.append(line).append('\n')
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        inputStreamReader.close()
        fis.close()
    }
    return stringBuilder.toString()
}

fun readModel(context: Context, fileName: String): List<Int> {
    val fis: FileInputStream = context.openFileInput(fileName)
    val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
    val stringBuilder = java.lang.StringBuilder()
    try {
        BufferedReader(inputStreamReader).use { reader ->
            val line: String = reader.readLine()
            if (line.isNotEmpty()) {
                stringBuilder.append(line)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        inputStreamReader.close()
        fis.close()
    }
    val strList = stringBuilder.toString().split(" ")

    val tempList = mutableListOf<Int>()
    strList.forEach { s ->
        if (s.isNotEmpty()) {
            tempList.add(s.toInt())
        }
    }
    return tempList
}
