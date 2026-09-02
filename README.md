    # MariaDB-Setup für den Webshop (Docker)

Diese Anleitung beschreibt Schritt für Schritt, wie du eine MariaDB-Datenbank in Docker aufsetzt, einen Datenbank-User anlegst und wie das Datenbankschema sowie die Testdaten über Liquibase verwaltet werden.

---

## Voraussetzungen

- Docker ist installiert und läuft (`sudo systemctl status docker`)
- Du hast `sudo`-Rechte auf dem System
- Optional: MySQL/MariaDB-Client zum Verbinden von der Kommandozeile

---

## 1. MariaDB-Container starten

```bash
sudo docker run --name mariadb-container \
  -e MYSQL_ROOT_PASSWORD=webshopdb-root-pw \
  -d \
  -v mariadb_data:/var/lib/mysql \
  -p 3306:3306 \
  mariadb:latest
```

**Was passiert hier?**

| Parameter | Bedeutung |
|---|---|
| `--name mariadb-container` | Name des Containers, unter dem du ihn später ansprichst |
| `-e MYSQL_ROOT_PASSWORD=...` | Setzt das Root-Passwort der Datenbank |
| `-d` | Startet den Container im Hintergrund (detached) |
| `-v mariadb_data:/var/lib/mysql` | Persistiert die Datenbankdateien in einem Docker-Volume, damit sie einen Container-Neustart überleben |
| `-p 3306:3306` | Macht den Datenbank-Port auf dem Host verfügbar |

> **Korrektur zum Originalbefehl:** Der offizielle `mariadb`-Docker-Container speichert seine Daten unter `/var/lib/mysql` (nicht `/var/lib/mariadb`). Mit dem falschen Pfad läuft der Container zwar, aber das Volume greift nicht richtig – bei einem `docker rm` wären deine Daten weg. Oben ist der korrigierte Pfad bereits eingetragen.

**Prüfen, ob der Container läuft:**

```bash
sudo docker ps
```

---

## 2. MySQL/MariaDB-Client installieren (falls nötig)

```bash
sudo apt install mysql-client
```

*(Je nach Distribution kann das Paket auch `mariadb-client` heißen.)*

---

## 3. Mit der Datenbank verbinden (als root)

```bash
mysql -h 127.0.0.1 -P 3306 -u root -pwebshopdb-root-pw
```

> Tipp: Zwischen `-p` und dem Passwort darf **kein Leerzeichen** stehen — das ist kein Tippfehler.

---

## 4. Datenbank und Webshop-User anlegen

Im MySQL-Client (als `root`) folgende Befehle ausführen:

```sql
CREATE DATABASE webshopdb;
CREATE USER 'webshopuser'@'%' IDENTIFIED BY 'webshoppassword';
GRANT ALL PRIVILEGES ON webshopdb.* TO 'webshopuser'@'%';
FLUSH PRIVILEGES;
```

Das legt an:
- eine neue Datenbank `webshopdb`
- einen neuen User `webshopuser`, der sich von **jedem Host** (`%`) aus verbinden darf
- volle Rechte für diesen User **nur** auf die Datenbank `webshopdb`

> **Sicherheitshinweis:** `webshopuser@'%'` ist für lokale Entwicklung okay, in einer produktiven Umgebung solltest du den Host einschränken (z. B. `'webshopuser'@'10.0.0.%'`) und ein starkes, nicht im Klartext dokumentiertes Passwort verwenden.

---

## 5. Datenbankschema & Testdaten (Liquibase)

Die Tabellenstruktur (`user`, `artikel`, `adresse`, `warenkorb_item`) sowie die initialen Testdaten (Benutzer und Artikel) werden **automatisch beim Start des Backends über Liquibase** verwaltet und eingespielt.

Es ist **nicht mehr nötig**, `CREATE TABLE` oder `INSERT`-Befehle manuell in MariaDB auszuführen.

**Prüfen, ob Liquibase die Tabellen und Daten angelegt hat:**

Nach dem ersten Start des Backends kannst du in der MariaDB kontrollieren, ob die Daten vorhanden sind:

```bash
mysql -h 127.0.0.1 -P 3306 -u webshopuser -pwebshoppassword webshopdb
```

```sql
SELECT * FROM user;
SELECT * FROM artikel;
SELECT * FROM adresse;
SELECT * FROM warenkorb_item;
```

---

## Kurz-Checkliste Datenbank

- [ ] Container `mariadb-container` läuft (`docker ps`)
- [ ] Verbindung als `root` funktioniert
- [ ] Datenbank `webshopdb` und User `webshopuser` angelegt
- [ ] Verbindung als `webshopuser` funktioniert
- [ ] Liquibase führt beim Backend-Start die Migrationen aus (Tabellen & Testdaten)

