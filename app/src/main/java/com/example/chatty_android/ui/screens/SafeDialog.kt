package com.example.chatty_android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SafeDialog(
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = null,
    onDismissAction: (() -> Unit)? = null,
    confirmColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (dismissLabel != null) {
                        TextButton(onClick = { onDismissAction?.invoke() ?: onDismiss() }) {
                            Text(dismissLabel)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onConfirm) {
                        Text(confirmLabel, color = confirmColor, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
