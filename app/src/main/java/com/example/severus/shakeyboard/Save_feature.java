package com.example.severus.shakeyboard;

/**
 * Created by abhishekcs10 on 15/11/16.
 */

public class Save_feature {
    private double _pressure;
    private double _speed;
    private double _duration;
    private int _id;
    public Save_feature(){
        _duration=0.0;
        _speed=0.0;
        _duration=0.0;

    }

    public void set_pressure(double pressure) {
        this._pressure = pressure;
    }

    public void set_speed(double speed) {
        this._speed = speed;
    }

    public void set_duration(double duration) {
        this._duration = duration;
    }

    public double get_pressure() {
        return _pressure;
    }

    public double get_speed() {
        return _speed;
    }

    public double get_duration() {
        return _duration;
    }
}
