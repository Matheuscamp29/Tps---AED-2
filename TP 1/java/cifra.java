import java.util.*;

public class cifra {
    
    public static boolean isFim(String palavra) {
        return palavra.length() >= 3 && palavra.substring(0, 3).equalsIgnoreCase("FIM");
    }

    public static void cifrarPalavra(String palavra) {
        StringBuilder palavraCifrada = new StringBuilder();
        
        for (int i = 0; i < palavra.length(); i++) {
            char letra = (char)(palavra.charAt(i) + 3);
            palavraCifrada.append(letra);
        }
        
        System.out.println(palavraCifrada.toString());
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String palavra = sc.nextLine();;

        
        
        
        // Loop até encontrar a palavra "FIM"
        while (!isFim(palavra)) {
            cifrarPalavra(palavra);
            palavra = sc.nextLine();
        }
        
        sc.close();
    }
}
