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
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import com.azazar.sqldumpparser.util.IOExceptionWrapper;
import com.azazar.sqldumpparser.util.ParseBuffer;

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
    
    /**
     * ArrayList of SqlTokens used to store the parsed tokens.
     */
    ArrayList<SqlToken> tokenBuffer = new ArrayList<>(10);
    
    /**
     * StringBuilder used to store the parsed strings.
     */
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
    public List<SqlStatement> parse(CharSequence str) throws SqlParseException {
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
    public void parse(CharSequence str, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        parse(ParseBuffer.wrap(str), stmtConsumer);
    }

    /**
     * Parses an SQL string from a Reader and returns a list of SqlStatement objects.
     *
     * @param reader the Reader containing the SQL string to parse.
     * @return a list of SqlStatement objects.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     * @throws IOException if an I/O error occurs while reading from the Reader.
     */
    public List<SqlStatement> parse(Reader reader) throws SqlParseException, IOException {
        ArrayList<SqlStatement> result = new ArrayList<>();
        
        parse(reader, result::add);
        
        return result;
    }

    /**
     * Parses an SQL string from a Reader and calls the stmtConsumer for each SqlStatement.
     *
     * @param reader the Reader containing the SQL string to parse.
     * @param stmtConsumer a Consumer instance that will be called for each SqlStatement.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     * @throws IOException if an I/O error occurs while reading from the Reader.
     */
    public void parse(Reader reader, Consumer<SqlStatement> stmtConsumer) throws SqlParseException, IOException {
        try {
            parse(ParseBuffer.wrap(reader), stmtConsumer);
        } catch (IOExceptionWrapper ex) {
            throw ex.getCause();
        }
    }

    /**
     * Parses a CharBuffer containing SQL and calls the stmtConsumer for each SqlStatement.
     *
     * @param buf the CharBuffer containing the SQL to parse.
     * @param stmtConsumer a Consumer instance that will be called for each SqlStatement.
     * @throws SqlParseException if there is a syntax error in the input SQL string.
     */    
    public void parse(ParseBuffer buf, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        skipWhitespacesAndComments(buf);
        
        while(!buf.isEmpty()) {
            parseStatement(buf, stmtConsumer);
            
            skipWhitespacesAndComments(buf);
        }
    }
    
    private void skipWhitespacesAndComments(ParseBuffer buf) throws SqlParseException {
        while(!buf.isEmpty()) {
            char ch = buf.charAt(0);
            
            if (ch == ' ' || ch == '\t') {
                buf.advance();
                continue;
            }
            
            if (ch == '\r' || ch == '\n') {
                buf.advance();
                continue;
            }

            // Skip single line comment
            if(StringUtils.startsWith(buf, "--")) {
                int end = ParseBufferUtils.indexOf(buf, "\n", 2);

                if (end == -1)
                    buf.advance(buf.length());
                else
                    buf.advance(end + 1);
                
                continue;
            }

            // Skip multiline comment
            while(StringUtils.startsWith(buf, "/*")) {
                int end = ParseBufferUtils.indexOf(buf, "*/", 2);

                if (end == -1)
                    throw new SqlParseException("Endless comment", buf);

                buf.advance(end + 2);
                
                continue;
            }
            
            break;
        }
    }

    private void parseStatement(ParseBuffer buf, Consumer<SqlStatement> stmtConsumer) throws SqlParseException {
        tokenBuffer.clear();
        parseGroup(buf, tokenBuffer, SqlUtil.SPLITTER);
        stmtConsumer.accept(new SqlStatement(new ArrayList<>(tokenBuffer)));
    }

    private ArrayList<SqlToken> parseGroup(ParseBuffer buf, char delimiter) throws SqlParseException {
        ArrayList<SqlToken> result = new ArrayList<>();
        
        if (delimiter == ')')
            result.add(SqlDelimiter.LEFT_PARENTHESES);
        
        buf.advance();
        skipWhitespacesAndComments(buf);

        parseGroup(buf, result, delimiter);

        if (delimiter == ')')
            result.add(SqlDelimiter.RIGHT_PARENTHESES);
        
        return result;
    }

    private void parseGroup(ParseBuffer buf, ArrayList<SqlToken> tokenBuffer, char delimiter) throws SqlParseException {
        while(!buf.isEmpty()) {
            // Parse token
            char startChar = buf.charAt(0);

            if (startChar == delimiter) {
                buf.advance();
                return;
            }

            if (SqlUtil.isKeywordFirst(startChar)) {
                int end = StringUtils.indexOfAnyBut(buf, SqlUtil.KEYWORD_CHARS);

                if (end == -1)
                    end = buf.length();
                
                String word = buf.subSequence(0, end).toString();

                buf.advance(end);
                
                tokenBuffer.add(SqlReservedKeyword.isKeyword(word) ? SqlReservedKeyword.create(word) : new SqlIdentifier(word));
            }
            else if (SqlUtil.isNumber(startChar)) {
                int end = StringUtils.indexOfAnyBut(buf, SqlUtil.NUMERIC_CHARS);

                if (end == -1)
                    end = buf.length();
                
                String number = buf.subSequence(0, end).toString();
                
                buf.advance(end);
                
                if (buf.length() >= 2 && buf.charAt(0) == '.' && SqlUtil.isNumber(buf.charAt(1))) {
                    buf.advance();
                    
                    end = StringUtils.indexOfAnyBut(buf, SqlUtil.NUMERIC_CHARS);

                    if (end == -1)
                        end = buf.length();
                    
                    number = number + "." + buf.subSequence(0, end).toString();

                    buf.advance(end);

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
                    case '.' -> parseDelimiter(buf, tokenBuffer, SqlDelimiter.DOT);
                    default -> throw new SqlParseException("Unexpected \"" + startChar + "\"", buf);
                }
            }
            
            skipWhitespacesAndComments(buf);
        }
        
        if (delimiter != ';') {
            throw new SqlParseException("Group closing delimiter \"" + delimiter + "\" not found", buf);
        }
    }
    
    private void parseDelimiter(ParseBuffer buf, ArrayList<SqlToken> tokenBuffer, SqlDelimiter delimiter) {
        tokenBuffer.add(delimiter);
        buf.advance();
    }

    private void parseString(ParseBuffer buf, ArrayList<SqlToken> tokenBuffer, char startChar) throws SqlParseException {
        stringBuffer.setLength(0);
        
        buf.advance();

        while(!buf.isEmpty()) {
            char ch = buf.getAdvance();
            
            if (ch == startChar) {
                tokenBuffer.add(new SqlString(stringBuffer.toString()));
                return;
            }

            if (ch == '\\') {
                char escaped = buf.getAdvance();
                
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

    private void parseIdentifier(ParseBuffer buf, ArrayList<SqlToken> tokenBuffer) throws SqlParseException {
        int end = StringUtils.indexOf(buf, '`', 1);
        
        if (end == -1) {
            throw new SqlParseException("No closing backtick", buf);
        }
        
        tokenBuffer.add(new SqlIdentifier(buf.subSequence(1, end).toString()));
        
        buf.advance(end + 1);
    }
    
}