---

# Webshop starten und aufrufen (Backend + UI)

Der Webshop besteht aus **zwei getrennten Projekten/Servern**. Beide müssen laufen, damit die Seite funktioniert:

| Server | Projekt | Port | Aufgabe |
|---|---|---|---|
| **Backend-Service** | `bi7-webshop-service` | `7070` | REST-API (Login, Artikel, Warenkorb, Adressen) – Daten aus **MariaDB** |
| **UI-Webserver** | `bi7-webshop-ui` | `8080` | Liefert die HTML-Seiten aus und bedient eigene API-Routen – eigene **H2-Datenbank** |

> **Konfiguration Backend:** Die Zugangsdaten liegen nicht im Code, sondern in `src/main/resources/application-dev.properties` (Keys `db.host`, `db.name`, `db.user`, `db.password`). Im Repository stehen nur Beispielwerte. Für lokale, echte Zugangsdaten die Umgebungsvariablen `DB_HOST`, `DB_NAME`, `DB_USER` und `DB_PASSWORD` setzen – diese haben Vorrang vor der Datei und landen so nie im Repo.
> Weitere Werte: `server.port` (HTTP-Port des Backends, Env `SERVER_PORT`) und `app.isDev` (Env `APP_IS_DEV`): `true` erlaubt CORS für jede Origin (`anyHost()`, nur für Entwicklung), `false` beschränkt CORS auf die lokale UI (Produktion).

---

## 1. Backend starten (bi7-webshop-service)

**Voraussetzung:** Die MariaDB läuft (siehe Setup oben, Port `3306`, Datenbank `webshopdb`).

```bash
cd /home/ilian/bi7-webshop-service
mvn package         # baut das ausführbare JAR
java -jar target/bi7-webshop-service-1.0-SNAPSHOT.jar
```

**Funktionstest:**

```bash
curl http://localhost:7070/        # erwartet: "Hello World"
curl http://localhost:7070/artikel # erwartet: JSON-Liste der Artikel
curl http://localhost:7070/users   # erwartet: JSON-Liste der Benutzernamen
```

---

## 2. UI-Webserver starten (bi7-webshop-ui)

Am einfachsten startest du ihn wie bisher **in IntelliJ** (Run-Konfiguration mit Main-Klasse `ch.suva.bi7.webshop.BI7WebshopWebserver`).

> **Wichtig:** Starte den Server immer aus dem Verzeichnis `bi7-webshop-ui` heraus. Die H2-Datenbank wird relativ zum Arbeitsverzeichnis gesucht: `./data/webshop-db`. Startest du woanders, findet er die Datenbank nicht.

**Funktionstest:**

```bash
curl http://localhost:8080/HTML/landingpage.html   # erwartet: HTTP 200, HTML-Code
curl http://localhost:8080/api/artikel             # erwartet: HTTP 200, JSON-Liste
```

---

## 3. Seite im Browser aufrufen

**Die Startseite ist:**

> **http://localhost:8080/HTML/landingpage.html** (oder `http://localhost:8080/` mit Weiterleitung, siehe Punkt 4)

| URL | Ergebnis |
|---|---|
| `http://localhost:8080/HTML/landingpage.html` | ✅ Startseite mit Artikeln |
| `http://localhost:8080/HTML/loginForm.html` | ✅ Login |
| `http://localhost:8080/HTML/registerForm.html` | ✅ Registrierung |
| `http://localhost:8080/HTML/warenkorb.html` | ✅ Warenkorb |
| `http://localhost:8080/HTML/checkOut.html` | ✅ Checkout |
---


## 4. Troubleshooting / Kurz-Checkliste

- [ ] Beide Server laufen: `ss -tlnp | grep -E ':7070|:8080'`
- [ ] `curl http://localhost:7070/` liefert `Hello World`
- [ ] `curl http://localhost:8080/HTML/landingpage.html` liefert HTTP 200
- [ ] MariaDB (3306) erreichbar – sonst antwortet das Backend mit 500
- [ ] H2-Datei vorhanden: `ls /home/ilian/bi7-webshop-ui/data/webshop-db.mv.db`
- [ ] Internet nötig: Bootstrap-CSS und Platzhalterbilder kommen von externen CDNs (`cdn.jsdelivr.net`, `via.placeholder.com`) – ohne Internet sieht die Seite ungestylt aus
- [ ] Nach **Java-Code-Änderungen** den jeweiligen Server **neu bauen und neu starten** (HTML/CSS/JS werden live übernommen, Java-Klassen nicht)