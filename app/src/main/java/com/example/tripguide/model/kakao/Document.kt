package com.example.tripguide.model.kakao

data class Document(
    val address: Address,
    val address_name: String,
    val address_type: String,
    val road_address: Any,
    val x: String,
    val y: String
)
