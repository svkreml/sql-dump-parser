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

import org.junit.jupiter.api.Test;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CharStreamBufferTest {

    private final String input = "This is a sample text for testing purposes.";

    @Test
    void testDiscardBufferedData() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));
        buffer.ensureBufferedTo(input.length());

        buffer.discardBufferedData(20);
        assertEquals(20, buffer.getBufferOffset());

        String str = buffer.toString();

        assertEquals(input.substring(20), str.substring(20));
    }

    @Test
    void testEnsureBufferedTo() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));

        assertTrue(buffer.ensureBufferedTo(input.length()));
        assertEquals(input.length(), buffer.getBuffered());
    }

    @Test
    void testLength() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));
        buffer.ensureBufferedTo(input.length());

        assertTrue(buffer.length() >= input.length());
    }

    @Test
    void testCharAt() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));
        buffer.ensureBufferedTo(input.length());

        for (int i = 0; i < input.length(); i++) {
            assertEquals(input.charAt(i), buffer.charAt(i));
        }
    }

    @Test
    void testSubSequence() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));
        buffer.ensureBufferedTo(input.length());

        CharSequence subSequence = buffer.subSequence(5, 10);
        assertEquals("is a ", subSequence.toString());
    }

    @Test
    void testBuffering() {
        CharStreamBuffer.bufferSize = 32;
        CharStreamBuffer.bufferStep = 16;
        CharStreamBuffer buffer = new CharStreamBuffer(new StringReader(input));

        // Check initial buffering
        assertTrue(buffer.ensureBufferedTo(16));
        assertEquals(16, buffer.getBuffered());

        // Check subsequent buffering
        assertTrue(buffer.ensureBufferedTo(32));
        assertEquals(32, buffer.getBuffered());

        // Check buffering beyond the initial buffer size
        assertEquals(input.length() >= 48, buffer.ensureBufferedTo(48));
        assertEquals(input.length(), buffer.getBuffered());
    }
}