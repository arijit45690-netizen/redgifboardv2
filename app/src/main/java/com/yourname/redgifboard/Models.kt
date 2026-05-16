package com.yourname.redgifboard

data class TokenResponse(
    val token: String = ""
)

data class GifUrls(
    val sd: String = "",
    val hd: String = "",
    val thumbnail: String = "",
    val vthumbnail: String = ""
)

data class GifItem(
    val id: String = "",
    val urls: GifUrls = GifUrls()
)

data class SearchResponse(
    val gifs: List<GifItem> = emptyList(),
    val page: Int = 1,
    val pages: Int = 1,
    val total: Int = 0
)
