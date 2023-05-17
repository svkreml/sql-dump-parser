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
public class SqlInteger implements SqlValue {
    
    public long value;

    public SqlInteger(long value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (int) (this.value ^ (this.value >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SqlInteger other = (SqlInteger) obj;
        return this.value == other.value;
    }

    public long getLong() {
        return value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
