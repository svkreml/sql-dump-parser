# SQL Dump Parser

[![](https://jitpack.io/v/azazar/sql-dump-parser.svg)](https://jitpack.io/#azazar/sql-dump-parser)

SQL Dump Parser is a lightweight Java library designed to parse SQL dump files, focusing on handling SQL statements, tokens, and groups from a given input string. The library supports basic SQL syntax, including SELECT, INSERT, and CREATE TABLE statements. It can be extended to support additional SQL dialects and constructs.

## Features

- Parses multiple SQL statements
- Handles white spaces and comments
- Processes various SQL tokens such as keywords, identifiers, strings, numbers, and delimiters
- Creates `SqlStatement` objects from parsed tokens
- Supports basic SQL syntax, including SELECT, INSERT, and CREATE TABLE statements

## Installation

### Using JitPack

To use SQL Dump Parser with JitPack, add the following to your project's `build.gradle` or `pom.xml` files:

#### Gradle

Add the JitPack repository to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Add the dependency:

```groovy
dependencies {
    implementation 'com.github.azazar:sql-dump-parser:1.1'
}
```

#### Maven

Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>com.github.azazar</groupId>
    <artifactId>sql-dump-parser</artifactId>
    <version>1.1</version>
</dependency>
```

### Manual Installation

To use SQL Dump Parser in your project, simply include the JAR file in your project's classpath.


## Usage Examples

### Parsing a single SQL statement

```java
import com.azazar.sqldumpparser.SqlParser;
import com.azazar.sqldumpparser.SqlStatement;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        SqlParser parser = new SqlParser();
        String sql = "SELECT * FROM users;";
        try {
            List<SqlStatement> statements = parser.parse(sql);
            // Process the parsed statements
        } catch (SqlParseException e) {
            System.err.println("Error parsing SQL: " + e.getMessage());
        }
    }
}
```

### Parsing multiple SQL statements

```java
import com.azazar.sqldumpparser.SqlParser;
import com.azazar.sqldumpparser.SqlStatement;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        SqlParser parser = new SqlParser();
        String sql = """
                     CREATE TABLE users (
                         id INT NOT NULL AUTO_INCREMENT,
                         username VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         PRIMARY KEY (id)
                     );
                     INSERT INTO users (username, email, password) VALUES ('test', 'test@example.com', 'password');
                     SELECT * FROM users;
                     """;
        try {
            List<SqlStatement> statements = parser.parse(sql);
            // Process the parsed statements
        } catch (SqlParseException e) {
            System.err.println("Error parsing SQL: " + e.getMessage());
        }
    }
}
```

# License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
