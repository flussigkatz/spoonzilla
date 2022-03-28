package xyz.flussigkatz.remote.entity.instructions_by_id


import com.google.gson.annotations.SerializedName

data class InstructionsItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("steps")
    val steps: List<Step>
)