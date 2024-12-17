import java.util.*;

public class alt {
    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }

    private static String substituirLetras(String str, char letra1, char letra2) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == letra1) {
                resultado.append(letra2);  
            } else {
                resultado.append(c);  
            }
        }
        return resultado.toString();
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random gerador = new Random();
        gerador.setSeed(4); 
        String pala=sc.nextLine();
        while (!fim(pala)) {
            
            char letra1 = (char) ('a' + (Math.abs(gerador.nextInt()) % 26));
            char letra2 = (char) ('a' + (Math.abs(gerador.nextInt()) % 26));

            String resultado = substituirLetras(pala, letra1, letra2);
            
            System.out.println(resultado);
            pala=sc.nextLine();
        }

        sc.close();
    }

    
}
