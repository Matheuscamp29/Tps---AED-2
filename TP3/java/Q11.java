import java.util.Scanner;

public class Q11 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Ler o número de casos de teste
        int numeroTestes = sc.nextInt();
        sc.nextLine();

        for (int t = 0; t < numeroTestes; t++) {
            // Ler a primeira matriz
            int linhas1 = Integer.parseInt(readNextNonEmptyLine(sc));
            int colunas1 = Integer.parseInt(readNextNonEmptyLine(sc));
            Matriz matriz1 = new Matriz(linhas1, colunas1);
            for (int i = 0; i < linhas1; i++) {
                String[] elementos = readNextNonEmptyLine(sc).split("\\s+");
                for (int j = 0; j < colunas1; j++) {
                    matriz1.setElemento(i, j, Integer.parseInt(elementos[j]));
                }
            }

            // Ler a segunda matriz
            int linhas2 = Integer.parseInt(readNextNonEmptyLine(sc));
            int colunas2 = Integer.parseInt(readNextNonEmptyLine(sc));
            Matriz matriz2 = new Matriz(linhas2, colunas2);
            for (int i = 0; i < linhas2; i++) {
                String[] elementos = readNextNonEmptyLine(sc).split("\\s+");
                for (int j = 0; j < colunas2; j++) {
                    matriz2.setElemento(i, j, Integer.parseInt(elementos[j]));
                }
            }

            // Exibir as diagonais da primeira matriz
            matriz1.mostrarDiagonalPrincipal();
            matriz1.mostrarDiagonalSecundaria();

            // Somar as duas matrizes
            Matriz soma = matriz1.soma(matriz2);
            if (soma != null) {
                soma.mostrarMatriz();
            }

            // Multiplicar as duas matrizes
            Matriz multiplicacao = matriz1.multiplicacao(matriz2);
            if (multiplicacao != null) {
                multiplicacao.mostrarMatriz();
            }
        }

        sc.close();
    }

    private static String readNextNonEmptyLine(Scanner sc) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return "";
    }
}

class Matriz {
    private int linhas;
    private int colunas;
    private int[][] dados;

    public Matriz(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        dados = new int[linhas][colunas];
    }

    public void setElemento(int linha, int coluna, int valor) {
        dados[linha][coluna] = valor;
    }

    // somar duas matrizes
    public Matriz soma(Matriz outra) {
        if (this.linhas != outra.linhas || this.colunas != outra.colunas) {
            return null;
        }
        Matriz resultado = new Matriz(this.linhas, this.colunas);
        for (int i = 0; i < this.linhas; i++) {
            for (int j = 0; j < this.colunas; j++) {
                resultado.dados[i][j] = this.dados[i][j] + outra.dados[i][j];
            }
        }
        return resultado;
    }

    // multiplicar duas matrizes
    public Matriz multiplicacao(Matriz outra) {
        if (this.colunas != outra.linhas) {
            return null;
        }
        Matriz resultado = new Matriz(this.linhas, outra.colunas);
        for (int i = 0; i < this.linhas; i++) {
            for (int j = 0; j < outra.colunas; j++) {
                resultado.dados[i][j] = 0;
                for (int k = 0; k < this.colunas; k++) {
                    resultado.dados[i][j] += this.dados[i][k] * outra.dados[k][j];
                }
            }
        }
        return resultado;
    }

    // exibir a diagonal principal
    public void mostrarDiagonalPrincipal() {
        StringBuilder sb = new StringBuilder();
        int tamanho = Math.min(linhas, colunas);
        for (int i = 0; i < tamanho; i++) {
            sb.append(dados[i][i]);
            if (i < tamanho - 1) {
                sb.append(" ");
            }
        }
        System.out.println(sb.toString());
    }

    // exibir a diagonal secundária
    public void mostrarDiagonalSecundaria() {
        StringBuilder sb = new StringBuilder();
        int tamanho = Math.min(linhas, colunas);
        for (int i = 0; i < tamanho; i++) {
            sb.append(dados[i][colunas - i - 1]);
            if (i < tamanho - 1) {
                sb.append(" ");
            }
        }
        System.out.println(sb.toString());
    }

    // exibir a matriz
    public void mostrarMatriz() {
        for (int i = 0; i < linhas; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < colunas; j++) {
                sb.append(dados[i][j]);
                if (j < colunas - 1) {
                    sb.append(" ");
                }
            }
            System.out.println(sb.toString());
        }
    }
}
