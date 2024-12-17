import java.util.Scanner;

public class cifrarec {
    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }

    static String cifra(String palavra, int i) {
        if (i >= palavra.length()) {
            return "";
        } else {
            char letra = (char) (palavra.charAt(i) + 3);
            return letra + cifra(palavra, i + 1);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String palavra = sc.nextLine();

        while (!fim(palavra)) {
            String palavraCifrada = cifra(palavra, 0);
            System.out.println(palavraCifrada);
            palavra = sc.nextLine();
        }

        sc.close();
    }
}
