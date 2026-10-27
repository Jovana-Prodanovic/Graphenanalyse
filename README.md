# Graphenanalyse

Java-Projekt zur Analyse ungerichteter, gewichteter Graphen auf Basis einer Adjazenzmatrix, mit grafischer Benutzeroberfläche in JavaFX.

## Funktionen
- Kürzeste Wege zwischen allen Knoten (Floyd-Warshall)
- Exzentrizitäten, Radius, Durchmesser und Zentrum des Graphen
- Zusammenhangskomponenten (Tiefensuche)
- Brücken und Artikulationen
- Prüfung auf Euler-Kreis bzw. Euler-Zug und Berechnung eines Euler-Kreises

## Technologien
- Java 17
- JavaFX 21
- Maven

## Aufbau
- `GraphLogic.java`: Algorithmen zur Graphenanalyse
- `GraphFxApp.java`: Grafische Benutzeroberfläche (JavaFX, deutschsprachig)
- `Main.java`: Startpunkt der Anwendung

## Starten
1. Repository klonen
2. Projekt in IntelliJ IDEA als Maven-Projekt öffnen
3. `Main.java` ausführen (oder `mvn javafx:run`)

## Hintergrund
Schulprojekt im Rahmen des Kollegs für Informatik, HTL Spengergasse.
