package com.abbasandfriends.injurymonitoringsystem.async;

import sendable.Sendable;

/**
 * Interface that handles the data to add from the {@link AsyncRequest}
 */
public interface AsyncListener {
    /**
     * Hanlde the data that the request recovered from the database.
     *
     * @param sendable Sendable data received.
     */
    void addData(Sendable sendable);
}
