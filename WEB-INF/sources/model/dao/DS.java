package model.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DS {

    private Properties p;
    private static final Logger logger = Logger.getLogger(DS.class.getName());

    public DS() {
        p = new Properties();
        try (FileInputStream input = new FileInputStream(
                "/home/infoetu/raphael.kiecken.etu/tomcat/webapps/pizzeria/WEB-INF/ressources/config.conf")) {
            p.load(input);
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        String url = p.getProperty("url");
        String nom = p.getProperty("login");
        String mdp = p.getProperty("password");

        try {
            Class.forName(p.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            logger.warning(e.getMessage());
        }

        return DriverManager.getConnection(url, nom, mdp);
    }

}
