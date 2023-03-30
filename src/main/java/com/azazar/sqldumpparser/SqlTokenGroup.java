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

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlTokenGroup implements SqlToken {

    private List<SqlToken> tokens;

    public SqlTokenGroup(List<SqlToken> tokens) {
        this.tokens = tokens;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.tokens);
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
        final SqlTokenGroup other = (SqlTokenGroup) obj;
        return Objects.equals(this.tokens, other.tokens);
    }

    public List<SqlToken> getTokens() {
        return tokens;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        
        for (SqlToken token : tokens) {
            if (b.length() > 0)
                b.append(' ');
            
            b.append(token);
        }
        
        return b.toString();
    }

}
