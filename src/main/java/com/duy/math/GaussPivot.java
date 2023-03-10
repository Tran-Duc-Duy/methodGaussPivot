package com.duy.math;
import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GaussPivot {
    public double getDeterminantOfMatrix(double coefficients[][], int lengthOfArray, int countChange)
    {
        double det = 1;
        for(int x = 0 ; x < lengthOfArray ; x++){
            det *=  coefficients[x][x];
        }
        det *= Math.pow(-1,countChange);
        if (Double.isNaN(det)) {
            return 0;
        }
        return det;

    }
    public double[] getNewDiscrepancy(double coefficients[][],double[] constants, double[] solution)
    {
        int lengthOfMatrix = coefficients.length;
        double[] dis = new double[lengthOfMatrix];

        for(int i = 0; i < lengthOfMatrix; i++){
            double r = constants[i];
            for(int j = 0; j < lengthOfMatrix; j++){
                r -= coefficients[i][j]*solution[j];
            }
            dis[i] = r;
        }
        return dis;
    }
    public void solve(double[][] coefficients, double[] constants)
    {
        int lengthOfArray = constants.length;
        int countChange=0;
        for (int k = 0; k < lengthOfArray; k++) {
            /** найти сводную строку **/
            int max = k;
            for (int i = k + 1; i < lengthOfArray; i++)
                if (Math.abs(coefficients[i][k]) > Math.abs(coefficients[max][k]))
                    max = i;

            /** поменять местами строку в матрице coefficients**/
            if (k != max) {
                double[] temp = coefficients[k];
                coefficients[k] = coefficients[max];
                coefficients[max] = temp;
                /** поменять местами соответствующие значения в матрице constants **/
                double t = constants[k];
                constants[k] = constants[max];
                constants[max] = t;
                countChange++;
            }
            /** сводная строка в пределах coefficients и constants **/
            for (int i = k + 1; i < lengthOfArray; i++) {
                double factor = coefficients[i][k] / coefficients[k][k];
                constants[i] -= factor * constants[k];
                for (int j = k; j < lengthOfArray; j++) {
                    coefficients[i][j] -= factor * coefficients[k][j];
                }
            }
        }

        /** Форма эшелона строки печати **/
        int check = printRowEchelonForm(coefficients, constants, countChange);
        if(check ==0){
            return;
        }
        /** обратная замена **/
        double[] solution = new double[lengthOfArray];
        for (int i = lengthOfArray - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < lengthOfArray; j++)
                sum += coefficients[i][j] * solution[j];
            solution[i] = (constants[i] - sum) / coefficients[i][i];
        }
        /** Распечатать решение**/
        printSolution(solution);

        System.out.println("Вектор невязки: ");
        double[] dis = getNewDiscrepancy(coefficients,constants, solution);
        for (double di : dis) System.out.printf("%.2f\s", (double) Math.round(di * 1000) / 1000);
        System.out.println();

    }
    /** функция для печати в форме эшелона строк **/
    public int printRowEchelonForm(double[][] coefficients, double[] constants, int countChange)
    {
        System.out.println();
        int lengthOfArray = constants.length;
        double tempD=getDeterminantOfMatrix(coefficients, lengthOfArray, countChange);
        System.out.println("Определитель матрицы равен: "+tempD);
        if(tempD==0){
            System.out.println("Определитель равен 0, нет решений");
            return 0;
        }
        System.out.println("\nРядно-эшелонная форма : ");
        for (int i = 0; i < lengthOfArray; i++)
        {
            for (int j = 0; j < lengthOfArray; j++)
                System.out.printf("%.3f ", coefficients[i][j]);
            System.out.printf("| %.3f\n", constants[i]);
        }
        System.out.println();
        return 1;
    }
    /** функция для печати СЛАУ **/
    public void printSolution(double[] solution)
    {
        int lengthOfArray = solution.length;
        System.out.println("\nНайдены корни СЛАУ : ");
        for (int i = 0; i < lengthOfArray; i++)
            System.out.printf("%.3f ", solution[i]);
        System.out.println();
    }


    /** Main функция **/
    public static void main (String[] args) {

        System.out.println("Gaussian Elimination Algorithm Test\n");
        /** Make an object of GaussianElimination class **/
        GaussPivot ge = new GaussPivot();


        int size = 0;
        ArrayList<Double> arrayList = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите способ ввода: 1 - с клавиатуры, 2 - из файла");
        int num = scanner.nextInt();
        while (!(num == 1 || num == 2)) {
            System.out.println("Ошибка ввода!");
            System.out.println("Выберите способ ввода: 1 - с клавиатуры, 2 - из файла");
            num = scanner.nextInt();
        }
        switch (num) {
            case 1 -> {
                System.out.println("Укажите размерносить матрицы: ");
                size = scanner.nextInt();

                if (size == 1)
                    System.out.println("Размерность СЛАУ не может быть равна одному");
                else if (size == 2) {
                    System.out.println("Формат ввода: 'a11 a12 = b1'");
                    System.out.println("Введите коффициенты через пробел:");
                } else {
                    System.out.println("Формат ввода: 'a11 ... a1" + size + " = b1'");
                    System.out.println("Введите коффициенты через пробел:");
                }

                try {
                    String ch = "";
                    Pattern p = Pattern.compile("[A-Za-zА-Яа-я!#$@_+?]");
                    scanner.nextLine();
                    for (int i = 0; i < size; i++) {
                        for (int k = 0; k < size+1; k++) {
                            System.out.print("arr["+i+"]["+k+"] = ");
                            ch = scanner.nextLine();
                            ch = ch.replaceAll(",+", ".");
                            int dot = 0;
                            Matcher m = p.matcher(ch);
                            for (int j = 0; j < ch.length(); j++) {
                                if (ch.charAt(j) == '.') {
                                    dot++;
                                }
                            }
                            if (dot > 1 || m.find()) {
                                System.out.println("Ошибка! Ещё раз");
                                k--;
                                continue;
                            }
                            Double db = Double.valueOf(ch);
                            System.out.println("получил -> " + db);
                            arrayList.add(db);
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка ввода!  Проверьте, что дробные числа записаны через запятую");
                }
            }
            case 2 -> {
                try {
                    FileInputStream path = new FileInputStream("20x20.txt");
                    DataInputStream inFile = new DataInputStream(path);
                    BufferedReader br = new BufferedReader(new InputStreamReader(inFile));
                    String data;

                    while ((data = br.readLine()) != null) {
                        data = data.replaceAll(",", ".");
                        String[] tmp = data.split(" ");    //Split space
                        for (String s : tmp)
                            arrayList.add(Double.parseDouble(s));
                        size++;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода!  Проверьте, что дробные числа записаны через точку");
                    System.exit(0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Размерность матрицы: ");
                System.out.println(size);
                System.out.println();
            }
        }

        double[][] coefficients = new double[size][size];
        double[] constants = new double[size];

        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                coefficients[i][j] = arrayList.get(index);
                index++;
            }
            constants[i] = arrayList.get(index);
            index++;
        }
        ge.solve(coefficients, constants);
    }
}
