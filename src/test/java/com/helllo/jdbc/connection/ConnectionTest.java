package com.helllo.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.helllo.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    /**
     * 순수 DriverManager 직접 이용 - 매번 커넥션 생성
     * @throws SQLException
     */
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("con1={}", con1);
        log.info("con2={}", con2);
    }

    /**
     * DataSource 를 구현한 DriverManagerDataSource 이용 - 매번 커넥션 생성
     * @throws SQLException
     */
    @Test
    void dataSourceDriverManager() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    /**
     * DataSource 르 구현한 HikariDataSource 이용 - 커넥션 풀에서 커넥션 획득
     * @throws SQLException
     * @throws InterruptedException
     */
    @Test
    void dataSourceHikariCP() throws SQLException, InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("myPool");

        useDataSource(dataSource);
        Thread.sleep(2000);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("con1={}", con1);
        log.info("con2={}", con2);
    }
}

