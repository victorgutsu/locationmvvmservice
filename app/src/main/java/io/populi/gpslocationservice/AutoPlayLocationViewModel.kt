package io.populi.gpslocationservice

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.populi.gpslocationservice.provider.CLocation
import io.populi.gpslocationservice.provider.LocationProvider
import io.populi.gpslocationservice.provider.DummyResourceProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AutoPlayLocationViewModel(application: Application) : AndroidViewModel(application) {
    private var d: Disposable? = null
    var notificationLiveData = MutableLiveData<String>()

    private val locationProvider = LocationProvider(application)
    private val resourceProvider =
        DummyResourceProvider("uid")

    fun start() {
        d = locationProvider
            .start()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                findNearestTrack(it)
                CLocation(it)
            }
            .subscribe { location ->
                updateUi(location)
            }
    }

    private fun updateUi(location: Location) {
        notificationLiveData.value =
            "Location:${location.longitude} " + "and ${location.latitude}\n" +
                    "Speed:${location.speed}\n" +
                    "Accuracy:${location.accuracy}\n" +
                    "Bearing:${location.bearing}"
    }

    private fun findNearestTrack(it: Location) {
        resourceProvider.getNearestTrack(it)
    }

    fun stop() {
        locationProvider.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        d?.dispose()
    }
}
