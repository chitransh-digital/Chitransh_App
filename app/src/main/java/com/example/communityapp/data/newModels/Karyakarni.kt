package com.example.communityapp.data.newModels

data class Karyakarni(
    val address: String,
    val city: String,
    val designations: List<String>,
    val id: String,
    val landmark: String,
    var level: String,
    val logo: String,
    val members: List<KaryaMember> = emptyList(),
    var name: String,
    val state: String
): java.io.Serializable{
    override fun hashCode(): Int {
        var result = id.hashCode()
        if(members.isEmpty()){
            result = 31 * result + members.hashCode()
        }
        return result
    }
}