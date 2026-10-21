package com.tet;

public class TetState {
    public int throttleN = 0;
    public int brakeN = 0;
    public int autoStopTicks = 0;      // 自动熄火倒计时
    public int sealedMessageTicks = 0; // 封口提示独占 action bar 的剩余 tick

    public int netForce() {
        return Math.max(0, throttleN - brakeN);
    }
}