package com.example.communityapp.data.models

import android.os.Parcel
import android.os.Parcelable

data class NewsFeed (
    val author: String,
    val body: String,
    val images: List<String>,
    val timestamp: String,
    val title: String,
    val visible: Boolean,
    val location:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(body)
        parcel.writeStringList(images)
        parcel.writeString(timestamp)
        parcel.writeString(title)
        parcel.writeByte(if (visible) 1 else 0)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsFeed> {
        override fun createFromParcel(parcel: Parcel): NewsFeed {
            return NewsFeed(parcel)
        }

        override fun newArray(size: Int): Array<NewsFeed?> {
            return arrayOfNulls(size)
        }
    }
}