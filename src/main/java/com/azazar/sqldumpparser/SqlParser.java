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

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;

import static com.azazar.sqldumpparser.SqlUtil.*;

/**
 * A class that represents an SQL parser. The parser is designed to process SQL statements, 
 * tokens, and groups from a given input string. It can handle multiple SQL statements, 
 * white spaces, comments, and various SQL tokens such as keywords, identifiers, strings, 
 * numbers, and delimiters. The parsed tokens are then used to create SqlStatement objects.
 * The parser supports the basic SQL syntax, including SELECT, INSERT, and CREATE TABLE 
 * statements, and can be extended to support additional SQL dialects and constructs.
 *
 * Usage example:
 * <pre>
 * {@code
 * SqlParser parser = new SqlParser();
 * List<SqlStatement> statements = parser.parse(sqlString);
 * }
 * </pre>
 *
 * @author Azazar <spam@azazar.com>
 */
public class SqlParser {
    
    ArrayList<SqlToken> tokenBuffer = new ArrayList<>(10);
    StringBuilder stringBuffer = new StringBuilder(10);

    /**
     * Constructs a new instance of the SqlParser.
     * This constructor initializes the necessary internal data structures 
     * and prepares the parser for processing SQL input strings.
     */
    public SqlParser() {
    }

    /**
     * Parses an SQL string and returns a list of SqlStatement objects.
     *
     * @param str the SQL string to parse.
     * @return a list of SqlStatement objects.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     */
    public List<SqlStatement> parse(String str) throws SqlParseException {
        ArrayList<SqlStatement> result = new ArrayList<>();
        
        parse(str, result::add);
        
        return result;
    }

    /**
     * Parses an SQL string and calls the stmtConsumer for each SqlStatement.
     *
     * @param str the SQL string to parse.
     * @param stmtConsumer a Consumer instance that will be called for each SqlStatement.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     */
    public void parse(String str, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        parse(CharBuffer.wrap(str), stmtConsumer);
    }

    /**
     * Parses a CharBuffer containing SQL and calls the stmtConsumer for each SqlStatement.
     *
     * @param buf the CharBuffer containing the SQL to parse.
     * @param stmtConsumer a Consumer instance that will be called for each SqlStatement.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     */    
    public void parse(CharBuffer buf, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        skipWhitespacesAndComments(buf);
        
        while(buf.hasRemaining()) {
            parseStatement(buf, stmtConsumer);
            
            skipWhitespacesAndComments(buf);
        }
    }
    
    private void skipWhitespacesAndComments(CharBuffer buf) throws SqlParseException {
        while(buf.hasRemaining()) {
            char ch = buf.charAt(0);
            
            if (ch == ' ' || ch == '\t') {
                buf.get();
                continue;
            }
            
            if (ch == '\r' || ch == '\n') {
                buf.get();
                continue;
            }

            // Skip single line comment
            if(StringUtils.startsWith(buf, "--")) {
                int end = StringUtils.indexOf(buf, "\n", 2);

                if (end == -1)
                    buf.position(buf.limit());
                else
                    buf.position(end + 1);
                
                continue;
            }

            // Skip multiline comment
            while(StringUtils.startsWith(buf, "/*")) {
                int end = StringUtils.indexOf(buf, "*/", 2);

                if (end == -1)
                    throw new SqlParseException("Endless comment", buf);

                buf.position(end + 2);
                
                continue;
            }
            
            break;
        }
    }

    private void parseStatement(CharBuffer buf, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        tokenBuffer.clear();
        parseGroup(buf, tokenBuffer, SqlUtil.SPLITTER);
        stmtConsumer.accept(new SqlStatement(new ArrayList<>(tokenBuffer)));
    }

    private ArrayList<SqlToken> parseGroup(CharBuffer buf, char delimiter) throws SqlParseException {
        ArrayList<SqlToken> result = new ArrayList<>();
        
        if (delimiter == ')')
            result.add(SqlDelimiter.LEFT_PARENTHESES);
        
        advance(buf);
        skipWhitespacesAndComments(buf);

        parseGroup(buf, result, delimiter);

        if (delimiter == ')')
            result.add(SqlDelimiter.RIGHT_PARENTHESES);
        
        return result;
    }

