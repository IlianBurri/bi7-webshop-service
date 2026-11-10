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

> **Achtung:** Das Backend läuft nur noch mit HTTPS und startet nicht ohne Zertifikat. Vor dem ersten Start musst du deshalb die Schritte unter [Backend mit SSL (HTTPS) starten](#backend-mit-ssl-https-starten) durchführen.

**Funktionstest:**

```bash
curl -k https://localhost:7070/        # erwartet: "Hello World"
curl -k https://localhost:7070/artikel # erwartet: JSON-Liste der Artikel
curl -k https://localhost:7070/users   # erwartet: JSON-Liste der Benutzernamen
```

*(`-k` ist nötig, weil curl einem selbst signierten Zertifikat sonst nicht vertraut.)*

### Backend als Docker-Image offline bauen

Hilfsskript zum Bauen und Exportieren des Docker-Images:

```bash
./build-docker-image-and-export.sh -o bi7-webshop-service_1.0.tar
```

Für Raspberry Pi 3 mit 32-bit OS (`arm/v7`) kannst du direkt die Plattform setzen:

```bash
./build-docker-image-and-export.sh -p linux/arm/v7 -t bi7-webshop-service:1.0 -o bi7-webshop-service_1.0.tar
```

Für `linux/amd64` entsprechend wie z.B. REHL 9 oder Ubuntu 22.04:

```bash
./build-docker-image-and-export.sh -p linux/amd64 -t bi7-webshop-service:1.0 -o bi7-webshop-service_1.0.tar
```

Beispiel für die Übertragung per `scp` von `192.168.1.30` nach `192.168.1.20`:

```bash
scp bi7-webshop-service_1.0.tar bi7@192.168.1.20:.
```

Auf dem Zielrechner importieren und starten:

```bash
# Ob 'docker' oder 'podman' verwendet wird, ist egal – die Befehle sind identisch.
sudo podman load -i bi7-webshop-service_1.0.tar
sudo podman run --name bi7-webshop-service --rm --network=host \
  -v "$HOME/webshop_ssl:/etc/webshop/ssl:ro,Z" \
  -e DB_HOST=127.0.0.1 \
  -e DB_PORT=3306 \
  -e DB_NAME=webshopdb \
  -e DB_USER=webshopuser \
  -e DB_PASSWORD=webshoppassword \
  -e APP_IS_DEV=false \
  bi7-webshop-service:1.0
```

---

## 2. UI-Webserver starten (bi7-webshop-ui)

Am einfachsten startest du ihn wie bisher **in IntelliJ** (Run-Konfiguration mit Main-Klasse `ch.suva.bi7.webshop.BI7WebshopWebserver`).

> **Wichtig:** Starte den Server immer aus dem Verzeichnis `bi7-webshop-ui` heraus. Die H2-Datenbank wird relativ zum Arbeitsverzeichnis gesucht: `./data/webshop-db`. Startest du woanders, findet er die Datenbank nicht.

**Funktionstest:**

```bash
curl -k https://localhost:8080/HTML/landingpage.html   # erwartet: HTTP 200, HTML-Code
curl -k https://localhost:8080/api/artikel             # erwartet: HTTP 200, JSON-Liste
```

---

## 3. Seite im Browser aufrufen

**Die Startseite ist:**

> **https://localhost:8080/HTML/landingpage.html** (oder `https://localhost:8080/` mit Weiterleitung, siehe Punkt 4)

| URL | Ergebnis |
|---|---|
| `https://localhost:8080/HTML/landingpage.html` | ✅ Startseite mit Artikeln |
| `https://localhost:8080/HTML/loginForm.html` | ✅ Login |
| `https://localhost:8080/HTML/registerForm.html` | ✅ Registrierung |
| `https://localhost:8080/HTML/warenkorb.html` | ✅ Warenkorb |
| `https://localhost:8080/HTML/checkOut.html` | ✅ Checkout |
---


## 4. Troubleshooting / Kurz-Checkliste

- [ ] Beide Server laufen: `ss -tlnp | grep -E ':7070|:8080'`
- [ ] `curl -k https://localhost:7070/` liefert `Hello World`
- [ ] `curl -k https://localhost:8080/HTML/landingpage.html` liefert HTTP 200
- [ ] MariaDB (3306) erreichbar – sonst antwortet das Backend mit 500
- [ ] H2-Datei vorhanden: `ls /home/ilian/bi7-webshop-ui/data/webshop-db.mv.db`
- [ ] Internet nötig: Bootstrap-CSS und Platzhalterbilder kommen von externen CDNs (`cdn.jsdelivr.net`, `via.placeholder.com`) – ohne Internet sieht die Seite ungestylt aus
- [ ] Nach **Java-Code-Änderungen** den jeweiligen Server **neu bauen und neu starten** (HTML/CSS/JS werden live übernommen, Java-Klassen nicht)

---

# Backend mit SSL (HTTPS) starten

Das Backend nimmt nur HTTPS-Verbindungen an, auf Port `7070`. Beim Start liest es ein Zertifikat und den passenden privaten Schlüssel im PEM-Format ein. Fehlen die Dateien, bricht der Start mit einer Fehlermeldung ab.

**Wo sucht das Backend die Dateien?**

| Einstellung | Umgebungsvariable | Standardwert (`application-dev.properties`) |
|---|---|---|
| `app.ssl.certPath` | `APP_SSL_CERT_PATH` | `/etc/webshop/ssl/cert.pem` |
| `app.ssl.keyPath` | `APP_SSL_KEY_PATH` | `/etc/webshop/ssl/key.pem` |
| `app.ssl.keyPassword` | `APP_SSL_KEY_PASSWORD` | *(leer: Schlüssel ohne Passwort)* |

Die Umgebungsvariable hat Vorrang vor der Datei.

Du musst **keinen zentralen Ordner** wie `/etc/webshop/ssl` anlegen und brauchst dafür auch kein `sudo`. Die Dateien liegen in deinem Home-Verzeichnis unter `~/webshop_ssl`:

- **Im Container** wird `~/webshop_ssl` beim Start nach `/etc/webshop/ssl` eingebunden. Diesen Pfad gibt es nur im Container, auf dem Host wird dort nichts angelegt.
- **Ohne Container** (`java -jar`) zeigst du mit den Umgebungsvariablen direkt auf `~/webshop_ssl`.

---

## 1. Ordner anlegen

Auf dem Rechner, auf dem das Backend läuft (z.B. `192.168.1.20`, User `bi7`):

```bash
mkdir -p ~/webshop_ssl
chmod 700 ~/webshop_ssl
```

`chmod 700` sorgt dafür, dass nur du den Ordner (und damit den privaten Schlüssel) lesen kannst.

---

## 2. Zertifikat erstellen (selbst signiert)

Für Entwicklung und Test reicht ein selbst signiertes Zertifikat:

```bash
cd ~/webshop_ssl
openssl req -x509 -newkey rsa:2048 -nodes -sha256 -days 365 \
  -keyout key.pem -out cert.pem \
  -subj "/CN=bi7-webshop" \
  -addext "subjectAltName=IP:192.168.1.20,IP:127.0.0.1,DNS:localhost"
chmod 600 key.pem
chmod 644 cert.pem
```

| Teil | Bedeutung |
|---|---|
| `-x509` | erstellt direkt ein fertiges Zertifikat statt nur einer Zertifikatsanfrage (CSR) |
| `-newkey rsa:2048` | erzeugt gleichzeitig einen neuen RSA-Schlüssel mit 2048 Bit |
| `-nodes` | speichert den Schlüssel **ohne Passwort**. Sonst musst du `APP_SSL_KEY_PASSWORD` setzen. |
| `-days 365` | Das Zertifikat ist ein Jahr gültig. |
| `-subj "/CN=..."` | Name im Zertifikat. Ohne diese Angabe fragt `openssl` interaktiv nach. |
| `-addext "subjectAltName=..."` | **Wichtig:** Alle Adressen, unter denen der Browser das Backend aufruft. Der Browser vergleicht die Adresse in der URL mit diesem Eintrag. Läuft das Backend auf einer anderen IP, musst du sie hier anpassen. |

**Prüfen:**

```bash
ls -l ~/webshop_ssl                                         # cert.pem und key.pem vorhanden
openssl x509 -in ~/webshop_ssl/cert.pem -noout -subject -enddate -ext subjectAltName
```

> Hast du ein **echtes Zertifikat** (z.B. von der Firma oder von Let's Encrypt), kopierst du es als `cert.pem` und den Schlüssel als `key.pem` in `~/webshop_ssl` und lässt Schritt 2 weg. Ist der Schlüssel mit einem Passwort geschützt, gibst du es beim Start mit `-e APP_SSL_KEY_PASSWORD=...` mit.

---

## 3a. Backend im Container starten (podman/docker)

Mit `-v` bindest du den lokalen Ordner in den Container ein:

```bash
sudo podman run --name bi7-webshop-service --rm --network=host \
  -v "$HOME/webshop_ssl:/etc/webshop/ssl:ro,Z" \
  -e DB_HOST=192.168.1.10 \
  -e DB_PORT=3306 \
  -e DB_NAME=webshopdb \
  -e DB_USER=webshopuser \
  -e DB_PASSWORD=webshoppassword \
  -e APP_IS_DEV=false \
  bi7-webshop-service:1.0
```

| Teil von `-v` | Bedeutung |
|---|---|
| `$HOME/webshop_ssl` | dein lokaler Ordner auf dem Host. Verwende `$HOME` statt `~`, weil die Shell `~` nicht in jeder Position durch den Home-Pfad ersetzt. |
| `/etc/webshop/ssl` | Hier sieht das Backend den Ordner **im Container**. Das ist der Standardpfad, darum ist kein `APP_SSL_CERT_PATH` nötig. |
| `ro` | read-only: Der Container kann die Dateien nur lesen. |
| `Z` | nur bei SELinux (z.B. RHEL) nötig, damit der Container den Ordner lesen darf. Auf Ubuntu schadet die Angabe nicht. |

> `$HOME` wird von deiner eigenen Shell ersetzt, **bevor** `sudo` läuft. Darum zeigt der Pfad auch mit `sudo` auf `/home/<dein-user>/webshop_ssl`.

---

## 3b. Backend ohne Container starten (`java -jar` / IntelliJ)

Hier zeigst du mit Umgebungsvariablen direkt auf den lokalen Ordner:

```bash
export APP_SSL_CERT_PATH="$HOME/webshop_ssl/cert.pem"
export APP_SSL_KEY_PATH="$HOME/webshop_ssl/key.pem"
java -jar target/bi7-webshop-service-1.0-SNAPSHOT.jar
```

In IntelliJ trägst du die beiden Variablen in der Run-Konfiguration unter *Environment variables* ein. Den vollen Pfad musst du ausschreiben, z.B. `/home/ilian/webshop_ssl/cert.pem`, weil IntelliJ `~` und `$HOME` dort nicht ersetzt.

---

## 4. Prüfen, ob es läuft

Im Start-Log stehen die verwendeten Pfade:

```
app.ssl.certPath          = /etc/webshop/ssl/cert.pem (...)
app.ssl.keyPath           = /etc/webshop/ssl/key.pem (...)
```

Danach testest du die Verbindung:

```bash
curl -k https://localhost:7070/            # erwartet: "Hello World"
curl -v -k https://localhost:7070/ 2>&1 | grep -E "subject|expire"   # zeigt das verwendete Zertifikat
```

---

## 5. Zertifikat im Browser einmal akzeptieren

Ein selbst signiertes Zertifikat kennt der Browser nicht. Bei direkten Aufrufen fragt er nach. Ruft aber die Webshop-Seite das Backend im Hintergrund auf (`fetch`), schlägt der Aufruf **ohne Meldung** fehl. Die Artikel bleiben dann leer, und in der Konsole (F12) steht z.B. `net::ERR_CERT_AUTHORITY_INVALID`.

Deshalb einmal pro Browser:

1. `https://192.168.1.20:7070/` direkt im Browser öffnen
2. *Erweitert* → *Weiter zu 192.168.1.20 (unsicher)* klicken
3. Es erscheint `Hello World`. Danach funktioniert der Webshop normal.

Dasselbe gilt für die UI unter `https://192.168.1.30:8080/`, falls sie ebenfalls ein selbst signiertes Zertifikat verwendet.

---

## Troubleshooting SSL

| Fehlermeldung / Symptom | Ursache | Lösung |
|---|---|---|
| `NoSuchFileException: /etc/webshop/ssl/cert.pem` | Der Ordner ist nicht eingebunden, oder die Datei heisst anders. | `-v "$HOME/webshop_ssl:/etc/webshop/ssl:ro,Z"` prüfen, `ls ~/webshop_ssl` |
| `Konfigurationskey 'app.ssl.certPath' fehlt` | Pfad weder in der Datei noch als Umgebungsvariable gesetzt | `APP_SSL_CERT_PATH` / `APP_SSL_KEY_PATH` setzen |
| `AccessDeniedException` beim Start | Rechte (oder SELinux) verhindern das Lesen. | `chmod 600 key.pem`, bei RHEL `:Z` an `-v` anhängen |
| Fehler beim Lesen des Schlüssels / Passwort | Der Schlüssel ist verschlüsselt. | `-e APP_SSL_KEY_PASSWORD=...` mitgeben oder den Schlüssel mit `-nodes` neu erstellen |
| Browser: `ERR_CERT_COMMON_NAME_INVALID` | Die IP in der URL steht nicht im `subjectAltName`. | Zertifikat mit der richtigen IP neu erstellen (Schritt 2) |
| Webshop zeigt keine Artikel, Konsole: `ERR_CERT_AUTHORITY_INVALID` | Zertifikat im Browser noch nicht akzeptiert | Schritt 5 |
| Zertifikat abgelaufen | `-days 365` ist vorbei. | Schritt 2 wiederholen und das Backend neu starten |
