package com.mleval.pexelsapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mleval.pexelsapp.R

@Composable
fun EmptyPhotosStub(
    modifier: Modifier = Modifier,
    text: String,
    onExploreClick: () -> Unit
) {
    Column (
        modifier=modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.clickable {
                onExploreClick()
            },
            text = stringResource(R.string.explore),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700,
            fontSize = 18.sp
        )
    }
}