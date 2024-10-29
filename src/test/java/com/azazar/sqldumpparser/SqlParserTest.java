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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Azazar <spam@azazar.com>
 */
public class SqlParserTest {

    private static String testSql;
    private static final Map<String, SqlReservedKeyword> convertToBooleanMap = new HashMap<>();

    public SqlParserTest() {
    }

    @BeforeAll public static void beforeAll() throws IOException {
        final byte[]
                readAllBytes =
                Files.readAllBytes(Paths.get("/home/svkreml/IdeaProjects/dit/kpp/sql2",
                        "MySQL_dbs1t_2024_10_25_04_00.sql"));

        testSql = new String(readAllBytes, StandardCharsets.UTF_8);


    }

    private static void replaceToBoolean(SqlTokenGroup token, int index) {
        token.getTokens().set(index, getaBoolean(token, index));
    }

    private static SqlReservedKeyword getaBoolean(SqlTokenGroup token, int index) {
        final SqlToken sqlToken = token.getTokens().get(index);
        final SqlReservedKeyword aBoolean = toBoolean(sqlToken);
        convertToBooleanMap.put(sqlToken.toString(), aBoolean);
        return aBoolean;
    }

    private static SqlReservedKeyword toBoolean(SqlToken token) {
        return switch (token.toString()) {
            case "0", "'0'" -> SqlReservedKeyword.FALSE;
            case "1", "'\u0001'" -> SqlReservedKeyword.TRUE;
            case "NULL" -> SqlReservedKeyword.NULL;
            default -> throw new RuntimeException("Unrecognized token: " + token);
        };
    }

    @Test public void testMysqlDumpResource() throws Exception {
        var p = new SqlParser();

        List<SqlStatement> ts = p.parse(testSql);

        int inserts = 0;
        StringBuilder output = new StringBuilder();
        for (SqlStatement stmt : ts) {
            if (stmt.getCommand().toString().equals("INSERT")) {
                inserts++;

                if ("user".equals(stmt.getTokens().get(2).toString())) {
                    stmt.getTokens().set(2, new SqlIdentifier("user_tbl"));
                    final List<SqlToken> tokens = stmt.getTokens();
                    for (int i = 4; i < tokens.size(); i = i + 2) {
                        SqlTokenGroup token = (SqlTokenGroup) tokens.get(i);
                        replaceToBoolean(token, 9); // ACTIVE
                        replaceToBoolean(token, 19); // FORCE_CHANGE_PASSWORD
                    }
                } else if ("method".equals(stmt.getTokens().get(2).toString())) {
                    final List<SqlToken> tokens = stmt.getTokens();
                    for (int i = 4; i < tokens.size(); i = i + 2) {
                        SqlTokenGroup token = (SqlTokenGroup) tokens.get(i);
                        replaceToBoolean(token, 5);
                        replaceToBoolean(token, 7);
                        replaceToBoolean(token, 9);
                        replaceToBoolean(token, 11);
                        replaceToBoolean(token, 13);
                        replaceToBoolean(token, 15);
                    }
                } else if ("spr_organization".equals(stmt.getTokens().get(2).toString())) {
                    final List<SqlToken> tokens = stmt.getTokens();
                    for (int i = 4; i < tokens.size(); i = i + 2) {
                        SqlTokenGroup token = (SqlTokenGroup) tokens.get(i);
                        replaceToBoolean(token, 7); // IS_AUTHORIZED
                    }
                }
                output.append(stmt).append(";\n");
            }
        }

        assertNotEquals(0, inserts);

        System.out.println(convertToBooleanMap);

        Files.write(Paths.get("/home/svkreml/IdeaProjects/dit/kpp/sql1/V1__data.sql"),
                output.toString().getBytes(StandardCharsets.UTF_8));

    }

}
