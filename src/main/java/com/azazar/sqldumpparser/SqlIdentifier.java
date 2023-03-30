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

import java.util.Objects;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlIdentifier implements SqlToken {
    
    private String identifier;

    public SqlIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.identifier);
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
        final SqlIdentifier other = (SqlIdentifier) obj;
        return Objects.equals(this.identifier, other.identifier);
    }

    public String getId() {
        return identifier;
    }

    @Override
    public String toString() {
        if (SqlReservedKeyword.isKeyword(identifier) || identifier.contains(" ")) {
            return '`' + identifier + '`';
        }

        return identifier;
    }

}