    private void parseGroup(CharBuffer buf, ArrayList<SqlToken> tokenBuffer, char delimiter) throws SqlParseException {
        while(buf.hasRemaining()) {
            // Parse token
            char startChar = buf.charAt(0);

            if (startChar == delimiter) {
                buf.get();
                return;
            }

            if (SqlUtil.isKeywordFirst(startChar)) {
                int end = StringUtils.indexOfAnyBut(buf, SqlUtil.KEYWORD_CHARS);

                if (end == -1)
                    end = buf.length();
                
                String word = buf.subSequence(0, end).toString();

                advance(buf, end);
                
                tokenBuffer.add(SqlReservedKeyword.isKeyword(word) ? SqlReservedKeyword.create(word) : new SqlIdentifier(word));
            }
            else if (SqlUtil.isNumber(startChar)) {
                int end = StringUtils.indexOfAnyBut(buf, SqlUtil.NUMERIC_CHARS);

                if (end == -1)
                    end = buf.length();
                
                String number = buf.subSequence(0, end).toString();
                
                advance(buf, end);
                
                if (buf.remaining() >= 2 && buf.charAt(0) == '.' && SqlUtil.isNumber(buf.charAt(1))) {
                    advance(buf, 1);
                    
                    end = StringUtils.indexOfAnyBut(buf, SqlUtil.NUMERIC_CHARS);

                    if (end == -1)
                        end = buf.length();
                    
                    number = number + "." + buf.subSequence(0, end).toString();

                    advance(buf, end);

                    tokenBuffer.add(new SqlReal(Double.parseDouble(number)));
                }
                else {
                    tokenBuffer.add(new SqlInteger(Long.parseLong(number)));
                }
            }
            else {
                switch (startChar) {
                    case '"', '\'' -> parseString(buf, tokenBuffer, startChar);
                    case '`' -> parseIdentifier(buf, tokenBuffer);
                    case '(' -> tokenBuffer.add(new SqlTokenGroup(parseGroup(buf, ')')));
                    case ',' -> parseDelimiter(buf, tokenBuffer, SqlDelimiter.COMMA);
                    case '=' -> parseDelimiter(buf, tokenBuffer, SqlDelimiter.EQUAL);
                    default -> throw new SqlParseException("Unexpected \"" + startChar + "\"", buf);
                }
            }
            
            skipWhitespacesAndComments(buf);
        }
        
        if (delimiter != ';') {
            throw new SqlParseException("Group closing delimiter \"" + delimiter + "\" not found", buf);
        }
    }
    
    private void parseDelimiter(CharBuffer buf, ArrayList<SqlToken> tokenBuffer, SqlDelimiter delimiter) {
        tokenBuffer.add(delimiter);
        buf.get();
    }

    private void parseString(CharBuffer buf, ArrayList<SqlToken> tokenBuffer, char startChar) throws SqlParseException {
        stringBuffer.setLength(0);
        
        buf.get();

        while(buf.hasRemaining()) {
            char ch = buf.get();
            
            if (ch == startChar) {
                tokenBuffer.add(new SqlString(stringBuffer.toString()));
                return;
            }

            if (ch == '\\') {
                char escaped = buf.get();
                
                stringBuffer.append(switch (escaped) {
                    case 'b' -> '\b';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case 'n' -> '\n';
                    default -> escaped;
                });
            }
            else {
                stringBuffer.append(ch);
            }
        }
 
        throw new SqlParseException("No closing delimiter for string: " + startChar, buf);
    }

    private void parseIdentifier(CharBuffer buf, ArrayList<SqlToken> tokenBuffer) throws SqlParseException {
        int end = StringUtils.indexOf(buf, '`', 1);
        
        if (end == -1) {
            throw new SqlParseException("No closing backtick", buf);
        }
        
        tokenBuffer.add(new SqlIdentifier(buf.subSequence(1, end).toString()));
        
        advance(buf, end + 1);
    }
    
}
