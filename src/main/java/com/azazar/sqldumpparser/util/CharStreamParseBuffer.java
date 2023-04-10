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

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public final class CharStreamParseBuffer extends CharSequenceParseBuffer {
    
    protected CharStreamBuffer buf;

    public CharStreamParseBuffer(CharStreamBuffer buf) {
        super(buf);
        this.buf = buf;
    }

    @Override
    public void advance(int offset) {
        super.advance(offset);
        buf.discardBufferedData(ofs);
    }

    @Override
    public void advance() {
        super.advance();
        buf.discardBufferedData(ofs);
    }

}
