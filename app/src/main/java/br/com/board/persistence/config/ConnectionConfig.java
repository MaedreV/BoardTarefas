package br.com.board.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost/board_dio";
        var user = "root";
        var password = "board";
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

}
