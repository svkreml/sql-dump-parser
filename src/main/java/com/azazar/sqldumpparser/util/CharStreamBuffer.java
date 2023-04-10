/*
 * Copyright (C) 2023 Azazar <spam@azazar.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.azazar.sqldumpparser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.lang3.StringUtils;

/**
 * A utility class that buffers data from a Reader and provides access to the
 * buffered data using CharSequence methods.
 *
 * @author Azazar <spam@azazar.com>
 */
public class CharStreamBuffer implements CharSequence {

    /**
     * The size of the buffer used to store data read from the Reader.
     */
    static int bufferSize = 0x10000;

    /**
     * The number of characters to buffer in a single read operation when more
     * data is needed.
     */
    static int bufferStep = 0x08000;

    /**
     * A BufferedReader used to read data from the input Reader.
     */
    private BufferedReader reader;

    /**
     * A flag indicating whether the entire input has been read.
     */
    private boolean fullyRead = false;

    /**
     * The length of the data that was actually read into the buffer (including
     * discarded data).
     */
    private int bufferedLength = 0;

    /**
     * The buffer offset from the beginning of the stream. This represents the
     * length of the discarded data.
     */
    private int bufferOffset = 0;

    /**
     * The buffer used to store the characters read from the input Reader.
     */
    private char[] buffer = new char[bufferSize];

