package com.na_at.fad.randomnumberlab.sign_drawn.model

import java.io.File
import java.io.Serializable

data class VideoSignResult(
    val signVideoUrl: File,
    val signImageUrl: File,
    val signerVideoUrl: File,
    val signatureData: Signature
) : Serializable
