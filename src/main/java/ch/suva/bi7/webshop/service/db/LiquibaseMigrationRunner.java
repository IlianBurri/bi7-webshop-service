package ch.suva.bi7.webshop.service.db;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.database.jvm.JdbcConnection;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public final class LiquibaseMigrationRunner {

    private static final String CHANGELOG_DIR = "db";

    private LiquibaseMigrationRunner() {
    }

    public static void migrate(String host, int port, String schema, String user, String password) throws Exception {
        Class.forName("org.mariadb.jdbc.Driver").newInstance();

        String jdbcUrl = "jdbc:mariadb://" + host + ":" + port + "/" + schema + "?" +
                "user=" + user + "&password=" + password + "&useSSL=false";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            List<String> changelogFiles = resolveChangelogFiles();

            for (String changelogFile : changelogFiles) {
                System.out.println("Applying migration: " + changelogFile);
                try (Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database)) {
                    liquibase.update(new Contexts(), new LabelExpression());
                }
            }
        }
    }

    private static List<String> resolveChangelogFiles() throws Exception {
        URL dbDirUrl = Thread.currentThread().getContextClassLoader().getResource(CHANGELOG_DIR);
        if (dbDirUrl == null) {
            throw new IllegalStateException("Migrationsordner '" + CHANGELOG_DIR + "' wurde im Classpath nicht gefunden");
        }

        List<String> files = new ArrayList<>();
        if ("file".equalsIgnoreCase(dbDirUrl.getProtocol())) {
            Path dir = Paths.get(dbDirUrl.toURI());
            try (var stream = Files.list(dir)) {
                stream.filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.toLowerCase().endsWith(".xml"))
                        .forEach(name -> files.add(CHANGELOG_DIR + "/" + name));
            }
        } else {
            throw new IllegalStateException(
                    "Nicht unterstuetztes Classpath-Protokoll fuer Migrationen (erwartet 'file'): " +
                            dbDirUrl.getProtocol());
        }

        files.sort(String::compareTo);
        if (files.isEmpty()) {
            throw new IllegalStateException("Keine XML-Migrationsdateien in '" + CHANGELOG_DIR + "' gefunden");
        }
        return files;
    }
}

