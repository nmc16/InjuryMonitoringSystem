package controller;

import sendable.Sendable;

public interface Producer {
	public void connectTo(String clientIP, int clientPort);
	public void send(Sendable sendable, Class<?> sendableType);
	public void disconnectFromClient();
}
