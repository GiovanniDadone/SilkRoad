package com.example.silkroad.config;

import java.util.Collections;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public class MysqlTestContainer implements SqlTestContainer {

    private MySQLContainer<?> mysqlContainer;

    @Override
    public void destroy() {
        if (null != mysqlContainer && mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (null == mysqlContainer) {
            mysqlContainer = new MySQLContainer<>("mysql:9.2.0")
                .withDatabaseName("SilkRoad")
                .withTmpFs(Collections.singletonMap("/testtmpfs", "rw"))
                .withReuse(true);
        }
        if (!mysqlContainer.isRunning()) {
            mysqlContainer.start();
        }
    }

    @Override
    public JdbcDatabaseContainer<?> getTestContainer() {
        return mysqlContainer;
    }
}
