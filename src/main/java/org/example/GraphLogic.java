package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphLogic {

    public static final int INF = 1000000;

    public static int[][] berechneDistanzen(int[][] matrix) {
        int n = matrix.length;
        int[][] dist = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else if (matrix[i][j] > 0) {
                    dist[i][j] = matrix[i][j];
                } else {
                    dist[i][j] = INF;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        int neu = dist[i][k] + dist[k][j];
                        if (neu < dist[i][j]) {
                            dist[i][j] = neu;
                        }
                    }
                }
            }
        }

        return dist;
    }

    public static int[] berechneExzentrizitaeten(int[][] dist) {
        int n = dist.length;
        int[] ex = new int[n];

        for (int i = 0; i < n; i++) {
            int max = 0;
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < INF && dist[i][j] > max) {
                    max = dist[i][j];
                }
            }
            ex[i] = max;
        }

        return ex;
    }

    public static int berechneRadius(int[] ex) {
        int radius = Integer.MAX_VALUE;

        for (int wert : ex) {
            if (wert < radius) {
                radius = wert;
            }
        }

        return radius;
    }

    public static int berechneDurchmesser(int[] ex) {
        int durchmesser = 0;

        for (int wert : ex) {
            if (wert > durchmesser) {
                durchmesser = wert;
            }
        }

        return durchmesser;
    }

    public static List<Integer> berechneZentrum(int[] ex, int radius) {
        List<Integer> zentrum = new ArrayList<>();

        for (int i = 0; i < ex.length; i++) {
            if (ex[i] == radius) {
                zentrum.add(i);
            }
        }

        return zentrum;
    }

    public static int zaehleKomponenten(int[][] matrix) {
        int n = matrix.length;
        boolean[] besucht = new boolean[n];
        int anzahl = 0;

        for (int i = 0; i < n; i++) {
            if (!besucht[i]) {
                anzahl++;
                dfs(matrix, i, besucht);
            }
        }

        return anzahl;
    }

    private static void dfs(int[][] matrix, int start, boolean[] besucht) {
        besucht[start] = true;

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[start][i] > 0 && !besucht[i]) {
                dfs(matrix, i, besucht);
            }
        }
    }

    public static List<String> findeBruecken(int[][] matrix) {
        List<String> bruecken = new ArrayList<>();
        int n = matrix.length;
        int alt = zaehleKomponenten(matrix);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (matrix[i][j] > 0) {
                    int gewicht = matrix[i][j];

                    matrix[i][j] = 0;
                    matrix[j][i] = 0;

                    int neu = zaehleKomponenten(matrix);
                    if (neu > alt) {
                        bruecken.add(i + " - " + j);
                    }

                    matrix[i][j] = gewicht;
                    matrix[j][i] = gewicht;
                }
            }
        }

        return bruecken;
    }

    public static List<Integer> findeArtikulationen(int[][] matrix) {
        List<Integer> liste = new ArrayList<>();
        int n = matrix.length;
        int alt = zaehleKomponenten(matrix);

        for (int entfernt = 0; entfernt < n; entfernt++) {
            boolean[] besucht = new boolean[n];
            besucht[entfernt] = true;
            int komponenten = 0;

            for (int i = 0; i < n; i++) {
                if (!besucht[i]) {
                    komponenten++;
                    dfsOhneKnoten(matrix, i, besucht, entfernt);
                }
            }

            if (komponenten > alt) {
                liste.add(entfernt);
            }
        }

        return liste;
    }

    private static void dfsOhneKnoten(int[][] matrix, int start, boolean[] besucht, int entfernt) {
        besucht[start] = true;

        for (int i = 0; i < matrix.length; i++) {
            if (i != entfernt && matrix[start][i] > 0 && !besucht[i]) {
                dfsOhneKnoten(matrix, i, besucht, entfernt);
            }
        }
    }

    public static int[][] kopiereMatrix(int[][] matrix) {
        int[][] kopie = new int[matrix.length][matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, kopie[i], 0, matrix.length);
        }

        return kopie;
    }

    public static String pruefeEuler(int[][] matrix) {
        if (zaehleKomponenten(matrix) > 1) return "Kein Euler möglich";

        int ungerade = 0;
        for (int i = 0; i < matrix.length; i++) {
            int grad = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] > 0) grad++;
            }
            if (grad % 2 != 0) ungerade++;
        }

        if (ungerade == 0) return "Euler-Kreis existiert";
        if (ungerade == 2) return "Euler-Zug existiert";
        return "Kein Euler möglich";
    }
    public static String findeEulerPfad(int[][] matrix) {
        if (!pruefeEuler(matrix).equals("Euler-Kreis existiert")) return "";

        int n = matrix.length;
        int[][] kopie = kopiereMatrix(matrix);
        List<Integer> pfad = new ArrayList<>();
        Stack<Integer> stapel = new java.util.Stack<>();

        stapel.push(0);
        while (!stapel.isEmpty()) {
            int v = stapel.peek();
            boolean gefunden = false;
            for (int i = 0; i < n; i++) {
                if (kopie[v][i] > 0) {
                    stapel.push(i);
                    kopie[v][i] = 0;
                    kopie[i][v] = 0;
                    gefunden = true;
                    break;
                }
            }
            if (!gefunden) {
                pfad.add(stapel.pop());
            }
        }

        StringBuilder sb = new StringBuilder("Euler-Kreis: ");
        for (int i = pfad.size() - 1; i >= 0; i--) {
            sb.append(pfad.get(i));
            if (i > 0) sb.append(" → ");
        }
        return sb.toString();
    }
}