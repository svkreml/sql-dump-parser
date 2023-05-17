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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlParserTest {

    private static String testSql;
    
    public SqlParserTest() {
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        var resourcePath = "mysql_dump.sql";
        
        try (InputStreamReader reader = new InputStreamReader(SqlParserTest.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8)) {
            CharArrayWriter w = new CharArrayWriter();
            
            reader.transferTo(w);
            
            testSql = w.toString();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void testMysqlDumpResource(boolean streamBuffer) throws Exception {
        var p = new SqlParser();
        
        List<SqlStatement> ts = new ArrayList<SqlStatement>();

        if (streamBuffer) {
            // Load the MySQL dump file as a resource
            try (StringReader reader = new StringReader(testSql)) {
                // Parse the loaded MySQL dump
                ts = p.parse(reader);
            }
        }
        else {
            ts = p.parse(testSql);
        }

        // Add your assertions to check the parsed tokens and statements
        // For example, you can check the number of statements or specific tokens within the statements
        assertNotEquals(0, ts.size());

        int creates = 0;
        int inserts = 0;

        for (SqlStatement stmt : ts) {
            if (stmt.getCommand().toString().equals("CREATE"))
                creates++;
            else if (stmt.getCommand().toString().equals("INSERT"))
                inserts++;
        }

        assertNotEquals(0, creates);
        assertNotEquals(0, inserts);
    }

    @Test
    public void testSelectInsert() throws Exception {
        var p = new SqlParser();

        var ts = p.parse("""
                         SELECT 'Hello,\\n World!',1234 FROM `tablename`;
                         INSERT INTO `tablename` (a,b,c) VALUES ('a',1234,1234.5678)
                         """);

        assertEquals(2, ts.size());

        var stmt = ts.get(0);
        var tokens = stmt.getTokens();

        assertEquals("SELECT", stmt.getCommand());
        var sqlStr = (SqlString) tokens.get(1);
        assertEquals("Hello,\n World!", sqlStr.getString());
        var sqlInt = (SqlInteger) tokens.get(3);
        assertEquals(1234, sqlInt.getLong());

        stmt = ts.get(1);
        tokens = stmt.getTokens();

        assertEquals("INSERT", stmt.getCommand());
        assertEquals("INTO", ((SqlReservedKeyword) tokens.get(1)).getKeyword());
        assertEquals("tablename", ((SqlIdentifier) tokens.get(2)).getId());

        var group = (SqlTokenGroup) tokens.get(3);
        var groupTokens = group.getTokens();
        assertEquals(SqlDelimiter.LEFT_PARENTHESES, groupTokens.get(0));
        assertEquals("a", ((SqlIdentifier) groupTokens.get(1)).getId());
        assertEquals(SqlDelimiter.COMMA, groupTokens.get(2));
        assertEquals("b", ((SqlIdentifier) groupTokens.get(3)).getId());
        assertEquals(SqlDelimiter.COMMA, groupTokens.get(4));
        assertEquals("c", ((SqlIdentifier) groupTokens.get(5)).getId());
        assertEquals(SqlDelimiter.RIGHT_PARENTHESES, groupTokens.get(6));
        assertEquals(SqlReservedKeyword.VALUES, tokens.get(4));
        var valuesGroup = (SqlTokenGroup) tokens.get(5);
        var valuesTokens = valuesGroup.getTokens();
        assertEquals(SqlDelimiter.LEFT_PARENTHESES, valuesTokens.get(0));
        assertEquals("a", ((SqlString) valuesTokens.get(1)).getString());
        assertEquals(SqlDelimiter.COMMA, valuesTokens.get(2));
        assertEquals(1234, ((SqlInteger) valuesTokens.get(3)).getLong());
        assertEquals(SqlDelimiter.COMMA, valuesTokens.get(4));
        assertEquals(1234.5678, ((SqlReal) valuesTokens.get(5)).getDouble(), 1e-4);
        assertEquals(SqlDelimiter.RIGHT_PARENTHESES, valuesTokens.get(6));
    }

    @Test
    public void testCreateTable() throws Exception {
        var p = new SqlParser();
    
        var ts = p.parse("""
                         CREATE TABLE `users` (
                             `id` INT NOT NULL AUTO_INCREMENT,
                             `username` VARCHAR(255) NOT NULL,
                             `email` VARCHAR(255) NOT NULL UNIQUE,
                             `password` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`)
                         );
                         """);
    
        assertEquals(1, ts.size());
        assertEquals("[CREATE TABLE users ( id INT NOT NULL AUTO_INCREMENT , username VARCHAR ( 255 ) NOT NULL , email VARCHAR ( 255 ) NOT NULL UNIQUE , password VARCHAR ( 255 ) NOT NULL , PRIMARY KEY ( id ) )]", ts.toString());
    
        var stmt = ts.get(0);
        var tokens = stmt.getTokens();
    
        assertEquals("CREATE", stmt.getCommand());
        assertEquals("TABLE", ((SqlReservedKeyword)tokens.get(1)).getKeyword());
        assertEquals("users", ((SqlIdentifier)tokens.get(2)).getId());
    
        var group = (SqlTokenGroup) tokens.get(3);
        var groupTokens = group.getTokens();
        assertEquals(SqlDelimiter.LEFT_PARENTHESES, groupTokens.get(0));
        assertEquals("id", ((SqlIdentifier)groupTokens.get(1)).getId());
        assertEquals(SqlReservedKeyword.INT, groupTokens.get(2));
        assertEquals(SqlReservedKeyword.NOT, groupTokens.get(3));
        assertEquals(SqlReservedKeyword.NULL, groupTokens.get(4));
        assertEquals(SqlReservedKeyword.AUTO_INCREMENT, groupTokens.get(5));
        assertEquals(SqlDelimiter.COMMA, groupTokens.get(6));
        assertEquals("username", ((SqlIdentifier)groupTokens.get(7)).getId());
        assertEquals(SqlReservedKeyword.VARCHAR, groupTokens.get(8));
        assertEquals("( 255 )", ((SqlTokenGroup) groupTokens.get(9)).toString());
        assertEquals(SqlReservedKeyword.NOT, groupTokens.get(10));
        assertEquals(SqlReservedKeyword.NULL, groupTokens.get(11));
        assertEquals(SqlDelimiter.COMMA, groupTokens.get(12));
        // ... continue with other columns and constraints
    
        assertEquals(SqlDelimiter.RIGHT_PARENTHESES, groupTokens.get(29));
    }

    @Test
    public void testUpdate() throws Exception {
        var p = new SqlParser();

        var ts = p.parse("""
                         UPDATE `users` SET `email` = 'new_email@example.com' WHERE `id` = 42;
                         """);

        assertEquals(1, ts.size());

        var stmt = ts.get(0);
        var tokens = stmt.getTokens();

        assertEquals("UPDATE", stmt.getCommand());
        assertEquals("users", ((SqlIdentifier)tokens.get(1)).getId());
        assertEquals(SqlReservedKeyword.SET, tokens.get(2));
        assertEquals("email", ((SqlIdentifier)tokens.get(3)).getId());
        assertEquals(SqlDelimiter.EQUAL, tokens.get(4));
        var sqlStr = (SqlString)tokens.get(5);
        assertEquals("new_email@example.com", sqlStr.getString());
        assertEquals(SqlReservedKeyword.WHERE, tokens.get(6));
        assertEquals("id", ((SqlIdentifier)tokens.get(7)).getId());
        assertEquals(SqlDelimiter.EQUAL, tokens.get(8));
        var sqlInt = (SqlInteger)tokens.get(9);
        assertEquals(42, sqlInt.getLong());
    }

    @Test
    public void testDelete() throws Exception {
        var p = new SqlParser();

        var ts = p.parse("""
                         DELETE FROM `users` WHERE `id` = 42;
                         """);

        assertEquals(1, ts.size());

        var stmt = ts.get(0);
        var tokens = stmt.getTokens();

        assertEquals("DELETE", stmt.getCommand());
        assertEquals(SqlReservedKeyword.FROM, tokens.get(1));
        assertEquals("users", ((SqlIdentifier)tokens.get(2)).getId());
        assertEquals(SqlReservedKeyword.WHERE, tokens.get(3));
        assertEquals("id", ((SqlIdentifier)tokens.get(4)).getId());
        assertEquals(SqlDelimiter.EQUAL, tokens.get(5));
        var sqlInt = (SqlInteger)tokens.get(6);
        assertEquals(42, sqlInt.getLong());
    }    

}
