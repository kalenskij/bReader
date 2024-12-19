package com.example.breader.Models

import androidx.annotation.DrawableRes
import com.example.breader.enums.ToolsType

data class ToolsModel(
    val title: String,
    @DrawableRes
    val image: Int,
    val type: ToolsType
)
