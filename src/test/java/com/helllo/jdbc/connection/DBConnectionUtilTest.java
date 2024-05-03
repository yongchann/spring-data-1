package com.helllo.jdbc.connection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;


class DBConnectionUtilTest {

    @Test
    void connectionTest() {
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }

}