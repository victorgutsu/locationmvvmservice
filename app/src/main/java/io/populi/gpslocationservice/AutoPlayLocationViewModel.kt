package io.populi.gpslocationservice

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.populi.gpslocationservice.provider.LocationProvider
import io.populi.gpslocationservice.provider.DummyResourceProvider
import io.reactivex.disposables.Disposable

class AutoPlayLocationViewModel(application: Application) : AndroidViewModel(application) {
    private var d: Disposable? = null
    var notificationLiveData = MutableLiveData<Location>()

    val locationProvider =
        LocationProvider(application)
    val resourceProvider =
        DummyResourceProvider("uid")

    fun start() {
        d = locationProvider
            .start()
            .subscribe { location ->
                updateUi(location)
                findNearestTrack(location)
            }
    }

    private fun updateUi(it: Location) {
        notificationLiveData.value = it
    }

    private fun findNearestTrack(it: Location) {
        resourceProvider.getNearestTrack(it)
    }

    override fun onCleared() {
        super.onCleared()
        d?.dispose()
        locationProvider.stop()
    }
}
