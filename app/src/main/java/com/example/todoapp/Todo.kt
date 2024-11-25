package com.example.todoapp

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class Todo(
    var id: String = UUID.randomUUID().toString(),  // Generate a unique ID for each task
    var title: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: UUID.randomUUID().toString(),  // Read the id from Parcel
        parcel.readString() ?: ""  // Read the title from Parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)  // Write the id to Parcel
        parcel.writeString(title)  // Write the title to Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todo> {
        override fun createFromParcel(parcel: Parcel): Todo {
            return Todo(parcel)
        }

        override fun newArray(size: Int): Array<Todo?> {
            return arrayOfNulls(size)
        }
    }
}
