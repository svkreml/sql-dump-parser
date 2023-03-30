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
import java.text.ParseException;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlParseException extends ParseException {

    public SqlParseException(String message, CharBuffer charBuffer) {
        this(message, charBuffer.subSequence(charBuffer.position(), Math.min(charBuffer.position() + 40, charBuffer.length())), charBuffer.position());
    }

    public SqlParseException(String message, CharSequence s, int errorOffset) {
        super(message + " (" + s.toString() + ')', errorOffset);
    }

}
