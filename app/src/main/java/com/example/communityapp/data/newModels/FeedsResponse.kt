package com.example.communityapp.data.newModels

import android.os.Parcel
import android.os.Parcelable
import com.example.communityapp.data.models.NewsFeed
import java.io.Serializable

data class FeedsResponse(
    var Feeds: List<NewsFeed>,
    var count: Int,
    var message: String,
    var status: Boolean
):Serializable,Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(NewsFeed).orEmpty(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(Feeds)
        parcel.writeInt(count)
        parcel.writeString(message)
        parcel.writeByte(if (status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedsResponse> {
        override fun createFromParcel(parcel: Parcel): FeedsResponse {
            return FeedsResponse(parcel)
        }

        override fun newArray(size: Int): Array<FeedsResponse?> {
            return arrayOfNulls(size)
        }
    }
}