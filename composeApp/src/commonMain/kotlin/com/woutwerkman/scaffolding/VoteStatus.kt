package com.woutwerkman.scaffolding

import kotlinx.serialization.Serializable

@Serializable
data class VoteStatus(
    val number_of_red_votes: Int,
    val number_of_blue_votes: Int,
)
