package com.darekbx.lifetimememo.commonui.theme

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val EmptyIcon: ImageVector
    get() {
        if (_emptyIcon != null) {
            return _emptyIcon!!
        }
        _emptyIcon = materialIcon(name = "Empty") {
            materialPath {  }
        }
        return _emptyIcon!!
    }

private var _emptyIcon: ImageVector? = null

val ArrowRight: ImageVector
    get() {
        if (_arrowRight != null) {
            return _arrowRight!!
        }
        _arrowRight = materialIcon(name = "ArrowRight") {
            materialPath {
                moveTo(10.0f, 7.0f)
                verticalLineToRelative(10.0f)
                lineToRelative(5.0f, -5.0f)
                close()
            }

        }
        return _arrowRight!!
    }

private var _arrowRight: ImageVector? = null