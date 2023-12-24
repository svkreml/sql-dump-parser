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

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class ParseBufferUtils {

    public static final int INDEX_NOT_FOUND = -1;

    public static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }

        if (searchSeq.length() == 0) {
            return startPos;
        }

        if (seq.length() < searchSeq.length()) {
            return INDEX_NOT_FOUND;
        }

        char startChar = searchSeq.charAt(0);

        mainLoop:
        for(int i = startPos, max = seq.length() - searchSeq.length(); i <= max; i++) {
            if (seq.charAt(i) != startChar) {
                continue;
            }

            for(int j = 1; j < searchSeq.length(); j++) {
                if (seq.charAt(i + j) != searchSeq.charAt(j)) {
                    continue mainLoop;
                }
            }

            return i;
        }

        return INDEX_NOT_FOUND;
    }

}
