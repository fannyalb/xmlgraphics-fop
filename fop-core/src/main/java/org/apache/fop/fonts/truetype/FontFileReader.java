/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.fonts.truetype;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/**
 * Reads a TrueType font file into a byte array and
 * provides file like functions for array access.
 */
public class FontFileReader {

    private final int fsize; // file size
    private int current;    // current position in file
    private final byte[] file;

    /**
     * Constructor
     *
     * @param in InputStream to read from
     * @throws IOException In case of an I/O problem
     */
    public FontFileReader(InputStream in) throws IOException {
        this.file = IOUtils.toByteArray(in);
        this.fsize = this.file.length;
        this.current = 0;
    }


    /**
     * Set current file position to offset
     *
     * @param offset The new offset to set
     * @throws IOException In case of an I/O problem
     */
    public void seekSet(long offset) throws IOException {
        if (offset > fsize || offset < 0) {
            throw new java.io.EOFException("Reached EOF, file size=" + fsize
                                           + " offset=" + offset);
        }
        current = (int)offset;
    }

    /**
     * Skip a given number of bytes.
     *
     * @param add The number of bytes to advance
     * @throws IOException In case of an I/O problem
     */
    public void skip(long add) throws IOException {
        seekSet(current + add);
    }

    /**
     * Returns current file position.
     *
     * @return int The current position.
     */
    public int getCurrentPos() {
        return current;
    }

    /**
     * Returns the size of the file.
     *
     * @return int The filesize
     */
    public int getFileSize() {
        return fsize;
    }

    /**
     * Read 1 byte.
     *
     * @return One byte
     * @throws IOException If EOF is reached
     */
    private byte read() throws IOException {
        if (current >= fsize) {
            throw new java.io.EOFException("Reached EOF, file size=" + fsize);
        }

        final byte ret = file[current++];
        return ret;
    }

    /**
     * Read 1 signed byte.
     *
     * @return One byte
     * @throws IOException If EOF is reached
     */
    public final byte readTTFByte() throws IOException {
        return read();
    }

    /**
     * Read 1 unsigned byte.
     *
     * @return One unsigned byte
     * @throws IOException If EOF is reached
     */
    public final int readTTFUByte() throws IOException {
        final byte buf = read();

        if (buf < 0) {
            return (256 + buf);
        } else {
            return buf;
        }
    }

    /**
     * Read 2 bytes signed.
     *
     * @return One signed short
     * @throws IOException If EOF is reached
     */
    public final short readTTFShort() throws IOException {
        final int ret = (readTTFUByte() << 8) + readTTFUByte();
        final short sret = (short)ret;
        return sret;
    }

    /**
     * Read 2 bytes unsigned.
     *
     * @return One unsigned short
     * @throws IOException If EOF is reached
     */
    public final int readTTFUShort() throws IOException {
        final int ret = (readTTFUByte() << 8) + readTTFUByte();
        return ret;
    }

    /**
     * Write a USHort at a given position.
     *
     * @param pos The absolute position to write to
     * @param val The value to write
     * @throws IOException If EOF is reached
     */
    public final void writeTTFUShort(long pos, int val) throws IOException {
        if ((pos + 2) > fsize) {
            throw new java.io.EOFException("Reached EOF");
        }
        final byte b1 = (byte)((val >> 8) & 0xff);
        final byte b2 = (byte)(val & 0xff);
        final int fileIndex = (int) pos;
        file[fileIndex] = b1;
        file[fileIndex + 1] = b2;
    }

    /**
     * Read 2 bytes signed at position pos without changing current position.
     *
     * @param pos The absolute position to read from
     * @return One signed short
     * @throws IOException If EOF is reached
     */
    public final short readTTFShort(long pos) throws IOException {
        final long cp = getCurrentPos();
        seekSet(pos);
        final short ret = readTTFShort();
        seekSet(cp);
        return ret;
    }

    /**
     * Read 2 bytes unsigned at position pos without changing current position.
     *
     * @param pos The absolute position to read from
     * @return One unsigned short
     * @throws IOException If EOF is reached
     */
    public final int readTTFUShort(long pos) throws IOException {
        long cp = getCurrentPos();
        seekSet(pos);
        int ret = readTTFUShort();
        seekSet(cp);
        return ret;
    }

    /**
     * Read 4 bytes.
     *
     * @return One signed integer
     * @throws IOException If EOF is reached
     */
    public final int readTTFLong() throws IOException {
        long ret = readTTFUByte();    // << 8;
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();

        return (int)ret;
    }

    /**
     * Read 4 bytes.
     *
     * @return One unsigned integer
     * @throws IOException If EOF is reached
     */
    public final long readTTFULong() throws IOException {
        long ret = readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();
        ret = (ret << 8) + readTTFUByte();

        return ret;
    }

    /**
     * Read a NUL terminated ISO-8859-1 string.
     *
     * @return A String
     * @throws IOException If EOF is reached
     */
    public final String readTTFString() throws IOException {
        int i = current;
        while (file[i++] != 0) {
            if (i >= fsize) {
                throw new java.io.EOFException("Reached EOF, file size="
                                               + fsize);
            }
        }

        byte[] tmp = new byte[i - current - 1];
        System.arraycopy(file, current, tmp, 0, i - current - 1);
        return new String(tmp, "ISO-8859-1");
    }


    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len The length of the string to read
     * @return A String
     * @throws IOException If EOF is reached
     */
    public final String readTTFString(int len) throws IOException {
        if ((len + current) > fsize) {
            throw new java.io.EOFException("Reached EOF, file size=" + fsize);
        }

        byte[] tmp = new byte[len];
        System.arraycopy(file, current, tmp, 0, len);
        current += len;
        final String encoding;
        if ((tmp.length > 0) && (tmp[0] == 0)) {
            encoding = "UTF-16BE";
        } else {
            encoding = "ISO-8859-1";
        }
        return new String(tmp, encoding);
    }

    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len The length of the string to read
     * @param encodingID the string encoding id (presently ignored; always uses UTF-16BE)
     * @return A String
     * @throws IOException If EOF is reached
     */
    public final String readTTFString(int len, int encodingID) throws IOException {
        if ((len + current) > fsize) {
            throw new java.io.EOFException("Reached EOF, file size=" + fsize);
        }

        byte[] tmp = new byte[len];
        System.arraycopy(file, current, tmp, 0, len);
        current += len;
        //Use this for all known encoding IDs for now
        return new String(tmp, StandardCharsets.UTF_16BE);
    }

    /**
     * Return a copy of the internal array
     *
     * @param offset The absolute offset to start reading from
     * @param length The number of bytes to read
     * @return An array of bytes
     * @throws IOException if out of bounds
     */
    public byte[] getBytes(int offset,
                           int length) throws IOException {
        if ((offset + length) > fsize) {
            throw new java.io.IOException("Reached EOF");
        }

        byte[] ret = new byte[length];
        System.arraycopy(file, offset, ret, 0, length);
        return ret;
    }
    /**
     * Returns the full byte array representation of the file.
     * @return byte array.
     */
    public byte[] getAllBytes() {
        return file;
    }
}
