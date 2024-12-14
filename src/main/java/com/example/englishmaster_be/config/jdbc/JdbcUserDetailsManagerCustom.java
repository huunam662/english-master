package com.example.englishmaster_be.config.jdbc;

import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class JdbcUserDetailsManagerCustom extends JdbcUserDetailsManager {

    public JdbcUserDetailsManagerCustom(DataSource dataSource) {

        super(dataSource);

        super.setUsersByUsernameQuery(
                        "SELECT u.email AS username, u.password, u.is_enabled AS enabled " +
                        "FROM users u " +
                        "WHERE u.email = ?"
        );

        super.setAuthoritiesByUsernameQuery(
                        "SELECT u.email AS username, CONCAT('ROLE_', r.role_name) AS authority " +
                        "FROM users u JOIN roles r " +
                        "ON u.email = ? " +
                        "AND r.id = u.role"
        );
    }
}
