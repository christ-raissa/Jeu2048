package com.example.jeu2048.utils;

public abstract class CountUpTimer {

    private final android.os.Handler handler = new android.os.Handler();
    private long startTime = 0;
    private boolean running = false;

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            long elapsed = System.currentTimeMillis() - startTime;
            onTick(elapsed);
            handler.postDelayed(this, 1000);
        }
    };

    public abstract void onTick(long millis);

    public void start() {
        if (running) return;
        running = true;
        startTime = System.currentTimeMillis();
        handler.post(tickRunnable);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(tickRunnable);
    }

    public void setStartTime(long millis) {
        startTime = System.currentTimeMillis() - millis;
    }
}
