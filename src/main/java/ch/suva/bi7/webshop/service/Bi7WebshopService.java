package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.*;
import ch.suva.bi7.webshop.service.dao.*;
import ch.suva.bi7.webshop.service.db.DBConfig;
import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.DBConnectionImpl;
import ch.suva.bi7.webshop.service.db.LiquibaseMigrationRunner;
import io.javalin.Javalin;

public class Bi7WebshopService {
    public static void main(String[] args) {
        try {

            LiquibaseMigrationRunner.migrate(
                    DBConfig.getHost(),
                    DBConfig.getPort(),
                    DBConfig.getSchema(),
                    DBConfig.getUser(),
                    DBConfig.getPassword());

            DBConnection dbConnection = new DBConnectionImpl(
                    DBConfig.getHost(), DBConfig.getPort(), DBConfig.getSchema(), DBConfig.getUser(), DBConfig.getPassword());

            BenutzerDao benutzerDao = new BenutzerDaoImpl(dbConnection);
            WarenkorbDao warenkorbDao = new WarenkorbDaoImpl(dbConnection);
            AdresseController adresseController = new AdresseController(new AdresseDaoImpl(dbConnection));
            WarenkorbController warenkorbController = new WarenkorbController(new WarenkorbDaoImpl(dbConnection));
            ArtikelController artikelController = new ArtikelController(new ArtikelDaoImpl(dbConnection), benutzerDao);
            BenutzerController benutzerController = new BenutzerController(benutzerDao);

            BestellungDao bestellungDao = new BestellungDaoImpl(dbConnection);
            BestellungController bestellungController = new BestellungController(bestellungDao, warenkorbDao);

            var app = Javalin.create(config -> {
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> {
                        if (DBConfig.isDev()) {
                            it.reflectClientOrigin = true;
                        } else {
                            it.allowHost("http://localhost:8080", "http://127.0.0.1:8080");
                        }
                        it.allowCredentials = true;
                        it.exposeHeader("sessionId");
                    });
                });
                config.routes.get("/", ctx -> ctx.result("Hello World"));
                config.routes.get("/users", benutzerController.fetchAlleBenutzernamen);
                config.routes.post("/users/login", benutzerController.login);
                config.routes.post("/users/logout", benutzerController.logout);
                config.routes.get("/users/{email}", benutzerController.fetchByEMail);
                config.routes.post("/users/register", benutzerController.register);

                config.routes.get("/artikel", artikelController.ladeAlleArtikel);
                config.routes.post("/artikel/addNew", artikelController.erstelleNeuenArtikel);

                config.routes.get("/api/warenkorb/{email}", warenkorbController.ladeWarenkorb);
                config.routes.post("/api/warenkorb/add", warenkorbController.fuegeArtikelZuWarenkorbHinzu);
                config.routes.delete("/api/warenkorb/item/{id}", warenkorbController.loescheWarenkorbEintrag);
                config.routes.put("/api/warenkorb/item/{id}", warenkorbController.aktualisiereMenge);

                config.routes.get("/api/adresse/{email}", adresseController.ladeAdressen);
                config.routes.post("/api/adresse", adresseController.erstelleAdresse);
                config.routes.put("/api/adresse/{adressId}", adresseController.aktualisiereAdresse);
                config.routes.delete("/api/adresse/{adressId}", adresseController.loescheAdresse);

                config.routes.post("/api/bestellung/checkout", bestellungController.erstelleBestellung);
                config.routes.get("/api/bestellung/{email}", bestellungController.ladeBestellungenNachBenutzer);
            }).start(7070);
        } catch (Exception e) {
            System.err.println("Webshop konnte nicht gestartet werden: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
