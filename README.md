#  MariaDB-Setup für den Webshop (Docker)

Diese Anleitung beschreibt Schritt für Schritt, wie du eine MariaDB-Datenbank in Docker aufsetzt, einen Datenbank-User und Artikel anlegst und die Tabellen für den Webshop (`user` und `artikel`) erstellst und befüllst.

---

##  Voraussetzungen

- Docker ist installiert und läuft (`sudo systemctl status docker`)
- Du hast `sudo`-Rechte auf dem System
- Optional: MySQL/MariaDB-Client zum Verbinden von der Kommandozeile

---

## 1 MariaDB-Container starten

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

>  **Korrektur zum Originalbefehl:** Der offizielle `mariadb`-Docker-Container speichert seine Daten unter `/var/lib/mysql` (nicht `/var/lib/mariadb`). Mit dem falschen Pfad läuft der Container zwar, aber das Volume greift nicht richtig – bei einem `docker rm` wären deine Daten weg. Oben ist der korrigierte Pfad bereits eingetragen.

**Prüfen, ob der Container läuft:**

```bash
sudo docker ps
```

---

## 2 MySQL/MariaDB-Client installieren (falls nötig)

```bash
sudo apt install mysql-client
```

*(Je nach Distribution kann das Paket auch `mariadb-client` heißen.)*

---

## 3⃣ Mit der Datenbank verbinden (als root)

```bash
mysql -h 127.0.0.1 -P 3306 -u root -pwebshopdb-root-pw
```

>  Tipp: Zwischen `-p` und dem Passwort darf **kein Leerzeichen** stehen — das ist kein Tippfehler im Original.

---

## 4 Datenbank und Webshop-User anlegen

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

>  **Sicherheitshinweis:** `webshopuser@'%'` ist für lokale Entwicklung okay, in einer produktiven Umgebung solltest du den Host einschränken (z. B. `'webshopuser'@'10.0.0.%'`) und ein starkes, nicht im Klartext dokumentiertes Passwort verwenden.

---

## 5 Mit dem neuen User verbinden

Ab jetzt mit `webshopuser` statt `root` arbeiten:

```bash
mysql -h 127.0.0.1 -P 3306 -u webshopuser -pwebshoppassword webshopdb
```

---

## 6 Tabelle `user` anlegen

```sql
CREATE TABLE user (
  email nvarchar(100) NOT NULL,
  username nvarchar(100) NOT NULL,
  password nvarchar(100) NOT NULL,
  PRIMARY KEY (email)
);
```

| Spalte | Typ | Beschreibung |
|---|---|---|
| `email` | `nvarchar(100)` | Primärschlüssel, eindeutige Kennung des Users |
| `username` | `nvarchar(100)` | Anzeigename |
| `password` | `nvarchar(100)` | Passwort |

>  **Wichtiger Hinweis für später:** Passwörter sollten in einem echten Webshop **niemals im Klartext** gespeichert werden, sondern gehasht (z. B. mit bcrypt/argon2). Für dieses Testsetup ist Klartext okay, für Produktivbetrieb nicht.
>
> Zusätzlich: `nvarchar` ist eigentlich ein MS-SQL-Typ; MariaDB kennt ihn zwar als Alias für `varchar`, technisch "sauberer" wäre `VARCHAR(100)`.

**Testdaten einfügen:**

```sql
INSERT INTO user VALUES
  ('steve.rogers@microsoft.com', 'Steve Rogers', 'steve'),
  ('t.stark@industries.com', 'Tony Stark', 'tony'),
  ('cd@amazon.com', 'Carol Danvers', 'carol');
```

---

## 7 Tabelle `artikel` anlegen

```sql
CREATE TABLE IF NOT EXISTS artikel (
    artikelId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    preis DECIMAL(10, 2) NOT NULL,
    bild VARCHAR(500)
);
```

| Spalte | Typ | Beschreibung |
|---|---|---|
| `artikelId` | `INT AUTO_INCREMENT` | Eindeutige, automatisch generierte ID |
| `name` | `VARCHAR(255)` | Produktname |
| `preis` | `DECIMAL(10,2)` | Preis mit 2 Nachkommastellen (korrekt für Geldbeträge, im Gegensatz zu `FLOAT`) |
| `bild` | `VARCHAR(500)` | URL zum Produktbild |

**Produkte einfügen:**

