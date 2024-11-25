package com.hisham.andalusi

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.hisham.andalusi.models.Area
import com.hisham.andalusi.models.Movement

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
) {
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.dog)
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("BRUSH", Context.MODE_PRIVATE)
    }

    val movement = getImageMovementData(
        Size(500f, 320f),
        Area(x = 0.4f, y = 0.6581f, width = 0.56f, height = 0.32f),
    )
    val localMovement = Movement(
        sharedPreferences.getFloat("scale", 1f),
        Offset(
            sharedPreferences.getFloat("translationX", 0f),
            sharedPreferences.getFloat("translationY", 0f)
        )
    )

    TextWithImageBrush(
        mainModifier = modifier,
        imageBitmap = imageBitmap,
        text = stringResource(R.string.app_name),
        movement = localMovement,
        onSave = { scale, translation ->
            sharedPreferences.edit {
                putFloat("scale", scale)
                putFloat("translationX", translation.x)
                putFloat("translationY", translation.y)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextWithImageBrush(
    mainModifier: Modifier = Modifier,
    imageBitmap: ImageBitmap,
    text: String,
    movement: Movement,
    onSave: (Float, Offset) -> Unit,
) {
    var scale by remember { mutableFloatStateOf(movement.scale) }
    var translation by remember {
        mutableStateOf(Offset(movement.translation.x, movement.translation.y))
    }

    val fontSize = 60.sp

    val textMeasure = rememberTextMeasurer()
    val textLayout = textMeasure.measure(
        text = text,
        style = TextStyle(fontSize = fontSize)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Andalusi") },
                actions = {
                    Text(
                        text = "Save",
                        modifier = Modifier.clickable {
                            onSave(scale, translation)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        Canvas(
            modifier = mainModifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        translation += pan
                    }
                }
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val shaderBrush = ShaderBrush(
                BitmapShader(
                    imageBitmap.asAndroidBitmap(),
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                ).apply {
                    setLocalMatrix(Matrix().apply {
                        postScale(scale, scale)
                        postTranslate(translation.x, translation.y)
                    })
                }
            )

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    text,
                    0f,
                    0f,
                    Paint().apply {
                        textSize = fontSize.toPx()
                        shader = shaderBrush.createShader(
                            Size(
                                textLayout.size.width.toFloat(),
                                textLayout.size.height.toFloat()
                            )
                        )
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

