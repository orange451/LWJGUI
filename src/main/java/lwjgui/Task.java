/*
 *
 * Copyright (C) 2015-2020 Anarchy Engine Open Source Contributors (see CONTRIBUTORS.md)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 */

package lwjgui;

import java.util.ArrayList;
import java.util.List;

public abstract class Task<T> {

	private boolean done;
	private T value;
	private List<Thread> ts = new ArrayList<>();
	private OnFinished<T> onFinished;
	private Thread thread;

	public Task() {
	}

	public Task(OnFinished<T> onFinish) {
		this.onFinished = onFinish;
	}

	public boolean isDone() {
		return done;
	}

	public T get() {
		if (!done) {
			if (Thread.currentThread().equals(thread)) {
				System.out.println("Unable to lock current thread.");
				return null;
			}
			synchronized (ts) {
				ts.add(Thread.currentThread());
			}
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
			}
		}
		return value;
	}

	public void onCompleted(T value) {
	}

	/**
	 * <b>INTERNAL FUNCTION</b>
	 */
	public void callI() {
		if (done)
			return;
		value = call();
		done = true;
		for (Thread t : ts)
			t.interrupt();
		onCompleted(value);
		if (onFinished != null)
			onFinished.onFinished(value);
	}

	public Task<T> setOnFinished(OnFinished<T> onFinished) {
		this.onFinished = onFinished;
		return this;
	}

	protected abstract T call();

}
