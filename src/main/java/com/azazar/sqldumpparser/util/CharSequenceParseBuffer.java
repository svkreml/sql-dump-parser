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
public class CharSequenceParseBuffer implements ParseBuffer {
    
    protected CharSequence s;

    protected int ofs;

    public CharSequenceParseBuffer(CharSequence s) {
        this.s = s;
    }

    @Override
    public void advance() {
        ofs++;
    }

    @Override
    public void advance(int offset) {
        ofs += offset;
    }

    @Override
    public int length() {
        return s.length() - ofs;
    }

    @Override
    public char charAt(int index) {
        return s.charAt(index + ofs);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return s.subSequence(start + ofs, end + ofs);
    }

    @Override
    public int position() {
        return ofs;
    }

    @Override
    public String toString() {
        if (ofs == 0) {
            return s.toString();
        }

        return s.subSequence(ofs, s.length()).toString();
    }

}
