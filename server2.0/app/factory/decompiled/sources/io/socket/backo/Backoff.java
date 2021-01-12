package io.socket.backo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Backoff {
    private int attempts;
    private int factor = 2;
    private double jitter;
    private long max = 10000;
    private long ms = 100;

    public long duration() {
        BigInteger valueOf = BigInteger.valueOf(this.ms);
        BigInteger valueOf2 = BigInteger.valueOf((long) this.factor);
        int i = this.attempts;
        this.attempts = i + 1;
        BigInteger multiply = valueOf.multiply(valueOf2.pow(i));
        if (this.jitter != 0.0d) {
            double random = Math.random();
            BigInteger bigInteger = BigDecimal.valueOf(random).multiply(BigDecimal.valueOf(this.jitter)).multiply(new BigDecimal(multiply)).toBigInteger();
            multiply = (((int) Math.floor(random * 10.0d)) & 1) == 0 ? multiply.subtract(bigInteger) : multiply.add(bigInteger);
        }
        return multiply.min(BigInteger.valueOf(this.max)).longValue();
    }

    public void reset() {
        this.attempts = 0;
    }

    public Backoff setMin(long j) {
        this.ms = j;
        return this;
    }

    public Backoff setMax(long j) {
        this.max = j;
        return this;
    }

    public Backoff setFactor(int i) {
        this.factor = i;
        return this;
    }

    public Backoff setJitter(double d) {
        this.jitter = d;
        return this;
    }

    public int getAttempts() {
        return this.attempts;
    }
}
