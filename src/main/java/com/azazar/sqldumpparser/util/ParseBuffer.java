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

import java.io.Reader;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public interface ParseBuffer extends CharSequence {
    
    default char getAdvance() {
        char c = charAt(0);
        
        advance();
        
        return c;
    }
    
    int position();

    default void advance() {
        advance(1);
    }

    void advance(int offset);

    static ParseBuffer wrap(CharSequence s) {
        return new CharSequenceParseBuffer(s);
    }

    static ParseBuffer wrap(CharStreamBuffer s) {
        return new CharStreamParseBuffer(s);
    }
    
    static ParseBuffer wrap(Reader r) {
        return new CharStreamParseBuffer(new CharStreamBuffer(r));
    }
    
}
