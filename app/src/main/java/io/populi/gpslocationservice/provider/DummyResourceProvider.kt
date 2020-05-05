package io.populi.gpslocationservice.provider

import android.location.Location
import io.reactivex.Single

class DummyResourceProvider(val popMapUid: String) {

    fun getNearestTrack(location: Location): Single<Any> {
        return Single.fromCallable {

            //            do some math  due to location
            //            find(uid).findAllTrack().filter(it.location==Location)
            return@fromCallable Any()
        }
    }
}
