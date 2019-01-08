package pl.transity.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class AppExecutors private constructor(
        val background : Executor,
        val diskIO: Executor,
        val networkIO: Executor,
        val sheduler: ScheduledExecutorService,
        val mainThread: Executor
) {

    companion object {
        private val TAG = AppExecutors::class.simpleName
        private var INSTANCE: AppExecutors? = null

        fun getInstance(context: Context): AppExecutors =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildAppExecutors().also { INSTANCE = it }
                }

        private fun buildAppExecutors() =
                AppExecutors(
                        Executors.newFixedThreadPool(3),
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        Executors.newScheduledThreadPool(3),
                        MainThreadExecutor()
                )
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}