```sql
INSERT INTO artikel (artikelId, name, preis, bild) VALUES
(1, 'iPhone 15 Pro', 1199.00, 'https://imgs.search.brave.com/XKzj-Ry1DHNPSKMfAu3qWuKp_PdZCmUA9_yPjrLtfP8/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9zczcu/dnp3LmNvbS9pcy9p/bWFnZS9WZXJpem9u/V2lyZWxlc3MvYXBw/bGUtaXBob25lLTE1/LXByby0xdGItbmF0/dXJhbC10aXRhbml1/bS1tdHU1M2xsLWEt/YT93aWQ9NDAwJmhl/aT00MDAmZm10PXdl/YnAtYWxwaGE'),
(2, 'Samsung Galaxy S24', 899.90, 'https://imgs.search.brave.com/BkadkX__5a26LuCKGPBUVS5kY4cRhKoh2dXmvCeXYgk/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tLWNk/bi5waG9uZWFyZW5h/LmNvbS9pbWFnZXMv/cGhvbmVzLzg0Mzg5/LTM1MC9TYW1zdW5n/LUdhbGF4eS1TMjQu/d2VicD93PTE'),
(3, 'MacBook Air M3', 1299.00, 'https://imgs.search.brave.com/oLakeowrB4SYM_w-OwZtOz1sgZ0rQlQdqIA4pcgJ5XY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/bW9zLmNtcy5mdXR1/cmVjZG4ubmV0L2l4/S3FkbUdvY3lqUm80/OWE5VGk2a2MuanBn'),
(4, 'Sony WH-1000XM5 Kopfhörer', 349.00, 'https://imgs.search.brave.com/TF1Xaz1hrrvM-SwfAeWQBMxZmyACkFpkvsBDrjPwLA8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWFn/ZS5jb29sYmx1ZS5k/ZS9tYXgvNzAweGF1/dG8vcHJvZHVjdHMv/MTc1NTc1OQ'),
(5, 'iPad Air', 699.00, 'https://imgs.search.brave.com/Vco2VWHR0elSR7DgrcuCrQdoLnrnJsFJ0kyKmpqMn78/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/bW9zLmNtcy5mdXR1/cmVjZG4ubmV0L2tV/RTNRMm1weThhbW9j/Vlp2b2NkVWEtMzIw/LTgwLmpwZw'),
(6, 'PlayStation 5', 499.00, 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=500&auto=format&fit=crop'),
(7, 'Dell XPS 13 Laptop', 1399.50, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=500&auto=format&fit=crop'),
(8, 'Apple Watch Series 9', 429.00, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500&auto=format&fit=crop'),
(9, 'LG OLED TV 55 Zoll', 1299.90, 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=500&auto=format&fit=crop'),
(10, 'Logitech MX Master 3S Maus', 109.90, 'https://imgs.search.brave.com/u4KQhRP6KF4HT7OVjZBlf0ZnK6jHa3omFvYlzpjfK9E/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL1JmTUFB/ZVN3SUNWcDU1ZGIv/cy1sMjI1LmpwZw');
```



## 8 Ergebnis prüfen

```sql
SELECT * FROM user;
SELECT * FROM artikel;
```

---

## 9 Tabelle Adresse einfügen **

| Spalte | Typ | Beschreibung |
|---|---|---|
```sql
| `adressId` | `INT AUTO_INCREMENT PRIMARY KEY` | Eindeutige, automatisch generierte ID |
| `userEmail` | `VARCHAR(255) NOT NULL` | Email Adresse des Users |
| `vorname` | `VARCHAR(255) NOT NULL` | Vorname des Kundens |
| `nachname` | `VARCHAR(255) NOT NULL` | Nachname des Kundens |
| `strasse` | `VARCHAR(255) NOt NULL` | Strassenname des Kunden|
| `plz` | `VARCHAR(10) NOT NULL` | Postleitzahl des Kunde|
| `ort` | `VARCHAR(255) NOT NULL` |Ort des Kunden|
| `land` | `VARCHAR(100) NOT NULL DEFAULT 'Schweiz'` | Land vom Kunde|
| `createdAt` | `TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`; | Wann die Adresse eingetragen wurde|
```
##  Kurz-Checkliste

- [ ] Container `mariadb-container` läuft (`docker ps`)
- [ ] Verbindung als `root` funktioniert
- [ ] Datenbank `webshopdb` und User `webshopuser` angelegt
- [ ] Tabelle `user` erstellt und mit Testdaten befüllt
- [ ] Tabelle `artikel` erstellt und mit 10 Produkten befüllt
- [ ] Verbindung als `webshopuser` funktioniert

---

# Webshop starten und aufrufen (Backend + UI)

