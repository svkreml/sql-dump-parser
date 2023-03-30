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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlReservedKeyword implements SqlToken {

    private static final LinkedHashMap<String, SqlReservedKeyword> instances = new LinkedHashMap<>();
    
    public static final SqlReservedKeyword ABORT = new SqlReservedKeyword("ABORT");
    public static final SqlReservedKeyword DECIMAL = new SqlReservedKeyword("DECIMAL");
    public static final SqlReservedKeyword INTERVAL = new SqlReservedKeyword("INTERVAL");
    public static final SqlReservedKeyword PRESERVE = new SqlReservedKeyword("PRESERVE");
    public static final SqlReservedKeyword ALL = new SqlReservedKeyword("ALL");
    public static final SqlReservedKeyword DECODE = new SqlReservedKeyword("DECODE");
    public static final SqlReservedKeyword INTO = new SqlReservedKeyword("INTO");
    public static final SqlReservedKeyword PRIMARY = new SqlReservedKeyword("PRIMARY");
    public static final SqlReservedKeyword ALLOCATE = new SqlReservedKeyword("ALLOCATE");
    public static final SqlReservedKeyword DEFAULT = new SqlReservedKeyword("DEFAULT");
    public static final SqlReservedKeyword LEADING = new SqlReservedKeyword("LEADING");
    public static final SqlReservedKeyword RESET = new SqlReservedKeyword("RESET");
    public static final SqlReservedKeyword ANALYSE = new SqlReservedKeyword("ANALYSE");
    public static final SqlReservedKeyword DESC = new SqlReservedKeyword("DESC");
    public static final SqlReservedKeyword LEFT = new SqlReservedKeyword("LEFT");
    public static final SqlReservedKeyword REUSE = new SqlReservedKeyword("REUSE");
    public static final SqlReservedKeyword ANALYZE = new SqlReservedKeyword("ANALYZE");
    public static final SqlReservedKeyword DISTINCT = new SqlReservedKeyword("DISTINCT");
    public static final SqlReservedKeyword LIKE = new SqlReservedKeyword("LIKE");
    public static final SqlReservedKeyword RIGHT = new SqlReservedKeyword("RIGHT");
    public static final SqlReservedKeyword AND = new SqlReservedKeyword("AND");
    public static final SqlReservedKeyword DISTRIBUTE = new SqlReservedKeyword("DISTRIBUTE");
    public static final SqlReservedKeyword LIMIT = new SqlReservedKeyword("LIMIT");
    public static final SqlReservedKeyword ROWS = new SqlReservedKeyword("ROWS");
    public static final SqlReservedKeyword ANY = new SqlReservedKeyword("ANY");
    public static final SqlReservedKeyword DO = new SqlReservedKeyword("DO");
    public static final SqlReservedKeyword LOAD = new SqlReservedKeyword("LOAD");
    public static final SqlReservedKeyword SELECT = new SqlReservedKeyword("SELECT");
    public static final SqlReservedKeyword AS = new SqlReservedKeyword("AS");
    public static final SqlReservedKeyword ELSE = new SqlReservedKeyword("ELSE");
    public static final SqlReservedKeyword LOCAL = new SqlReservedKeyword("LOCAL");
    public static final SqlReservedKeyword SESSION_USER = new SqlReservedKeyword("SESSION_USER");
    public static final SqlReservedKeyword ASC = new SqlReservedKeyword("ASC");
    public static final SqlReservedKeyword END = new SqlReservedKeyword("END");
    public static final SqlReservedKeyword LOCK = new SqlReservedKeyword("LOCK");
    public static final SqlReservedKeyword SETOF = new SqlReservedKeyword("SETOF");
    public static final SqlReservedKeyword BETWEEN = new SqlReservedKeyword("BETWEEN");
    public static final SqlReservedKeyword EXCEPT = new SqlReservedKeyword("EXCEPT");
    public static final SqlReservedKeyword MINUS = new SqlReservedKeyword("MINUS");
    public static final SqlReservedKeyword SHOW = new SqlReservedKeyword("SHOW");
    public static final SqlReservedKeyword BINARY = new SqlReservedKeyword("BINARY");
    public static final SqlReservedKeyword EXCLUDE = new SqlReservedKeyword("EXCLUDE");
    public static final SqlReservedKeyword MOVE = new SqlReservedKeyword("MOVE");
    public static final SqlReservedKeyword SOME = new SqlReservedKeyword("SOME");
    public static final SqlReservedKeyword BIT = new SqlReservedKeyword("BIT");
    public static final SqlReservedKeyword EXISTS = new SqlReservedKeyword("EXISTS");
    public static final SqlReservedKeyword NATURAL = new SqlReservedKeyword("NATURAL");
    public static final SqlReservedKeyword TABLE = new SqlReservedKeyword("TABLE");
    public static final SqlReservedKeyword BOTH = new SqlReservedKeyword("BOTH");
    public static final SqlReservedKeyword EXPLAIN = new SqlReservedKeyword("EXPLAIN");
    public static final SqlReservedKeyword NCHAR = new SqlReservedKeyword("NCHAR");
    public static final SqlReservedKeyword THEN = new SqlReservedKeyword("THEN");
    public static final SqlReservedKeyword CASE = new SqlReservedKeyword("CASE");
    public static final SqlReservedKeyword EXPRESS = new SqlReservedKeyword("EXPRESS");
    public static final SqlReservedKeyword NEW = new SqlReservedKeyword("NEW");
    public static final SqlReservedKeyword TIES = new SqlReservedKeyword("TIES");
    public static final SqlReservedKeyword CAST = new SqlReservedKeyword("CAST");
    public static final SqlReservedKeyword EXTEND = new SqlReservedKeyword("EXTEND");
    public static final SqlReservedKeyword NOT = new SqlReservedKeyword("NOT");
    public static final SqlReservedKeyword TIME = new SqlReservedKeyword("TIME");
    public static final SqlReservedKeyword CHAR = new SqlReservedKeyword("CHAR");
    public static final SqlReservedKeyword EXTERNAL = new SqlReservedKeyword("EXTERNAL");
    public static final SqlReservedKeyword NOTNULL = new SqlReservedKeyword("NOTNULL");
    public static final SqlReservedKeyword TIMESTAMP = new SqlReservedKeyword("TIMESTAMP");
    public static final SqlReservedKeyword CHARACTER = new SqlReservedKeyword("CHARACTER");
    public static final SqlReservedKeyword EXTRACT = new SqlReservedKeyword("EXTRACT");
    public static final SqlReservedKeyword NULL = new SqlReservedKeyword("NULL");
    public static final SqlReservedKeyword TO = new SqlReservedKeyword("TO");
    public static final SqlReservedKeyword CHECK = new SqlReservedKeyword("CHECK");
    public static final SqlReservedKeyword FALSE = new SqlReservedKeyword("FALSE");
    public static final SqlReservedKeyword NULLS = new SqlReservedKeyword("NULLS");
    public static final SqlReservedKeyword TRAILING = new SqlReservedKeyword("TRAILING");
    public static final SqlReservedKeyword CLUSTER = new SqlReservedKeyword("CLUSTER");
    public static final SqlReservedKeyword FIRST = new SqlReservedKeyword("FIRST");
    public static final SqlReservedKeyword NUMERIC = new SqlReservedKeyword("NUMERIC");
    public static final SqlReservedKeyword TRANSACTION = new SqlReservedKeyword("TRANSACTION");
    public static final SqlReservedKeyword COALESCE = new SqlReservedKeyword("COALESCE");
    public static final SqlReservedKeyword FLOAT = new SqlReservedKeyword("FLOAT");
    public static final SqlReservedKeyword NVL = new SqlReservedKeyword("NVL");
    public static final SqlReservedKeyword TRIGGER = new SqlReservedKeyword("TRIGGER");
    public static final SqlReservedKeyword COLLATE = new SqlReservedKeyword("COLLATE");
    public static final SqlReservedKeyword FOLLOWING = new SqlReservedKeyword("FOLLOWING");
    public static final SqlReservedKeyword NVL2 = new SqlReservedKeyword("NVL2");
    public static final SqlReservedKeyword TRIM = new SqlReservedKeyword("TRIM");
    public static final SqlReservedKeyword COLLATION = new SqlReservedKeyword("COLLATION");
    public static final SqlReservedKeyword FOR = new SqlReservedKeyword("FOR");
    public static final SqlReservedKeyword OFF = new SqlReservedKeyword("OFF");
    public static final SqlReservedKeyword TRUE = new SqlReservedKeyword("TRUE");
    public static final SqlReservedKeyword COLUMN = new SqlReservedKeyword("COLUMN");
    public static final SqlReservedKeyword FOREIGN = new SqlReservedKeyword("FOREIGN");
    public static final SqlReservedKeyword OFFSET = new SqlReservedKeyword("OFFSET");
    public static final SqlReservedKeyword UNBOUNDED = new SqlReservedKeyword("UNBOUNDED");
    public static final SqlReservedKeyword CONSTRAINT = new SqlReservedKeyword("CONSTRAINT");
    public static final SqlReservedKeyword FROM = new SqlReservedKeyword("FROM");
    public static final SqlReservedKeyword OLD = new SqlReservedKeyword("OLD");
    public static final SqlReservedKeyword UNION = new SqlReservedKeyword("UNION");
    public static final SqlReservedKeyword COPY = new SqlReservedKeyword("COPY");
    public static final SqlReservedKeyword FULL = new SqlReservedKeyword("FULL");
    public static final SqlReservedKeyword ON = new SqlReservedKeyword("ON");
    public static final SqlReservedKeyword UNIQUE = new SqlReservedKeyword("UNIQUE");
    public static final SqlReservedKeyword CROSS = new SqlReservedKeyword("CROSS");
    public static final SqlReservedKeyword FUNCTION = new SqlReservedKeyword("FUNCTION");
    public static final SqlReservedKeyword ONLINE = new SqlReservedKeyword("ONLINE");
    public static final SqlReservedKeyword USER = new SqlReservedKeyword("USER");
    public static final SqlReservedKeyword CURRENT = new SqlReservedKeyword("CURRENT");
    public static final SqlReservedKeyword GENSTATS = new SqlReservedKeyword("GENSTATS");
    public static final SqlReservedKeyword ONLY = new SqlReservedKeyword("ONLY");
    public static final SqlReservedKeyword USING = new SqlReservedKeyword("USING");
    public static final SqlReservedKeyword CURRENT_CATALOG = new SqlReservedKeyword("CURRENT_CATALOG");
    public static final SqlReservedKeyword GLOBAL = new SqlReservedKeyword("GLOBAL");
    public static final SqlReservedKeyword OR = new SqlReservedKeyword("OR");
    public static final SqlReservedKeyword VACUUM = new SqlReservedKeyword("VACUUM");
    public static final SqlReservedKeyword CURRENT_DATE = new SqlReservedKeyword("CURRENT_DATE");
    public static final SqlReservedKeyword GROUP = new SqlReservedKeyword("GROUP");
    public static final SqlReservedKeyword ORDER = new SqlReservedKeyword("ORDER");
    public static final SqlReservedKeyword VARCHAR = new SqlReservedKeyword("VARCHAR");
    public static final SqlReservedKeyword CURRENT_DB = new SqlReservedKeyword("CURRENT_DB");
    public static final SqlReservedKeyword HAVING = new SqlReservedKeyword("HAVING");
    public static final SqlReservedKeyword OTHERS = new SqlReservedKeyword("OTHERS");
    public static final SqlReservedKeyword VERBOSE = new SqlReservedKeyword("VERBOSE");
    public static final SqlReservedKeyword CURRENT_SCHEMA = new SqlReservedKeyword("CURRENT_SCHEMA");
    public static final SqlReservedKeyword IDENTIFIER_CASE = new SqlReservedKeyword("IDENTIFIER_CASE");
    public static final SqlReservedKeyword OUT = new SqlReservedKeyword("OUT");
    public static final SqlReservedKeyword VERSION = new SqlReservedKeyword("VERSION");
    public static final SqlReservedKeyword CURRENT_SID = new SqlReservedKeyword("CURRENT_SID");
    public static final SqlReservedKeyword ILIKE = new SqlReservedKeyword("ILIKE");
    public static final SqlReservedKeyword OUTER = new SqlReservedKeyword("OUTER");
    public static final SqlReservedKeyword VIEW = new SqlReservedKeyword("VIEW");
    public static final SqlReservedKeyword CURRENT_TIME = new SqlReservedKeyword("CURRENT_TIME");
    public static final SqlReservedKeyword IN = new SqlReservedKeyword("IN");
    public static final SqlReservedKeyword OVER = new SqlReservedKeyword("OVER");
    public static final SqlReservedKeyword WHEN = new SqlReservedKeyword("WHEN");
    public static final SqlReservedKeyword CURRENT_TIMESTAMP = new SqlReservedKeyword("CURRENT_TIMESTAMP");
    public static final SqlReservedKeyword INDEX = new SqlReservedKeyword("INDEX");
    public static final SqlReservedKeyword OVERLAPS = new SqlReservedKeyword("OVERLAPS");
    public static final SqlReservedKeyword WHERE = new SqlReservedKeyword("WHERE");
    public static final SqlReservedKeyword CURRENT_USER = new SqlReservedKeyword("CURRENT_USER");
    public static final SqlReservedKeyword INITIALLY = new SqlReservedKeyword("INITIALLY");
    public static final SqlReservedKeyword PARTITION = new SqlReservedKeyword("PARTITION");
    public static final SqlReservedKeyword WITH = new SqlReservedKeyword("WITH");
    public static final SqlReservedKeyword CURRENT_USERID = new SqlReservedKeyword("CURRENT_USERID");
    public static final SqlReservedKeyword INNER = new SqlReservedKeyword("INNER");
    public static final SqlReservedKeyword POSITION = new SqlReservedKeyword("POSITION");
    public static final SqlReservedKeyword WRITE = new SqlReservedKeyword("WRITE");
    public static final SqlReservedKeyword CURRENT_USEROID = new SqlReservedKeyword("CURRENT_USEROID");
    public static final SqlReservedKeyword INOUT = new SqlReservedKeyword("INOUT");
    public static final SqlReservedKeyword PRECEDING = new SqlReservedKeyword("PRECEDING");
    public static final SqlReservedKeyword DEALLOCATE = new SqlReservedKeyword("DEALLOCATE");
    public static final SqlReservedKeyword INTERSECT = new SqlReservedKeyword("INTERSECT");
    public static final SqlReservedKeyword PRECISION = new SqlReservedKeyword("PRECISION");
    public static final SqlReservedKeyword DEC = new SqlReservedKeyword("DEC");
    
    // Non-IBM
    public static final SqlReservedKeyword KEY = new SqlReservedKeyword("KEY");
    public static final SqlReservedKeyword AUTO_INCREMENT = new SqlReservedKeyword("AUTO_INCREMENT");
    
    // Data types
    
    public static final SqlReservedKeyword TINYINT = new SqlReservedKeyword("TINYINT");
    public static final SqlReservedKeyword SMALLINT = new SqlReservedKeyword("SMALLINT");
    public static final SqlReservedKeyword MEDIUMINT = new SqlReservedKeyword("MEDIUMINT");
    public static final SqlReservedKeyword INT = new SqlReservedKeyword("INT");
    public static final SqlReservedKeyword INTEGER = new SqlReservedKeyword("INTEGER");
    public static final SqlReservedKeyword BIGINT = new SqlReservedKeyword("BIGINT");
    public static final SqlReservedKeyword REAL = new SqlReservedKeyword("REAL");
    public static final SqlReservedKeyword DOUBLE = new SqlReservedKeyword("DOUBLE");
    //public static final SqlReservedKeyword FLOAT = new SqlReservedKeyword("FLOAT");
    public static final SqlReservedKeyword DATE = new SqlReservedKeyword("DATE");
    public static final SqlReservedKeyword DATETIME = new SqlReservedKeyword("DATETIME");
    //public static final SqlReservedKeyword TIME = new SqlReservedKeyword("TIME");
    public static final SqlReservedKeyword YEAR = new SqlReservedKeyword("YEAR");
    public static final SqlReservedKeyword TEXT = new SqlReservedKeyword("TEXT");
    public static final SqlReservedKeyword TINYTEXT = new SqlReservedKeyword("TINYTEXT");
    public static final SqlReservedKeyword MEDIUMTEXT = new SqlReservedKeyword("MEDIUMTEXT");
    public static final SqlReservedKeyword LONGTEXT = new SqlReservedKeyword("LONGTEXT");
    public static final SqlReservedKeyword BLOB = new SqlReservedKeyword("BLOB");
    public static final SqlReservedKeyword TINYBLOB = new SqlReservedKeyword("TINYBLOB");
    public static final SqlReservedKeyword MEDIUMBLOB = new SqlReservedKeyword("MEDIUMBLOB");
    public static final SqlReservedKeyword LONGBLOB = new SqlReservedKeyword("LONGBLOB");
    public static final SqlReservedKeyword ENUM = new SqlReservedKeyword("ENUM");
    public static final SqlReservedKeyword SET = new SqlReservedKeyword("SET");
    public static final SqlReservedKeyword VARBINARY = new SqlReservedKeyword("VARBINARY");
    //public static final SqlReservedKeyword BINARY = new SqlReservedKeyword("BINARY");

    // Additional keywords
    public static final SqlReservedKeyword ALTER = new SqlReservedKeyword("ALTER");
    public static final SqlReservedKeyword CREATE = new SqlReservedKeyword("CREATE");
    public static final SqlReservedKeyword DELETE = new SqlReservedKeyword("DELETE");
    public static final SqlReservedKeyword DROP = new SqlReservedKeyword("DROP");
    public static final SqlReservedKeyword GROUP_BY = new SqlReservedKeyword("GROUP_BY");
    //public static final SqlReservedKeyword HAVING = new SqlReservedKeyword("HAVING");
    public static final SqlReservedKeyword INSERT = new SqlReservedKeyword("INSERT");
    public static final SqlReservedKeyword UPDATE = new SqlReservedKeyword("UPDATE");
    public static final SqlReservedKeyword JOIN = new SqlReservedKeyword("JOIN");
    public static final SqlReservedKeyword INNER_JOIN = new SqlReservedKeyword("INNER_JOIN");
    public static final SqlReservedKeyword LEFT_JOIN = new SqlReservedKeyword("LEFT_JOIN");
    public static final SqlReservedKeyword RIGHT_JOIN = new SqlReservedKeyword("RIGHT_JOIN");
    public static final SqlReservedKeyword OUTER_JOIN = new SqlReservedKeyword("OUTER_JOIN");
    public static final SqlReservedKeyword VALUES = new SqlReservedKeyword("VALUES");
    
    
    public static boolean isKeyword(String s) {
        return instances.containsKey(s.toUpperCase(Locale.ENGLISH));
    }
    
    public static SqlReservedKeyword create(String s) {
        var sqlKw = instances.get(s.toUpperCase(Locale.ENGLISH));
        
        if (sqlKw == null) {
            throw new IllegalArgumentException(s);
        }
        
        return sqlKw;
    }
    
    public final String keyword;
    
    private SqlReservedKeyword(String keyword) {
        this.keyword = keyword;
        
        instances.put(keyword, this);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.keyword);
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
        final SqlReservedKeyword other = (SqlReservedKeyword) obj;
        return Objects.equals(this.keyword, other.keyword);
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }

}
