package com.collegehelp.studentsupport.models

import com.collegehelp.studentsupport.R

enum class SupportCategoryType(
    val titleResId: Int,
    val descResId: Int,
    val infoResId: Int,
    val iconResId: Int,
    val bgColorResId: Int
) {
    ACADEMIC(
        titleResId = R.string.category_academic,
        descResId = R.string.category_academic_desc,
        infoResId = R.string.category_academic_info,
        iconResId = R.drawable.ic_academic,
        bgColorResId = R.color.academic_bg
    ),
    TECHNICAL(
        titleResId = R.string.category_technical,
        descResId = R.string.category_technical_desc,
        infoResId = R.string.category_technical_info,
        iconResId = R.drawable.ic_technical,
        bgColorResId = R.color.technical_bg
    ),
    LIBRARY(
        titleResId = R.string.category_library,
        descResId = R.string.category_library_desc,
        infoResId = R.string.category_library_info,
        iconResId = R.drawable.ic_library,
        bgColorResId = R.color.library_bg
    )
}
