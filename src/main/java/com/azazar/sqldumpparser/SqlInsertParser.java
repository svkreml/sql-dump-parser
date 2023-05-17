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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author ChatGPT-4
 * @author Mikhail Yevchenko <spam@uo1.net>
 * @since  May 17, 2023
 */
public class SqlInsertParser {

    private static Consumer<SqlStatement> createConsumer(Set<String> tableNames, SqlInsertParseCallback callback) {
        return stmt -> {
            if (stmt.getCommand().toString().equalsIgnoreCase("INSERT")) {
                try {
                    processInsertStatement(stmt, tableNames, callback);
                }
                catch (SqlInsertParseException e) {
                    throw e.wrap();
                }
            }
        };
    }

    public static void parse(Reader reader, Set<String> tableNames, SqlInsertParseCallback callback) throws SqlInsertParseException, SqlParseException, IOException {
        try {
            new SqlParser().parse(reader, createConsumer(tableNames, callback));
        }
        catch (SqlInsertParseException.WrappedSqlInsertParseException ex) {
            throw ex.getCause();
        }
    }

    public static void parse(CharSequence str, Set<String> tableNames, SqlInsertParseCallback callback) throws SqlInsertParseException, SqlParseException, IOException {
        try {
            new SqlParser().parse(str, createConsumer(tableNames, callback));
        }
        catch (SqlInsertParseException.WrappedSqlInsertParseException ex) {
            throw ex.getCause();
        }
    }

    private static void extractTupleValues(SqlTokenGroup group, List<SqlValue> values) throws SqlInsertParseException {
        var tokens = group.getTokens();

        var openingDelimiter = tokens.get(0);
        var closingDelimiter = tokens.get(tokens.size() - 1);

        if (!(SqlDelimiter.LEFT_PARENTHESES.equals(openingDelimiter) && SqlDelimiter.RIGHT_PARENTHESES.equals(closingDelimiter))) {
            throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad opening or closing delimiter");
        }

        for(int i = 1; i < tokens.size(); i += 2) {
            if (i > 1 && !SqlDelimiter.COMMA.equals(tokens.get(i - 1))) {
                throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad delimiter at index #" + i);
            }
            
            if (tokens.get(i) instanceof SqlValue value) {
                values.add(value);
            }
            else {
                throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad value at index #" + i);
            }
        }
    }

    private static void extractTupleIdentifiers(SqlTokenGroup group, List<String> idents) throws SqlInsertParseException {
        var tokens = group.getTokens();

        var openingDelimiter = tokens.get(0);
        var closingDelimiter = tokens.get(tokens.size() - 1);

        if (!(SqlDelimiter.LEFT_PARENTHESES.equals(openingDelimiter) && SqlDelimiter.RIGHT_PARENTHESES.equals(closingDelimiter))) {
            throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad opening or closing delimiter");
        }

        for(int i = 1; i < tokens.size(); i += 2) {
            if (i > 1 && !SqlDelimiter.COMMA.equals(tokens.get(i - 1))) {
                throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad delimiter at index #" + i);
            }
            
            if (tokens.get(i) instanceof SqlIdentifier ident) {
                idents.add(ident.getId());
            }
            else {
                throw new SqlInsertParseException("Failed to parse tuple (" + group + "), bad value at index #" + i);
            }
        }
    }

    private static void processInsertStatement(SqlStatement stmt, Set<String> tableNames, SqlInsertParseCallback callback) throws SqlInsertParseException {
        List<SqlToken> tokens = stmt.getTokens();
        
        if (tokens.size() < 6) {
            throw new SqlInsertParseException("Statement is too short", stmt);
        }

        if (!(tokens.get(1).toString().equalsIgnoreCase("INTO"))) {
            throw new SqlInsertParseException("\"INSERT\" is not followed by \"INTO\"", stmt);
        }
        
        String tableName = ((SqlIdentifier) tokens.get(2)).getId();

        if (!tableNames.contains(tableName)) {
            return; // Table should be ignored
        }

        SqlToken shouldBeGroup = tokens.get(3);

        if (!(shouldBeGroup instanceof SqlTokenGroup)) {
            throw new SqlInsertParseException("\"INSERT\" statement doesn't include column names", stmt);
        }

        SqlTokenGroup group = (SqlTokenGroup)shouldBeGroup;

        if (!SqlDelimiter.LEFT_PARENTHESES.equals(group.getTokens().get(0))) {
            throw new SqlInsertParseException("\"INSERT\" statement doesn't include column names", stmt);
        }
        
        List<String> columnNames = new ArrayList<>();
        extractTupleIdentifiers(group, columnNames);
        
        if (!SqlReservedKeyword.VALUES.equals(tokens.get(4))) {
            throw new SqlInsertParseException("\"INSERT\" statement doesn't include \"VALUES\" keywords", stmt);
        }

        var rowValues = new LinkedHashMap<String, SqlValue>(columnNames.size());
        var rowValuesArr = new ArrayList<SqlValue>(columnNames.size());

        for(int i = 5; i < tokens.size(); i+= 2) {
            if (i > 5 && !SqlDelimiter.COMMA.equals(tokens.get(i - 1))) {
                throw new SqlInsertParseException("Delimiter expected, \"" + tokens.get(i - 1) + "\" found", stmt);
            }

            if (tokens.get(i) instanceof SqlTokenGroup tg) {
                rowValues.clear();
                rowValuesArr.clear();

                extractTupleValues(tg, rowValuesArr);

                for(int j = 0; j < columnNames.size(); j++) {
                    rowValues.put(columnNames.get(j), rowValuesArr.get(j));
                }
                callback.onInsert(tableName, rowValues);
            }
            else {
                throw new SqlInsertParseException("Values expected where \"" + tokens.get(i) + "\" found", stmt);
            }
        }
    }
}