    /**
     * Creates a new CharStreamBuffer for the given Reader.
     *
     * @param reader The Reader to buffer data from.
     */
    public CharStreamBuffer(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Discards buffered data that won't be accessed later.
     *
     * @param position The position up to which the data can be discarded.
     */
    public void discardBufferedData(int position) {
        ensureBufferedTo(position); // Ensure the position is buffered before discarding data

        if (position <= bufferOffset + bufferStep) {
            return;
        }

        int newOffset = Math.min(position, bufferedLength);
        int lengthToKeep = bufferedLength - newOffset;
        System.arraycopy(buffer, newOffset - bufferOffset, buffer, 0, lengthToKeep);
        bufferOffset = newOffset;
    }
    
    /**
     * The buffer offset from the beginning of the stream. This represents the
     * length of the discarded data.
     * 
     * @return The buffer offset from the beginning of the stream
     */
    public int getBufferOffset() {
        return bufferOffset;
    }

    /**
     * Returns the number of buffered characters from the beginning of the stream.
     *
     * @return The number of buffered characters.
     */
    public int getBuffered() {
        return bufferedLength;
    }

    /**
     * Ensures that the specified position is buffered.
     *
     * @param position The position to be buffered.
     * @return true if the requested position is available in the buffer, false
     * otherwise.
     */
    public boolean ensureBufferedTo(int position) {
        int remaining = position - bufferedLength;
    
        if (remaining <= 0) {
            return true;
        }
    
        if (fullyRead) {
            return bufferedLength >= position;
        }
    
        try {
            while (remaining > 0) {
                // Calculate available space based on buffer capacity
                int availableSpace = buffer.length - (bufferedLength - bufferOffset);
    
                if (availableSpace < Math.min(remaining, bufferStep)) {
                    int newBufferSize = buffer.length * 2;
                    char[] newBuffer = new char[newBufferSize];
                    System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                    buffer = newBuffer;
                    availableSpace = buffer.length - (bufferedLength - bufferOffset);
                }
    
                int read = reader.read(buffer, bufferedLength - bufferOffset, Math.min(availableSpace, bufferStep));
    
                if (read == -1) {
                    fullyRead = true;
                    break;
                }
    
                bufferedLength += read;
                remaining -= read; // Update the remaining variable here
            }
        } catch (IOException e) {
            throw new IOExceptionWrapper(e);
        }
    
        return bufferedLength >= position - bufferOffset;
    }
    

    /**
     * Returns the available length of the buffered data.
     *
     * @return The available length of the buffered data.
     */
    @Override
    public int length() {
        if (fullyRead) {
            return bufferedLength;
        }

        return bufferedLength + Integer.MAX_VALUE / 2;
    }

    /**
     * Returns the character at the specified index.
     *
     * @param index The index of the character to return.
     * @return The character at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @Override
    public char charAt(int index) {
        if (index < bufferOffset) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index >= bufferedLength) {
            ensureBufferedTo(index + 1);
        }
        return buffer[index - bufferOffset];
    }

    /**
     * Returns a CharSequence that is a subsequence of this sequence starting
     * from {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param start The starting position of the subsequence.
     * @param end The ending position of the subsequence.
     * @return A new CharSequence that is a subsequence of this sequence.
     * @throws IndexOutOfBoundsException If start or end positions are invalid.
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < bufferOffset) {
            throw new IndexOutOfBoundsException("Start position is in discarded data");
        }
        
        if (start > end) {
            throw new IndexOutOfBoundsException("Invalid start and end positions");
        }
        
        ensureBufferedTo(end);
        
        return new BufferSubSequence(start - bufferOffset, end - start);
    }

    @Override
    public String toString() {
        ensureBufferedTo(length());

        if (bufferOffset == 0) {
            return subSequence(0, length()).toString();
        }

        return StringUtils.repeat('?', bufferOffset) + subSequence(bufferOffset, length()).toString();
    }

    /**
     * An inner class that represents a subsequence of the CharStreamBuffer
     * without copying data.
     */
    private class BufferSubSequence implements CharSequence {

        /**
         * The offset of the subsequence in the parent buffer.
         */
        private final int offset;

        /**
         * The length of the subsequence.
         */
        private final int length;

        /**
         * The buffer offset of the parent CharStreamBuffer when this subsequence was created.
         */
        private final int parentBufferOffsetAtCreation;

        /**
         * Creates a new BufferSubSequence with the specified offset and length.
         *
         * @param offset The starting position of the subsequence in the parent buffer.
         * @param length The length of the subsequence.
         */
        public BufferSubSequence(int offset, int length) {
            if (offset < 0 || length < 0) {
                throw new IllegalArgumentException("offset=" + offset + ", length=" + length);
            }

            this.offset = offset;
            this.length = length;
            this.parentBufferOffsetAtCreation = bufferOffset;
        }

        /**
         * Returns the length of the subsequence.
         *
         * @return The length of the subsequence.
         */
        @Override
        public int length() {
            checkParentBufferOffset();

            return length;
        }

        /**
         * Returns the character at the specified index within the subsequence.
         *
         * @param index The index of the character to return.
         * @return The character at the specified index.
         * @throws IndexOutOfBoundsException If the index is out of bounds.
         */
        @Override
        public char charAt(int index) {
            checkParentBufferOffset();
            if (index < 0 || index >= length) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            return buffer[offset + index];
        }

        /**
         * Returns a CharSequence that is a subsequence of this subsequence
         * starting from {@code start} (inclusive) to {@code end} (exclusive).
         *
         * @param start The starting position of the subsequence.
         * @param end The ending position of the subsequence.
         * @return A new CharSequence that is a subsequence of this subsequence.
         * @throws IndexOutOfBoundsException If start or end positions are
         * invalid.
         */
        @Override
        public CharSequence subSequence(int start, int end) {
            checkParentBufferOffset();
            if (start < 0 || end > length || start > end) {
                throw new IndexOutOfBoundsException("Invalid start and end positions");
            }
            return new BufferSubSequence(offset + start, end - start);
        }

        /**
         * Returns a string representation of the subsequence.
         *
         * @return A string containing the characters in the subsequence.
         */
        @Override
        public String toString() {
            checkParentBufferOffset();
            return new String(buffer, offset, length);
        }

        /**
         * Checks if the parent buffer offset has changed since the creation of this subsequence.
         *
         * @throws IllegalStateException If the parent buffer offset has changed.
         */
        private void checkParentBufferOffset() {
            if (parentBufferOffsetAtCreation != bufferOffset) {
                throw new IllegalStateException("Parent buffer offset has changed since the creation of this subsequence");
            }
        }
    }
}
