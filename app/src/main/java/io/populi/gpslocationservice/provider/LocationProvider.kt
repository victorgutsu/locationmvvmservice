package io.populi.gpslocationservice.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit


class LocationProvider(val context: Context) {
    private val TAG = "MainViewModel"

    private val LOCATION_INTERVAL = 500
    private val LOCATION_DISTANCE = 10

    private var mLocationManager: LocationManager? = null
    private var mLocationListener: LocationListener = AutoPlayLocationListener()

    private var source = PublishSubject.create<Location>()


    inner class AutoPlayLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.i(TAG, "LocationChanged: $location")
            source.onNext(location)
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(TAG, "onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(TAG, "onProviderEnabled: $provider")
        }

        override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
        ) {
            Log.e(TAG, "onStatusChanged: $status")
        }
    }


    fun stop() {
        try {
            mLocationManager?.removeUpdates(mLocationListener)
        } catch (ex: Exception) {
            Log.i(TAG, "fail to remove location listeners, ignore", ex)
        }
    }

    fun start(): Observable<Location> {
        //FIXME why last location does not come!
        lastLocation(context, TimeUnit.SECONDS.toMillis(5))?.let {
            mLocationListener.onLocationChanged(it)
        }
        listenGpsChanges()

        return source
    }

    private fun listenGpsChanges() {
        if (mLocationManager == null) {
            mLocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE.toFloat(),
                mLocationListener
            )
        } catch (ex: SecurityException) {
            Log.e(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "gps provider does not exist " + ex.message)
        }
    }

    private fun lastLocation(context: Context, locationTimeoutMs: Long = 0): Location? {
        when {
            mLocationManager == null -> return null
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED -> return null
            else -> {
                val lastKnownLocationGPS: Location? =
                    mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val lastLoc = lastKnownLocationGPS
                    ?: mLocationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                return if (lastLoc != null && locationTimeoutMs != 0L) {
                    val currentMs = Calendar.getInstance().timeInMillis
                    val locationTime = lastLoc.time
                    if (currentMs - locationTime < locationTimeoutMs) {
                        return lastLoc
                    } else null
                } else lastLoc
            }
        }
    }
}