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
package com.azazar.sqldumpparser;

import java.nio.CharBuffer;
import java.util.Locale;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlUtil {
    
    private static final String LATIN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LATIN_CHARS_LC = LATIN_CHARS.toLowerCase(Locale.ENGLISH);
    private static final String NUMERIC_CHARS_S = "0123456789";

    static final char SPLITTER = ';';
    static final char[] WHITESPACE_CHARS = " \t\r\n".toCharArray();
    static final char[] KEYWORD_CHARS = (LATIN_CHARS + LATIN_CHARS_LC + NUMERIC_CHARS_S + "_").toCharArray();
    static final char[] NUMERIC_CHARS = NUMERIC_CHARS_S.toCharArray();
    
    public static boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    public static boolean isKeywordFirst(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }
    
    public static boolean isKeyword(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_';
    }

    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    public static void advance(CharBuffer buf) {
        buf.get();
    }
    
    public static void advance(CharBuffer buf, int offset) {
        buf.position(buf.position() + offset);
    }
    
}
