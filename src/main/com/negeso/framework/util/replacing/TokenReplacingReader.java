/*
 * @(#)$Id: $
 *
 * Copyright (c) 2010 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.framework.util.replacing;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.CharBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @TODO
 * 
 * @author Mykola Lyhozhon
 * @version $Revision: $
 * 
 */
public class TokenReplacingReader extends Reader {

	protected PushbackReader pushbackReader = null;
	protected ITokenResolver tokenResolver = null;
	protected StringBuilder tokenNameBuffer = new StringBuilder();
	protected String tokenValue = null;
	protected int tokenValueIndex = 0;
	private char CHAR = '%';

	public TokenReplacingReader(Reader source, ITokenResolver resolver) {
		this.pushbackReader = new PushbackReader(source, 2);
		this.tokenResolver = resolver;
	}

	public int read(CharBuffer target) throws IOException {
		throw new RuntimeException("Operation Not Supported");
	}

	public int read() throws IOException {
		if (this.tokenValue != null) {
			if (this.tokenValueIndex < this.tokenValue.length()) {
				return this.tokenValue.charAt(this.tokenValueIndex++);
			}
			if (this.tokenValueIndex == this.tokenValue.length()) {
				this.tokenValue = null;
				this.tokenValueIndex = 0;
			}
		}

		int data = this.pushbackReader.read();
		if (data != CHAR)
			return data;

		this.tokenNameBuffer.delete(0, this.tokenNameBuffer.length());
		this.tokenNameBuffer.append(CHAR);

		data = this.pushbackReader.read();
		while (data != CHAR && data != -1) {
			this.tokenNameBuffer.append((char) data);
			data = this.pushbackReader.read();
		}
		
		if (data == CHAR) {
			this.tokenNameBuffer.append(CHAR);
		}

		this.tokenValue = this.tokenResolver.resolveToken(this.tokenNameBuffer
				.toString());

		if (this.tokenValue == null) {
			if (data == CHAR) {
				this.pushbackReader.unread(data);
				this.tokenNameBuffer.deleteCharAt(this.tokenNameBuffer.length() - 1);
			}
			this.tokenValue = this.tokenNameBuffer.toString();
		} else {
			if (tokenValue.indexOf(CHAR) != -1) {
				this.tokenValue = StringUtils.replaceEach(this.tokenValue.toUpperCase(), tokenResolver.getKeys(), tokenResolver.getValues());
			}
		}
		return this.tokenValue.charAt(this.tokenValueIndex++);

	}

	public int read(char cbuf[]) throws IOException {
		return read(cbuf, 0, cbuf.length);
	}

	public int read(char cbuf[], int off, int len) throws IOException {
		int charsRead = 0;
		for (int i = 0; i < len; i++) {
			charsRead = i;
			int nextChar = read();
			if (nextChar == -1) {
				break;
			}
			cbuf[off + i] = (char) nextChar;
		}
		return charsRead;
	}

	public void close() throws IOException {
		this.pushbackReader.close();
	}

	public long skip(long n) throws IOException {
		throw new RuntimeException("Operation Not Supported");
	}

	public boolean ready() throws IOException {
		return this.pushbackReader.ready();
	}

	public boolean markSupported() {
		return false;
	}

	public void mark(int readAheadLimit) throws IOException {
		throw new RuntimeException("Operation Not Supported");
	}

	public void reset() throws IOException {
		throw new RuntimeException("Operation Not Supported");
	}
}
