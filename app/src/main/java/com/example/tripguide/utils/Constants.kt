package com.example.tripguide.utils

object Constants {
    const val TAG: String = "로그"
}

class KakaoApi {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "kakaoAK 48ad751ca72b3e49a7f746f46b40b142"
    }
}

enum class REPONSE_STATEUS {
    OKEY, FAIL, NO_CONTENT
}


object API {
    const val BASE_URL : String = "https://openapi.naver.com/v1/"
    const val CLIENT_ID : String = "tlsWuqVpcy0CY6oqvnhf"
    const val CLIENT_SECRET : String = "IRVqBATb7R"
    const val SEARCH_REGION : String = "search/locals"
}
