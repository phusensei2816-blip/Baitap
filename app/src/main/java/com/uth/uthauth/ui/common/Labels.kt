package com.uth.uthauth.ui.common

import androidx.annotation.StringRes
import com.uth.uthauth.R
import com.uth.uthauth.domain.model.TaskCategory
import com.uth.uthauth.domain.model.TaskPriority

@StringRes
fun TaskCategory.labelRes(): Int = when (this) {
    TaskCategory.STUDY -> R.string.category_study
    TaskCategory.LIFE -> R.string.category_life
}

@StringRes
fun TaskPriority.labelRes(): Int = when (this) {
    TaskPriority.LOW -> R.string.priority_low
    TaskPriority.MEDIUM -> R.string.priority_medium
    TaskPriority.HIGH -> R.string.priority_high
}
