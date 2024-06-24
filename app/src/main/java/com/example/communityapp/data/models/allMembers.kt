package com.example.communityapp.data.models

import com.example.communityapp.data.newModels.MemberX
import java.io.Serializable

data class allMembers(
    val allMembers : List<MemberX>
) : Serializable
