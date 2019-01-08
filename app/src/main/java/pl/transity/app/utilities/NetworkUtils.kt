/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.transity.app.utilities

import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * These utilities will be used to communicate with the network.
 */
object NetworkUtils {

    private const val BASE_URL = "http://ztm-scsexpert.eu-central-1.elasticbeanstalk.com/api/v1"
    private const val STOPS_BASE_URL = BASE_URL
    private const val VEHICLES_BASE_URL = BASE_URL

    /**
     * Builds the URL used to query data server for Stops.
     * @return The URL to use to query the weather server.
     */
    fun buildStopsQueryUrl(): URL? {
        val builtUri = Uri.parse(STOPS_BASE_URL).buildUpon()
                //.appendQueryParameter(key,value)
                .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    fun buildVehiclesQueryUrl(): URL? {
        val builtUri = Uri.parse(VEHICLES_BASE_URL).buildUpon()
                //.appendQueryParameter(key,value)
                .build()
        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in` = urlConnection.inputStream

            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}