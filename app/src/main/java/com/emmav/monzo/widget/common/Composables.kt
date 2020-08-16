package com.emmav.monzo.widget.common

import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import java.util.*

@Composable
fun Info(
    modifier: Modifier,
    emoji: Text,
    title: Text,
    subtitle: Text
) {
    Column(modifier = modifier, horizontalGravity = Alignment.CenterHorizontally) {
        Text(
            text = ContextAmbient.current.resolveText(emoji),
            fontSize = 84.sp
        )
        Text(
            text = ContextAmbient.current.resolveText(title),
            fontSize = 22.sp,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = ContextAmbient.current.resolveText(subtitle),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSecondary.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun EmptyState(
    emoji: Text,
    title: Text,
    subtitle: Text
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        gravity = ContentGravity.Center
    ) {
        Info(
            modifier = Modifier.padding(16.dp),
            emoji = emoji,
            title = title,
            subtitle = subtitle
        )
    }
}

@Preview
@Composable
fun InfoPreview() {
    AppTheme {
        Info(modifier = Modifier, emoji = text("🙌🏽"), title = text("title"), subtitle = text("subtitle"))
    }
}

@Composable
fun FullWidthButton(
    onClick: () -> Unit,
    title: Int,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
    ) {
        Text(
            text = ContextAmbient.current.getString(title).toUpperCase(Locale.getDefault()),
            color = MaterialTheme.colors.onPrimary,
        )
    }
}