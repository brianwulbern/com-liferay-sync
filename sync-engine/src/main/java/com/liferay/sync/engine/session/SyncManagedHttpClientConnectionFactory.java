/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.engine.session;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;

/**
 * @author Jonathan McCann
 */
public class SyncManagedHttpClientConnectionFactory
	extends ManagedHttpClientConnectionFactory {

	@Override
	public ManagedHttpClientConnection create(
		HttpRoute route, ConnectionConfig connectionConfig) {

		if (connectionConfig == null) {
			connectionConfig = ConnectionConfig.DEFAULT;
		}

		CodingErrorAction malformedInputAction =
			connectionConfig.getMalformedInputAction();

		if (malformedInputAction == null) {
			malformedInputAction = CodingErrorAction.REPORT;
		}

		CodingErrorAction unmappableInputAction =
			connectionConfig.getUnmappableInputAction();

		if (unmappableInputAction == null) {
			unmappableInputAction = CodingErrorAction.REPORT;
		}

		CharsetDecoder charsetDecoder = null;
		CharsetEncoder charsetEncoder = null;

		Charset charset = connectionConfig.getCharset();

		if (charset != null) {
			charsetDecoder = charset.newDecoder();
			charsetDecoder.onMalformedInput(malformedInputAction);
			charsetDecoder.onUnmappableCharacter(unmappableInputAction);
			charsetEncoder = charset.newEncoder();
			charsetEncoder.onMalformedInput(malformedInputAction);
			charsetEncoder.onUnmappableCharacter(unmappableInputAction);
		}

		final String id =
			"http-outgoing-" + Long.toString(_counter.getAndIncrement());

		return new SyncManagedHttpClientConnection(
			id, connectionConfig.getBufferSize(),
			connectionConfig.getFragmentSizeHint(), charsetDecoder,
			charsetEncoder, connectionConfig.getMessageConstraints(), null,
			null, null, null);
	}

	private final AtomicLong _counter = new AtomicLong();

}