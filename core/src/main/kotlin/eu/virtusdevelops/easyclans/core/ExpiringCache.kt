package eu.virtusdevelops.easyclans.core

import java.util.concurrent.TimeUnit

class ExpiringCache<T>(
    duration: Long,
    unit: TimeUnit
) {
    private val durationNanos = unit.toNanos(duration)

    @Volatile
    private var value: T? = null
    @Volatile
    private var expirationNanos: Long = 0


    fun put(put: T) {
        val now = System.nanoTime()

        synchronized(this) {
            if(put == null){
                this.invalidate()
                return
            }
            value = put
            val  nanos: Long = now + durationNanos
            this.expirationNanos = if (nanos <= 0)  1 else nanos
        }

    }

    fun get(): T? {
        val nanos = this.expirationNanos
        val now = System.nanoTime()

        if(nanos == 0L || (now - nanos >= 0 && durationNanos >= 0)){
            synchronized(this) {
                if(this.expirationNanos == nanos){
                    return null
                }
            }
        }

        return value;
    }

    fun invalidate() {
        expirationNanos = 0
    }

    fun isPermanent(): Boolean = durationNanos < 0L
}