Der Webshop besteht aus **zwei getrennten Projekten/Servern**. Beide müssen laufen, damit die Seite funktioniert:

| Server | Projekt | Port | Aufgabe |
|---|---|---|---|
| **Backend-Service** | `bi7-webshop-service` | `7070` | REST-API (Login, Artikel, Warenkorb, Adressen) – Daten aus **MariaDB** |
| **UI-Webserver** | `bi7-webshop-ui` | `8080` | Liefert die HTML-Seiten aus und bedient eigene API-Routen – eigene **H2-Datenbank** |

---

## 1 Backend starten (bi7-webshop-service)

**Voraussetzung:** Die MariaDB läuft (siehe Setup oben, Port `3306`, Datenbank `webshopdb`).

```bash
cd /home/ilian/bi7-webshop-service
mvn package                 # baut das ausführbare JAR
java -jar target/bi7-webshop-service-1.0-SNAPSHOT.jar
```

**Funktionstest:**

```bash
curl http://localhost:7070/            # erwartet: "Hello World"
curl http://localhost:7070/artikel     # erwartet: JSON-Liste der Artikel
curl http://localhost:7070/users       # erwartet: JSON-Liste der Benutzernamen
```

---

## 2 UI-Webserver starten (bi7-webshop-ui)

Am einfachsten startest du ihn wie bisher **in IntelliJ** (Run-Konfiguration mit Main-Klasse `ch.suva.bi7.webshop.BI7WebshopWebserver`).

>  **Wichtig:** Starte den Server immer aus dem Verzeichnis `bi7-webshop-ui` heraus. Die H2-Datenbank wird relativ zum Arbeitsverzeichnis gesucht: `./data/webshop-db`. Startest du woanders, findet er die Datenbank nicht.

**Funktionstest:**

```bash
curl http://localhost:8080/HTML/landingpage.html   # erwartet: HTTP 200, HTML-Code
curl http://localhost:8080/api/artikel             # erwartet: HTTP 200, JSON-Liste
```

---

## 3 Seite im Browser aufrufen

**Die Startseite ist NICHT `http://localhost:8080/`, sondern:**

>  **http://localhost:8080/HTML/landingpage.html**

| URL | Ergebnis |
|---|---|
| `http://localhost:8080/HTML/landingpage.html` | ✅ Startseite mit Artikeln |
| `http://localhost:8080/HTML/loginForm.html` | ✅ Login |
| `http://localhost:8080/HTML/registerForm.html` | ✅ Registrierung |
| `http://localhost:8080/HTML/warenkorb.html` | ✅ Warenkorb |
| `http://localhost:8080/HTML/checkOut.html` | ✅ Checkout |
| `http://localhost:8080/` | ❌ **404** – es gibt kein `index.html` im Webapp-Root |

---

## 4 Problem: `http://localhost:8080/` liefert 404

**Ursache:** Im UI-Projekt (`src/main/webapp/`) wurde die Datei `index.html` gelöscht (Commit `8a7dee8`). Der Server liefert daher an der Wurzel nichts aus.

**Lösung:** Eine Datei `src/main/webapp/index.html` anlegen, die auf die Landingpage weiterleitet:

```html
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="refresh" content="0; url=HTML/landingpage.html">
    <title>Webshop</title>
</head>
<body>
    <p>Weiterleitung zu <a href="HTML/landingpage.html">landingpage.html</a>…</p>
</body>
</html>
```

Danach greift `http://localhost:8080/` automatisch. Ein Neustart des UI-Servers ist für die HTML-Datei **nicht** nötig (statische Dateien werden live aus dem Verzeichnis gelesen).

---

## 5 Troubleshooting / Kurz-Checkliste

- [ ] Beide Server laufen: `ss -tlnp | grep -E ':7070|:8080'`
- [ ] `curl http://localhost:7070/` liefert `Hello World`
- [ ] `curl http://localhost:8080/HTML/landingpage.html` liefert HTTP 200
- [ ] MariaDB (3306) erreichbar – sonst antwortet das Backend mit 500
- [ ] H2-Datei vorhanden: `ls /home/ilian/bi7-webshop-ui/data/webshop-db.mv.db`
- [ ] Internet nötig: Bootstrap-CSS und Platzhalterbilder kommen von externen CDNs (`cdn.jsdelivr.net`, `via.placeholder.com`) – ohne Internet sieht die Seite ungestylt aus
- [ ] Nach **Java-Code-Änderungen** den jeweiligen Server **neu bauen und neu starten** (HTML/CSS/JS werden live übernommen, Java-Klassen nicht)
