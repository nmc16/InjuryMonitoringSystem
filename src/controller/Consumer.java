package controller;

import sendable.Sendable;

public interface Consumer {
	public void host(int hostPort);
	public <T extends Sendable> T receive(Class<T> sendable);
	public void disconnectHost();
}
