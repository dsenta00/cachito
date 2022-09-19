package dsenta.cachito.model.leakybucket;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Data
@NoArgsConstructor
public class LeakyBucket implements Serializable {
    private static final long serialVersionUID = 301859009527064871L;
    private int perSecond;
    private int bucketMax;
    private final AtomicInteger current = new AtomicInteger(0);
    private AtomicLong lastCheck = new AtomicLong();

    public LeakyBucket(int perSecond, int bucketMax) {
        this.perSecond = perSecond;
        this.bucketMax = bucketMax;
        current.set(0);
        lastCheck.set(Date.from(Instant.now()).getTime());
    }

    public synchronized LeakyBucket add() {
        if (current.get() < this.bucketMax) {
            current.set(current.get() + 1);
        }

        return this;
    }

    public synchronized boolean isFull() {
        Date currentTime = Date.from(Instant.now());
        long milliseconds = currentTime.getTime() - this.lastCheck.get();
        long numberOfSeconds = milliseconds / 1000;
        long millisecondsRest = milliseconds % 1000;
        currentTime.setTime(currentTime.getTime() - millisecondsRest);
        this.lastCheck.set(currentTime.getTime());
        long toDecrease = numberOfSeconds / this.perSecond;

        if (current.get() - toDecrease < 0) {
            current.set(0);
            return false;
        }

        current.set(Math.toIntExact(current.get() - toDecrease));

        if (current.get() >= this.bucketMax) {
            current.set(this.bucketMax);

            return true;
        }

        return false;
    }
}