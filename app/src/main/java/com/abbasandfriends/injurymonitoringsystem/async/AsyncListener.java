package com.abbasandfriends.injurymonitoringsystem.async;

import sendable.Sendable;

public interface AsyncListener {
    void addData(Sendable sendable);
}
