import java.util.Scanner;

public class html {

    public static boolean isFim(String palavra) {
        return palavra.equalsIgnoreCase("FIM");
    }

    public static int contarOcorrencias(String texto, String padrao) {
        return texto.length() - texto.replace(padrao, "").length();
    }

    public static int contarConsoantes(String texto) {
        String consoantes = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
        int count = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (consoantes.indexOf(texto.charAt(i)) != -1) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String endereco = scanner.nextLine();
            if (isFim(endereco)) {
                break;
            }

            // Segunda linha: nome da página
            String nomePagina = scanner.nextLine();

            // Contagem de vogais e suas variações
            int a = contarOcorrencias(nomePagina, "a");
            int e = contarOcorrencias(nomePagina, "e");
            int i = contarOcorrencias(nomePagina, "i");
            int o = contarOcorrencias(nomePagina, "o");
            int u = contarOcorrencias(nomePagina, "u");

            // Contagem de vogais acentuadas
            int acentuados[] = {
                contarOcorrencias(nomePagina, "á"), contarOcorrencias(nomePagina, "é"), contarOcorrencias(nomePagina, "í"),
                contarOcorrencias(nomePagina, "ó"), contarOcorrencias(nomePagina, "ú"), contarOcorrencias(nomePagina, "à"),
                contarOcorrencias(nomePagina, "è"), contarOcorrencias(nomePagina, "ì"), contarOcorrencias(nomePagina, "ò"),
                contarOcorrencias(nomePagina, "ù"), contarOcorrencias(nomePagina, "ã"), contarOcorrencias(nomePagina, "õ"),
                contarOcorrencias(nomePagina, "â"), contarOcorrencias(nomePagina, "ê"), contarOcorrencias(nomePagina, "î"),
                contarOcorrencias(nomePagina, "ô"), contarOcorrencias(nomePagina, "û")
            };

            // Contagem de consoantes
            int consoantes = contarConsoantes(nomePagina);
            
            // Contagem de padrões HTML
            int br = contarOcorrencias(nomePagina, "<br>");
            int table = contarOcorrencias(nomePagina, "<table>");

            // Exibindo o resultado no formato solicitado
            System.out.println(
                "a(" + a + ") e(" + e + ") i(" + i + ") o(" + o + ") u(" + u + ") " +
                "á(" + acentuados[0] + ") é(" + acentuados[1] + ") í(" + acentuados[2] + ") ó(" + acentuados[3] + ") ú(" + acentuados[4] + ") " +
                "à(" + acentuados[5] + ") è(" + acentuados[6] + ") ì(" + acentuados[7] + ") ò(" + acentuados[8] + ") ù(" + acentuados[9] + ") " +
                "ã(" + acentuados[10] + ") õ(" + acentuados[11] + ") â(" + acentuados[12] + ") ê(" + acentuados[13] + ") î(" + acentuados[14] + ") ô(" + acentuados[15] + ") û(" + acentuados[16] + ") " +
                "consoante(" + consoantes + ") <br>(" + br + ") <table>(" + table + ") nomepágina(" + endereco + ")"
            );
        }

        scanner.close();
    }
}
