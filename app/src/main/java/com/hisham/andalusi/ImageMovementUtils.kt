package com.hisham.andalusi

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hisham.andalusi.models.Area
import com.hisham.andalusi.models.Movement

fun getImageMovementData(
    canvasSize: Size,
    interestArea: Area,
): Movement {
    val canvasWidth = canvasSize.width
    val canvasHeight = canvasSize.height

    // interest area -> abs size (original image)
    val interestWidth = interestArea.width * canvasWidth
    val interestHeight = interestArea.height * canvasHeight

    // Calculate the scale to fit the interest area into the canvas
    val scaleX = canvasWidth / interestWidth
    val scaleY = canvasHeight / interestHeight
    val scale = minOf(scaleX, scaleY) // Maintain aspect ratio 

    // Calculate translation 
    val scaledInterestWidth = interestWidth * scale
    val scaledInterestHeight = interestHeight * scale

    val translationX =
        (canvasWidth - scaledInterestWidth) / 2 - interestArea.x * canvasWidth * scale
    val translationY =
        (canvasHeight - scaledInterestHeight) / 2 - interestArea.y * canvasHeight * scale

    return Movement(scale, Offset(translationX, translationY))
}
