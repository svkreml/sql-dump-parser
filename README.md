# SQL Dump Parser

[![](https://jitpack.io/v/azazar/sql-dump-parser.svg)](https://jitpack.io/#azazar/sql-dump-parser)

SQL Dump Parser is a lightweight Java library designed to parse SQL dump files, focusing on handling SQL statements and tokens from a given input string. The library supports basic SQL syntax, including SELECT, INSERT, and CREATE TABLE statements. It can be extended to support additional SQL dialects and constructs.

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
    implementation 'com.github.azazar:sql-dump-parser:1.4'
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
    <version>1.4</version>
</dependency>
```

### Manual Installation

To use SQL Dump Parser in your project, simply include the JAR file in your project's classpath.


## Usage Examples

This library provides a simple SQL dump parser to process SQL statements and extract data from `INSERT` statements. Below are a few examples of how to use the library to achieve this.

### Example 1: Parsing SQL from a String

```java
import com.azazar.sqldumpparser.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String sql = "INSERT INTO users (id, name, age) VALUES (1, 'John Doe', 30);";
        
        for (SqlStatement stmt : new SqlParser().parse(sql)) {
            if (stmt.getCommand().toString().equalsIgnoreCase("INSERT")) {
                System.out.println("INSERT statement found:");
                System.out.println(stmt);
            }
        }
    }
}
```

### Example 2: Parsing SQL from a File

```java
import com.azazar.sqldumpparser.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        try (FileReader fileReader = new FileReader("path/to/your/sql_dump.sql")) {
            for (SqlStatement stmt : new SqlParser().parse(fileReader)) {
                if (stmt.getCommand().toString().equalsIgnoreCase("UPDATE")) {
                    System.out.println("UPDATE statement found:");
                    System.out.println(stmt);
                }
            }
        }
    }
}
```

### Example 3: Extracting Data from an INSERT Statement

```java
import com.azazar.sqldumpparser.SqlInsertParser;
import com.azazar.sqldumpparser.SqlValue;

import java.io.StringReader;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        var inputSql = "INSERT INTO users (id, name, age) VALUES (1, 'Alice', 30), (2, 'Bob', 25);";
        var inputReader = new StringReader(inputSql);

        SqlInsertParser.parse(inputReader, Set.of("users"), (tableName, values) -> {
            Map<String, Object> actualValues = new LinkedHashMap<>();
            for (var entry : values.entrySet()) {
                actualValues.put(entry.getKey(), entry.getValue().getValue());
            }

            System.out.println("Inserting into table: " + tableName);
            System.out.println("Values: " + actualValues);
        });
    }
}
```

These examples demonstrate how to use the `SqlParser` class to parse SQL strings, read SQL dumps from files, and extract data from `INSERT` statements.

# License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
