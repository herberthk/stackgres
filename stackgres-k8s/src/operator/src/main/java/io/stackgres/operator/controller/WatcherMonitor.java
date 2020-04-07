/*
 * Copyright (C) 2019 OnGres, Inc.
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package io.stackgres.operator.controller;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;

public class WatcherMonitor<T> implements AutoCloseable {

  public static final int MAX_RETRIES = 5;

  private final AtomicInteger retries = new AtomicInteger(0);
  private final MonitorListener listener = new MonitorListener();

  private final Function<WatcherListener<T>, Watch> watcherCreator;
  private final Runnable giveUp;
  private boolean closeCalled = false;

  private Watch watcher;

  public WatcherMonitor(
      Function<WatcherListener<T>, Watch> watcherCreator,
      Runnable giveUp) {
    this.watcherCreator = watcherCreator;
    watcher = watcherCreator.apply(listener);
    this.giveUp = giveUp;
  }

  private void onEventReceived() {
    retries.set(0);
  }

  private void onWatcherClosed() {
    if (closeCalled) {
      return;
    }
    int currentRetries = retries.addAndGet(1);
    if (currentRetries >= MAX_RETRIES) {
      giveUp.run();
    } else {
      try {
        watcher = watcherCreator.apply(listener);
      } catch (Exception ignored) {
        onWatcherClosed();
      }
    }
  }

  @Override
  public void close() {
    closeCalled = true;
    watcher.close();
  }

  private class MonitorListener implements WatcherListener<T> {

    @Override
    public void eventReceived(Watcher.Action action, T resource) {
      onEventReceived();
    }

    @Override
    public void watcherClosed(Exception ex) {
      onWatcherClosed();
    }
  }
}
