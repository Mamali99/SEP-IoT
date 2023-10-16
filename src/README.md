# Software Engineering
Das Git Projekt enthält alle nötigen grundlegenden Dateien, um die Aufgaben zu lösen.

## Requirements
- Java 17
- IntelliJ

## Setup Lokaler Rechner
- Mittels git clone \<repolink\> lokal herunterladen.
- neueste IntelliJ Version herunterladen
- Projekt in IntelliJ öffnen
- IntelliJ schlägt automatisch beim Öffnen des Projekts die Plugins Aqua, Spring Websocket und Vaadin Endpoints vor. Diese bitte installieren (Pop Up erscheint unten links)
- <img src="/doc-images/plugins.png" width="400px">
- Dependencies herunterladen (geschieht automatisch, kann 2 min dauern)
- <img src="/doc-images/build.png" width="400px">
- in das VPN einwählen
- src/main/java/de/ostfalia/application/Application.java starten oder auf den Play Button oben rechts drücken (ggf Projekt vorher builden. siehe FAQ)
- nach Starten schlägt IntelliJ vor weitere Dependencys mit "npm install" heruterzuladen \(mit Button bestätigen)</br>
- <img src="/doc-images/build.png" width="400px">
- Server mit der Anwendung läuft unter dem Tab Services

## Setup auf dem Raspberry PI
### Erstes Aufsetzen

- ins VPN einwählen
- ssh seuser@ipadresse
- einloggen mit passwort "youshouldchangeme"
- sudo apt install maven
- git clone \<pfad\>
- cd /pfad/in/das/oberste/verzeichnis
- mvn spring-boot:run
  - muss im oberstem verzeichnnis gestartet werden, pom.xml ist im selben Verzeichnis 
  - download der abhängigkeiten startet automatisch
- anwendung läuft auf Port 8081
  - falls nicht in der src/main/ressources/application.properties schauen
  - der Port ist mit server.port=${PORT:8081} angegeben

### Neue Änderungen herunterladen und starten
- mit ssh auf den PI einwählen (siehe oben)
- git pull origin main \(oder master\)
- curl -X POST localhost:8081/actuator/shutdown
  - beendet die laufende, lokale Anwendung
- sudo mvn spring-boot:run
  - erneut builden und starten

## Projekt Struktur
- application/views: Enthält alle Frontend Oberflächen
- application/data: Alle Logik Klassen liegen in diesem Verzeichnis
- application/data/entity: Klassen, die Entitäten der Datenbanktabellen abbilden
- application/data/repository: hier liegen nur Interfaces, die sich um das herauslesen
  der Daten aus der Datenbank kümmern
- application/data/service: hier liegen die Services, die sich um die Logik bei der
  Datenverwaltung kümmern

## Nützliche Links
- Vaadin Dokumentation: https://vaadin.com/docs/latest/overview
- Queries mit JPQl erstellen: https://www.bezkoder.com/spring-jpa-query/
- Datenset mit JPQL limitieren: https://www.bezkoder.com/spring-data-pageable-custom-query/
- SO Charts Plug In:https://vaadin.com/directory/component/so-charts
- SO Charts Beispiele: https://storedobject.com/examples/?login=AUTO

## FAQ
### Der Run Button ist in IntelliJ ausgegraut und ich kann die Anwendung nicht starten.
Das Projekt neu builden (obere Leiste, Hammer Button oder Strg+9), 1-2 Min warten und dann den Run Button erneut
drücken.
### Ich bekomme eine CannotCreateTransactionException auf der Konsole. Woran liegt das?
Die Verbindung zum VPN wurde nicht hergestellt oder ist abgebrochen. An dieser Stelle entweder mit dem VPN
verbinden oder bei bestehender Verbindung die Verbindung trennen und erneut verbinden.

### Bei IntelliJ werden mir Dateien in Gelb angezeigt.
Diese Dateien liegen in der .gitignore und werden nicht versioniert.
