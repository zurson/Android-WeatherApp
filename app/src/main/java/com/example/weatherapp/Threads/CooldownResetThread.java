package com.example.weatherapp.Threads;

import com.example.weatherapp.Interfaces.Cooldown;

public class CooldownResetThread extends Thread {

    private final Cooldown cooldown;
    private final long timeMs;

    public CooldownResetThread(Cooldown cooldown, long timeMs) {
        this.cooldown = cooldown;
        this.timeMs = timeMs;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(timeMs);
            cooldown.resetCooldown();
        } catch (InterruptedException ignored) {
        }

    }

}
