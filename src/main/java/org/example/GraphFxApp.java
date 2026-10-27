package org.example;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphFxApp extends Application {

    private static final int SIZE = 10;

    private final TextField[][] felder = new TextField[SIZE][SIZE];
    private final Label infoLabel = new Label("Radius: -- | Durchmesser: -- | Zentrum: -- | Komponenten: --");
    private final Label knotenInfoLabel = new Label("Knoteninfo: Kein Knoten ausgewählt");
    private final TextArea ausgabeArea = new TextArea();
    private final TextArea logArea = new TextArea("Bereit.");
    private final Pane graphPane = new Pane();
    private final ScrollPane graphScroll = new ScrollPane();

    private final List<Circle> knotenKreise = new ArrayList<>();
    private int[][] aktuelleMatrix = new int[SIZE][SIZE];
    private Circle ausgewaehlterKreis = null;

    @Override
    public void start(Stage stage) {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setPrefWidth(520);

        Label titel = new Label("Graphenanalyse mit JavaFX");
        titel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label hinweis = new Label("0 = keine Kante, positive Zahl = Gewicht");
        hinweis.setStyle("-fx-font-size: 12px;");

        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                TextField tf = new TextField("0");
                tf.setPrefWidth(40);
                tf.setAlignment(Pos.CENTER);
                felder[i][j] = tf;
                grid.add(tf, j, i);
            }
        }

        Button ladenBtn     = new Button("CSV laden");
        Button analyseBtn   = new Button("Analyse starten");
        Button beispiel1Btn = new Button("Beispiel 1");
        Button beispiel2Btn = new Button("Beispiel 2");
        Button eulerBtn     = new Button("Euler-Kreis");
        Button leerenBtn    = new Button("Leeren");

        ladenBtn.setOnAction(e -> ladeCsv(stage));
        analyseBtn.setOnAction(e -> starteAnalyse());
        beispiel1Btn.setOnAction(e -> ladeBeispiel1());
        beispiel2Btn.setOnAction(e -> ladeBeispiel2());
        eulerBtn.setOnAction(e -> ladeEulerBeispiel());
        leerenBtn.setOnAction(e -> leereMatrix());

        HBox buttons = new HBox(10, ladenBtn, analyseBtn, beispiel1Btn, beispiel2Btn, eulerBtn, leerenBtn);
        buttons.setAlignment(Pos.CENTER);

        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(480);

        knotenInfoLabel.setWrapText(true);
        knotenInfoLabel.setMaxWidth(480);
        knotenInfoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1f4e79;");

        ausgabeArea.setEditable(false);
        ausgabeArea.setPrefRowCount(14);
        ausgabeArea.setPrefWidth(480);

        logArea.setEditable(false);
        logArea.setPrefRowCount(3);
        logArea.setPrefWidth(480);

        leftPanel.getChildren().addAll(
                titel, hinweis, grid, buttons,
                infoLabel, knotenInfoLabel, ausgabeArea, logArea
        );

        graphPane.setPrefSize(950, 950);
        graphPane.setMinSize(950, 950);
        graphPane.setStyle("-fx-background-color: white; -fx-border-color: black;");

        graphScroll.setContent(graphPane);
        graphScroll.setPannable(true);
        graphScroll.setFitToWidth(false);
        graphScroll.setFitToHeight(false);
        graphScroll.setPrefViewportWidth(680);
        graphScroll.setPrefViewportHeight(780);
        graphScroll.setStyle("-fx-background: white; -fx-border-color: #999;");

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setAlignment(Pos.TOP_CENTER);

        Label graphTitel = new Label("Grafische Darstellung");
        graphTitel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        rightPanel.getChildren().addAll(graphTitel, graphScroll);
        VBox.setVgrow(graphScroll, Priority.ALWAYS);

        HBox root = new HBox(15, leftPanel, rightPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 1280, 880);
        stage.setTitle("Graphenanalyse");
        stage.setScene(scene);
        stage.show();
    }

    private void starteAnalyse() {
        try {
            int[][] matrix = leseMatrix();
            pruefeMatrix(matrix);
            aktuelleMatrix = kopiereMatrix(matrix);

            int[][] dist = GraphLogic.berechneDistanzen(matrix);
            int[] ex = GraphLogic.berechneExzentrizitaeten(dist);

            int radius = GraphLogic.berechneRadius(ex);
            int durchmesser = GraphLogic.berechneDurchmesser(ex);
            List<Integer> zentrum = GraphLogic.berechneZentrum(ex, radius);
            int komponenten = GraphLogic.zaehleKomponenten(matrix);
            List<String> bruecken = GraphLogic.findeBruecken(GraphLogic.kopiereMatrix(matrix));
            List<Integer> artikulationen = GraphLogic.findeArtikulationen(matrix);

            infoLabel.setText(
                    "Radius: " + radius +
                            " | Durchmesser: " + durchmesser +
                            " | Zentrum: " + zentrum +
                            " | Komponenten: " + komponenten
            );

            StringBuilder text = new StringBuilder();
            text.append("Exzentrizitäten:\n");
            for (int i = 0; i < ex.length; i++) {
                text.append("Knoten ").append(i).append(": ").append(ex[i]).append("\n");
            }

            text.append("\nBrücken: ").append(bruecken);
            text.append("\nArtikulationen: ").append(artikulationen);
            text.append("\nEuler: ").append(GraphLogic.pruefeEuler(matrix)).append("\n");
            text.append(GraphLogic.findeEulerPfad(matrix)).append("\n");
            text.append("\nDistanzmatrix:\n");
            text.append(matrixZuText(dist));

            ausgabeArea.setText(text.toString());
            zeichneGraph(matrix);
            zentriereGraphAnsicht();
            knotenInfoLabel.setText("Knoteninfo: Kein Knoten ausgewählt");
            logArea.setText("Analyse erfolgreich durchgeführt.");

        } catch (NumberFormatException e) {
            logArea.setText("Fehler: Bitte nur ganze Zahlen eingeben.");
        } catch (IllegalArgumentException e) {
            logArea.setText("Fehler: " + e.getMessage());
        }
    }

    // Einfacher Ring: jeder Knoten hat Grad 2 → Euler-Kreis garantiert
    private void ladeEulerBeispiel() {
        leereMatrix();

        int[][] beispiel = {
                {0,1,0,0,0,0,0,0,0,1},
                {1,0,1,0,0,0,0,0,0,0},
                {0,1,0,1,0,0,0,0,0,0},
                {0,0,1,0,1,0,0,0,0,0},
                {0,0,0,1,0,1,0,0,0,0},
                {0,0,0,0,1,0,1,0,0,0},
                {0,0,0,0,0,1,0,1,0,0},
                {0,0,0,0,0,0,1,0,1,0},
                {0,0,0,0,0,0,0,1,0,1},
                {1,0,0,0,0,0,0,0,1,0}
        };

        setzeMatrix(beispiel);
        aktuelleMatrix = kopiereMatrix(beispiel);
        zeichneGraph(beispiel);
        zentriereGraphAnsicht();
        logArea.setText("Euler-Kreis Beispiel geladen.");
    }

    private int[][] leseMatrix() {
        int[][] matrix = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                matrix[i][j] = Integer.parseInt(felder[i][j].getText().trim());
        return matrix;
    }

    private void pruefeMatrix(int[][] matrix) {
        for (int i = 0; i < SIZE; i++) {
            if (matrix[i][i] != 0)
                throw new IllegalArgumentException("Auf der Diagonale muss 0 stehen.");
            for (int j = 0; j < SIZE; j++) {
                if (matrix[i][j] < 0)
                    throw new IllegalArgumentException("Negative Werte sind nicht erlaubt.");
                if (matrix[i][j] != matrix[j][i])
                    throw new IllegalArgumentException("Die Matrix muss symmetrisch sein.");
            }
        }
    }

    private void ladeCsv(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("CSV-Datei auswählen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Dateien", "*.csv"));
        File file = chooser.showOpenDialog(stage);

        if (file == null) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            leereMatrix();
            String line;
            int zeile = 0;
            while ((line = br.readLine()) != null && zeile < SIZE) {
                if (line.trim().isEmpty()) continue;
                String[] teile = line.split(",");
                for (int spalte = 0; spalte < teile.length && spalte < SIZE; spalte++)
                    felder[zeile][spalte].setText(teile[spalte].trim());
                zeile++;
            }
            logArea.setText("CSV erfolgreich geladen: " + file.getName());
        } catch (IOException e) {
            logArea.setText("Fehler beim Lesen der Datei.");
        }
    }

    private void ladeBeispiel1() {
        leereMatrix();
        int[][] beispiel = {
                {0,2,1,0,0,0,0,0,0,0},
                {2,0,3,1,0,0,0,0,0,0},
                {1,3,0,0,2,0,0,0,0,0},
                {0,1,0,0,1,2,0,0,0,0},
                {0,0,2,1,0,0,1,0,0,0},
                {0,0,0,2,0,0,2,1,0,0},
                {0,0,0,0,1,2,0,1,0,0},
                {0,0,0,0,0,1,1,0,2,0},
                {0,0,0,0,0,0,0,2,0,1},
                {0,0,0,0,0,0,0,0,1,0}
        };
        setzeMatrix(beispiel);
        aktuelleMatrix = kopiereMatrix(beispiel);
        zeichneGraph(beispiel);
        zentriereGraphAnsicht();
        logArea.setText("Beispielmatrix 1 geladen.");
    }

    private void ladeBeispiel2() {
        leereMatrix();
        int[][] beispiel = {
                {0,1,1,1,0,0,0,0,0,0},
                {1,0,0,0,2,0,0,0,0,0},
                {1,0,0,0,0,3,0,0,0,0},
                {1,0,0,0,0,0,4,0,0,0},
                {0,2,0,0,0,1,0,0,0,0},
                {0,0,3,0,1,0,1,0,0,0},
                {0,0,0,4,0,1,0,2,0,0},
                {0,0,0,0,0,0,2,0,2,1},
                {0,0,0,0,0,0,0,2,0,3},
                {0,0,0,0,0,0,0,1,3,0}
        };
        setzeMatrix(beispiel);
        aktuelleMatrix = kopiereMatrix(beispiel);
        zeichneGraph(beispiel);
        zentriereGraphAnsicht();
        logArea.setText("Beispielmatrix 2 geladen.");
    }

    private void setzeMatrix(int[][] matrix) {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                felder[i][j].setText(String.valueOf(matrix[i][j]));
    }

    private void leereMatrix() {
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                felder[i][j].setText("0");
        ausgabeArea.clear();
        graphPane.getChildren().clear();
        knotenKreise.clear();
        ausgewaehlterKreis = null;
        infoLabel.setText("Radius: -- | Durchmesser: -- | Zentrum: -- | Komponenten: --");
        knotenInfoLabel.setText("Knoteninfo: Kein Knoten ausgewählt");
        logArea.setText("Matrix wurde geleert.");
    }

    private String matrixZuText(int[][] matrix) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] >= GraphLogic.INF) text.append("∞ ");
                else text.append(matrix[i][j]).append(" ");
            }
            text.append("\n");
        }
        return text.toString();
    }

    private void zeichneGraph(int[][] matrix) {
        graphPane.getChildren().clear();
        knotenKreise.clear();
        ausgewaehlterKreis = null;

        int n = matrix.length;
        double mitteX = 475;
        double mitteY = 475;
        double radiusKreis = 320;

        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            double winkel = 2 * Math.PI * i / n - Math.PI / 2;
            x[i] = mitteX + radiusKreis * Math.cos(winkel);
            y[i] = mitteY + radiusKreis * Math.sin(winkel);
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (matrix[i][j] > 0) {
                    Line linie = new Line(x[i], y[i], x[j], y[j]);
                    linie.setStroke(Color.BLACK);
                    linie.setStrokeWidth(1.5);
                    graphPane.getChildren().add(linie);

                    Label gewicht = new Label(String.valueOf(matrix[i][j]));
                    gewicht.setLayoutX((x[i] + x[j]) / 2 - 8);
                    gewicht.setLayoutY((y[i] + y[j]) / 2 - 8);
                    gewicht.setStyle("-fx-background-color: white; -fx-padding: 2px; -fx-font-size: 11px;");
                    graphPane.getChildren().add(gewicht);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            Circle kreis = new Circle(x[i], y[i], 24);
            kreis.setFill(Color.LIGHTBLUE);
            kreis.setStroke(Color.BLACK);
            kreis.setStrokeWidth(1.5);

            final int knotenIndex = i;
            kreis.setOnMouseClicked(event -> markiereKnoten(kreis, knotenIndex));

            Label knoten = new Label(String.valueOf(i));
            knoten.setLayoutX(x[i] - 4);
            knoten.setLayoutY(y[i] - 8);
            knoten.setStyle("-fx-font-weight: bold;");
            knoten.setMouseTransparent(true);

            knotenKreise.add(kreis);
            graphPane.getChildren().addAll(kreis, knoten);
        }
    }

    private void markiereKnoten(Circle geklickterKreis, int index) {
        for (Circle kreis : knotenKreise) {
            kreis.setFill(Color.LIGHTBLUE);
            kreis.setScaleX(1.0);
            kreis.setScaleY(1.0);
        }

        ausgewaehlterKreis = geklickterKreis;
        ausgewaehlterKreis.setFill(Color.ORANGE);

        ScaleTransition st = new ScaleTransition(Duration.millis(180), ausgewaehlterKreis);
        st.setToX(1.25);
        st.setToY(1.25);
        st.play();

        List<Integer> nachbarn = findeNachbarn(index);
        knotenInfoLabel.setText("Knoteninfo: Knoten " + index + " ausgewählt | Nachbarn: " + nachbarn);
        logArea.setText("Knoten " + index + " wurde angeklickt.");
    }

    private List<Integer> findeNachbarn(int index) {
        List<Integer> nachbarn = new ArrayList<>();
        for (int i = 0; i < aktuelleMatrix.length; i++)
            if (aktuelleMatrix[index][i] > 0) nachbarn.add(i);
        return nachbarn;
    }

    private void zentriereGraphAnsicht() {
        graphScroll.setHvalue(0.5);
        graphScroll.setVvalue(0.5);
    }

    private int[][] kopiereMatrix(int[][] matrix) {
        int[][] kopie = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++)
            System.arraycopy(matrix[i], 0, kopie[i], 0, matrix.length);
        return kopie;
    }

    public static void main(String[] args) {
        launch(args);
    }
}