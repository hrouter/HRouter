package com.lq.core.util

import android.content.Context
import android.widget.Toast

internal fun showToast(
    context: Context,
    data: String,
) {
    Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
}
