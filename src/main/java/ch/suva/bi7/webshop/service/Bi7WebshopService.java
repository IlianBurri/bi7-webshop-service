package ch.suva.bi7.webshop.service;

import ch.suva.bi7.webshop.service.controller.*;
import ch.suva.bi7.webshop.service.dao.*;
import ch.suva.bi7.webshop.service.config.WebshopServiceConfig;
import ch.suva.bi7.webshop.service.db.JpaEntityManagerFactoryProvider;
import ch.suva.bi7.webshop.service.db.LiquibaseMigrationRunner;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.service.*;
import io.javalin.Javalin;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.pem.util.PemUtils;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.http.HttpCookie;
import jakarta.persistence.EntityManagerFactory;

import java.nio.file.Path;
import javax.net.ssl.X509ExtendedKeyManager;

public class Bi7WebshopService {
    public static void main(String[] args) {
        try {
            WebshopServiceConfig.logKonfiguration();

            if (WebshopServiceConfig.runLiquibaseMigration()) {
                LiquibaseMigrationRunner.migrate(
                        WebshopServiceConfig.getDBHost(),
                        WebshopServiceConfig.getDBPort(),
                        WebshopServiceConfig.getDBSchema(),
                        WebshopServiceConfig.getDBUser(),
                        WebshopServiceConfig.getDBPassword());
            } else {
                System.out.println("Liquibase Migrationen werden nicht ausgeführt. Setze 'app.runLiquibaseMigration=true' in der Konfiguration, um Migrationen auszuführen.");
            }

            EntityManagerFactory entityManagerFactory = JpaEntityManagerFactoryProvider.createEntityManagerFactory(
                    WebshopServiceConfig.getDBHost(), WebshopServiceConfig.getDBPort(), WebshopServiceConfig.getDBSchema(), WebshopServiceConfig.getDBUser(), WebshopServiceConfig.getDBPassword());
            // Beim Shudown der Anwendung die EntityManagerFactory schließen, um Ressourcen freizugeben
            Runtime.getRuntime().addShutdownHook(new Thread(entityManagerFactory::close));

            BenutzerDao benutzerDao = new BenutzerDaoImpl(entityManagerFactory);
            WarenkorbDao warenkorbDao = new WarenkorbDaoImpl(entityManagerFactory);
            ArtikelDao artikelDao = new ArtikelDaoImpl(entityManagerFactory);
            BenutzerService benutzerService = new BenutzerService(benutzerDao);
            ArtikelService artikelService = new ArtikelService(artikelDao);
            if (WebshopServiceConfig.lagerAuffuellenBeimStart()) {
                int aufgefuellt = artikelService.fuelleLagerAuf();
                System.out.println("Lager aufgefüllt: " + aufgefuellt + " Artikel auf mindestens "
                        + ArtikelEntity.STANDARD_LAGERBESTAND + " Stück gesetzt.");
            }
            AdresseController adresseController = new AdresseController(
                    new AdresseService(new AdresseDaoImpl(entityManagerFactory)));
            WarenkorbController warenkorbController = new WarenkorbController(
                    new WarenkorbService(warenkorbDao, artikelDao), artikelService);
            ArtikelController artikelController = new ArtikelController(artikelService, benutzerService);
            BenutzerController benutzerController = new BenutzerController(benutzerService);

            BestellungDao bestellungDao = new BestellungDaoImpl(entityManagerFactory);
            BestellungController bestellungController = new BestellungController(
                    new BestellungService(bestellungDao, warenkorbDao, artikelDao));

            Javalin.create(config -> {
                config.jetty.addConnector((server, httpConfig) -> {
                    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
                    httpsConfig.addCustomizer(new SecureRequestCustomizer());
                    ServerConnector connector = new ServerConnector(server,
                            new SslConnectionFactory(erstelleSslContextFactory(), "http/1.1"),
                            new HttpConnectionFactory(httpsConfig));
                    connector.setPort(7070);
                    return connector;
                });
                config.jetty.modifyServletContextHandler(handler -> {
                    handler.getSessionHandler().setSameSite(HttpCookie.SameSite.NONE);
                    handler.getSessionHandler().getSessionCookieConfig().setSecure(true);
                });

                // CORS: Der Browser (z.B. 192.168.1.40) lädt die Seite vom UI-Server und sendet dessen Origin mit.
                // Erlaubt werden muss also die Origin des UI-Servers (Schema + Host + Port), nicht die IP des Clients.
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> {
                        // Wenn man 'allowCredentials' auf true setzt, verlangt die CORS-Spezifikation, dass die
                        // erlaubten Origins explizit angegeben werden. Ein Wildcard '*' ist dann nicht erlaubt.
                        it.allowCredentials = true;
                        it.allowHost(WebshopServiceConfig.getFirstCorsAllowedOrigin(),
                                     WebshopServiceConfig.getAdditionalCorsAllowedOrigins());
                        // Hosts ohne Schema werden als https://... interpretiert (UI-Server läuft ebenfalls auf HTTPS)
                        it.defaultScheme = "https";
                        // Damit das JavaScript den Header "sessionId" aus der Antwort lesen darf
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
                config.routes.get("/api/warenkorb/{email}/verfuegbarkeit", warenkorbController.pruefeVerfuegbarkeit);
                config.routes.post("/api/warenkorb/add", warenkorbController.fuegeArtikelZuWarenkorbHinzu);
                config.routes.delete("/api/warenkorb/item/{id}", warenkorbController.loescheWarenkorbEintrag);
                config.routes.put("/api/warenkorb/item/{id}", warenkorbController.aktualisiereMenge);

                config.routes.get("/api/adresse/{email}", adresseController.ladeAdressen);
                config.routes.post("/api/adresse", adresseController.erstelleAdresse);
                config.routes.put("/api/adresse/{adressId}", adresseController.aktualisiereAdresse);
                config.routes.delete("/api/adresse/{adressId}", adresseController.loescheAdresse);

                config.routes.post("/api/bestellung/checkout", bestellungController.erstelleBestellung);
                config.routes.get("/api/bestellung/{email}", bestellungController.ladeBestellungenNachBenutzer);
            }).start();
        } catch (Exception e) {
            System.err.println("Webshop konnte nicht gestartet werden: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static SslContextFactory.Server erstelleSslContextFactory() {
        Path certPath = Path.of(WebshopServiceConfig.getSslCertPath());
        Path keyPath = Path.of(WebshopServiceConfig.getSslKeyPath());
        String keyPassword = WebshopServiceConfig.getSslKeyPassword();
        X509ExtendedKeyManager keyManager = keyPassword == null
                ? PemUtils.loadIdentityMaterial(certPath, keyPath)
                : PemUtils.loadIdentityMaterial(certPath, keyPath, keyPassword.toCharArray());

        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setSslContext(SSLFactory.builder().withIdentityMaterial(keyManager).build().getSslContext());
        return sslContextFactory;
    }
}
