package pl.transity.app.markerclustering

import android.util.Log
import android.util.SparseArray
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.algo.Algorithm
import com.google.maps.android.clustering.algo.StaticCluster
import java.util.*

const val TAG = "StopSetClusterAlgorithm"
const val ZOOM_CLUSTER_CUTOFF = 14

class StopSetClusterAlgorithm<T : StopSetClusterItem> : Algorithm<T> {

    private val items = Collections.synchronizedSet(HashSet<T>())

    override fun addItem(item: T) {
        items.add(item)
    }

    override fun addItems(items: Collection<T>?) {
        items?.let { this.items.addAll(it) }
    }

    override fun removeItem(item: T) {
        items.remove(item)
    }

    override fun clearItems() {
        items.clear()
    }

    override fun getItems(): Set<T>? {
        return items
    }

    override fun getClusters(zoom: Double): MutableSet<out Cluster<T>> {
        Log.d(TAG,"Current zoom $zoom")
        val clusters = HashSet<StaticCluster<T>>()
        synchronized(items) {
            if (zoom > ZOOM_CLUSTER_CUTOFF) {
                for (item in items) {
                    val cluster = StaticCluster<T>(item.position)
                    cluster.add(item)
                    clusters.add(cluster)
                }
            } else {
                val sparseArray = SparseArray<StaticCluster<T>>()
                for (item in items) {
                    val set = item.set
                    var cluster = sparseArray[set]
                    if (cluster == null) {
                        cluster = StaticCluster(item.position)
                        sparseArray.append(set,cluster)
                        clusters.add(cluster)
                    }
                    cluster.add(item)
                }
            }
        }
        Log.d(TAG,"Total number of clusters ${clusters.size}")
        for (cluster in clusters) {
            Log.d(TAG,"${cluster.size}")
        }
        return clusters
    }
}