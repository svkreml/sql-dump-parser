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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        var tableFields = new HashMap<String, List<String>>();

        return stmt -> {
            switch (stmt.getCommand().toString().toUpperCase()) {
                case "CREATE":
                    if (stmt.getTokens().size() >= 4 && stmt.getTokens().get(1).toString().equalsIgnoreCase("TABLE")) {
                        var tableName = ((SqlIdentifier) stmt.getTokens().get(2)).getId();

                        if (tableNames.contains(tableName) && stmt.getTokens().get(3) instanceof SqlTokenGroup tableDef) {
                            try {
                                tableFields.put(tableName.toLowerCase(), parseCreateTableStatement(stmt, tableName, tableDef, callback));
                            }
                            catch (SqlInsertParseException e) {
                                throw e.wrap();
                            }
                        }
                    }
                    break;
                case "INSERT":
                    try {
                        processInsertStatement(stmt, tableNames, tableFields, callback);
                    }
                    catch (SqlInsertParseException e) {
                        throw e.wrap();
                    }
                    break;
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

    private static List<String> parseCreateTableStatement(SqlStatement stmt, String tableName, SqlTokenGroup tableDef, SqlInsertParseCallback callback) throws SqlInsertParseException {
        if (SqlDelimiter.LEFT_PARENTHESES != tableDef.getTokens().get(0)) {
            return null;
        }

        SqlToken previous, token = null;

        var fieldNames = new ArrayList<String>();

        for(int i = 1; i < tableDef.getTokens().size(); i++) {
            previous = token;
            token = tableDef.getTokens().get(i);

            if ((i == 1 || previous == SqlDelimiter.COMMA) && token instanceof SqlIdentifier ident) {
                fieldNames.add(ident.getId());
            }
        }

        callback.onCreateTable(tableName.toLowerCase(), fieldNames);

        return fieldNames;
    }

    private static void processInsertStatement(SqlStatement stmt, Set<String> tableNames, Map<String, List<String>> tablesFields, SqlInsertParseCallback callback) throws SqlInsertParseException {
        List<SqlToken> tokens = stmt.getTokens();
        
        if (tokens.size() < 5) {
            throw new SqlInsertParseException("Statement is too short", stmt);
        }

        if (!(tokens.get(1).toString().equalsIgnoreCase("INTO"))) {
            throw new SqlInsertParseException("\"INSERT\" is not followed by \"INTO\"", stmt);
        }
        
        String tableName = ((SqlIdentifier) tokens.get(2)).getId();

        if (!tableNames.contains(tableName)) {
            return; // Table should be ignored
        }

        SqlToken columnNamesGroupOrValues = tokens.get(3);

        int index;

        List<String> columnNames = null;

        if (columnNamesGroupOrValues instanceof SqlTokenGroup group) {
            if (!SqlDelimiter.LEFT_PARENTHESES.equals(group.getTokens().get(0))) {
                throw new SqlInsertParseException("Unexpected token (" + group.getTokens() + ')', stmt);
            }
        
            columnNames = new ArrayList<>();
            extractTupleIdentifiers(group, columnNames);

            if (!SqlReservedKeyword.VALUES.equals(tokens.get(4))) {
                throw new SqlInsertParseException("\"INSERT\" statement doesn't include \"VALUES\" keyword", stmt);
            }

            index = 5;
        }
        else if (SqlReservedKeyword.VALUES.equals(columnNamesGroupOrValues)) {
            index = 4;
        }
        else {
            throw new SqlInsertParseException("\"INSERT\" statement doesn't include column names or \"VALUES\" keyword", stmt);
        }

        int initialCapacity = 2;
        
        if (columnNames == null) {
            columnNames = tablesFields.get(tableName.toLowerCase());
        }

        if (columnNames != null) {
            initialCapacity = columnNames.size();
        }

        var rowValues = new LinkedHashMap<String, Object>(initialCapacity);
        var rowValuesArr = new ArrayList<SqlValue>(initialCapacity);

        for(int i = index; i < tokens.size(); i+= 2) {
            if (i > 5 && !SqlDelimiter.COMMA.equals(tokens.get(i - 1))) {
                throw new SqlInsertParseException("Delimiter expected, \"" + tokens.get(i - 1) + "\" found", stmt);
            }

            if (tokens.get(i) instanceof SqlTokenGroup tg) {
                rowValues.clear();
                rowValuesArr.clear();

                extractTupleValues(tg, rowValuesArr);

                if (columnNames == null) {
                    for(int j = 0; j < rowValuesArr.size(); j++) {
                        rowValues.put("#" + j, rowValuesArr.get(j).getValue());
                    }
                }
                else {
                    for(int j = 0; j < columnNames.size(); j++) {
                        rowValues.put(columnNames.get(j), rowValuesArr.get(j).getValue());
                    }
                }

                callback.onInsert(tableName, rowValues);
            }
            else {
                throw new SqlInsertParseException("Values expected where \"" + tokens.get(i) + "\" found", stmt);
            }
        }
    }
}
