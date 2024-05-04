/*
 * Copyright (C) 2018-2024 Velocity Contributors
 *
 * The Velocity API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the api top-level directory.
 */

package com.velocitypowered.api.event.connection;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.network.HandshakeIntent;
import com.velocitypowered.api.proxy.InboundConnection;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This event is fired when a handshake is established between a client and the proxy.
 * Velocity will fire this event asynchronously and will not wait for it to complete before
 * handling the connection.
 */
public final class ConnectionHandshakeEvent implements ResultedEvent<ConnectionHandshakeEvent.ConnectionHandshakeComponentResult> {

  private final InboundConnection connection;
  private final HandshakeIntent intent;
  private ConnectionHandshakeComponentResult result;

  /**
   * Creates a new instance.
   *
   * @param connection the connection logging into the proxy
   * @param intent the intent of the handshake
   */
  public ConnectionHandshakeEvent(InboundConnection connection, HandshakeIntent intent) {
    this.connection = Preconditions.checkNotNull(connection, "connection");
    this.intent = Preconditions.checkNotNull(intent, "intent");
    this.result = ConnectionHandshakeComponentResult.allowed();
  }

  /**
   * This method is only retained to avoid breaking plugins
   * that have not yet updated their integration tests.
   *
   * @param connection the inbound connection
   * @deprecated use {@link #ConnectionHandshakeEvent(InboundConnection, HandshakeIntent)}
   */
  @Deprecated(forRemoval = true)
  public ConnectionHandshakeEvent(InboundConnection connection) {
    this.connection = Preconditions.checkNotNull(connection, "connection");
    this.intent = HandshakeIntent.LOGIN;
    this.result = ConnectionHandshakeComponentResult.allowed();
  }

  public InboundConnection getConnection() {
    return connection;
  }

  public HandshakeIntent getIntent() {
    return this.intent;
  }

  @Override
  public ConnectionHandshakeComponentResult getResult() {
    return result;
  }

  @Override
  public void setResult(final @NonNull ConnectionHandshakeComponentResult result) {
    this.result = Preconditions.checkNotNull(result, "result");
  }

  @Override
  public String toString() {
    return "ConnectionHandshakeEvent{"
        + "connection=" + connection
        + ", intent=" + intent
        + ", result=" + result
        + '}';
  }

  /**
   * Represents an "allowed/allowed with forced online\offline mode/denied" result with a reason
   * allowed for denial.
   */
  public static final class ConnectionHandshakeComponentResult implements ResultedEvent.Result {

    private static final ConnectionHandshakeComponentResult ALLOWED = new ConnectionHandshakeComponentResult(
        ConnectionHandshakeComponentResult.Result.ALLOWED, null);

    private final ConnectionHandshakeComponentResult.Result result;
    private final net.kyori.adventure.text.Component reason;

    private ConnectionHandshakeComponentResult(ConnectionHandshakeComponentResult.Result result,
                                               net.kyori.adventure.text.@Nullable Component reason) {
      this.result = result;
      this.reason = reason;
    }

    @Override
    public boolean isAllowed() {
      return result != ConnectionHandshakeComponentResult.Result.DISALLOWED;
    }

    public Optional<Component> getReasonComponent() {
      return Optional.ofNullable(reason);
    }

    @Override
    public String toString() {
      return result == Result.ALLOWED ? "allowed" : "denied";
    }

    /**
     * Returns a result indicating the connection will be allowed through the proxy.
     *
     * @return the allowed result
     */
    public static ConnectionHandshakeComponentResult allowed() {
      return ALLOWED;
    }

    /**
     * Denies the login with the specified reason.
     *
     * @param reason the reason for disallowing the connection
     * @return a new result
     */
    public static ConnectionHandshakeComponentResult denied(net.kyori.adventure.text.Component reason) {
      Preconditions.checkNotNull(reason, "reason");
      return new ConnectionHandshakeComponentResult(ConnectionHandshakeComponentResult.Result.DISALLOWED, reason);
    }

    private enum Result {
      ALLOWED,
      DISALLOWED
    }
  }
}
