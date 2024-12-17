import java.util.*;

public class isrec {

    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }

    public static boolean vogais(String palavra, int i) {
        boolean resp;
        if (i >= palavra.length()) {
            resp = true;  
        } else if (palavra.charAt(i) == 'a' || palavra.charAt(i) == 'e' || palavra.charAt(i) == 'i' || palavra.charAt(i) == 'o' || palavra.charAt(i) == 'u' || 
                   palavra.charAt(i) == 'A' || palavra.charAt(i) == 'E' || palavra.charAt(i) == 'I' || palavra.charAt(i) == 'O' || palavra.charAt(i) == 'U') {
            resp = vogais(palavra, i + 1);
        } else {
            resp = false;   
       }
        return resp;
    }

    public static boolean consoante(String palavra, int i) {
        boolean resp;
        if (i >= palavra.length()) {
            resp = true; 
        } else if ((palavra.charAt(i) >= 'a' && palavra.charAt(i) <= 'z' || palavra.charAt(i) >= 'A' && palavra.charAt(i) <= 'Z') && 
                   !(palavra.charAt(i) == 'a' || palavra.charAt(i) == 'e' || palavra.charAt(i) == 'i' || palavra.charAt(i) == 'o' || palavra.charAt(i) == 'u' || 
                     palavra.charAt(i) == 'A' || palavra.charAt(i) == 'E' || palavra.charAt(i) == 'I' || palavra.charAt(i) == 'O' || palavra.charAt(i) == 'U')) {
            resp = consoante(palavra, i + 1);
        } else {
            resp = false;  
        }
        return resp;
    }

    public static boolean numint(String palavra, int i) {
        boolean resp;
        if (i >= palavra.length()) {
            resp = true;  
        } else if (palavra.charAt(i) >= '0' && palavra.charAt(i) <= '9') {
            resp = numint(palavra, i + 1);  
        } else {
            resp = false;  
        }
        return resp;
    }

    public static boolean numreal(String palavra, int i) {
        boolean resp;
        if (i >= palavra.length()) {
            resp = true;  
        } else if ((palavra.charAt(i) >= '0' && palavra.charAt(i) <= '9') || palavra.charAt(i) == '.' || palavra.charAt(i) == ',') {
            resp = numreal(palavra, i + 1);  
        } else {
            resp = false;  
        }
        return resp;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String palavra = sc.nextLine();

        while (!fim(palavra)) {
            boolean x1 = vogais(palavra, 0);
            boolean x2 = consoante(palavra, 0);
            boolean x3 = numint(palavra, 0);
            boolean x4 = numreal(palavra, 0);

            if (x1) {
                System.out.print("SIM ");
            } else {
                System.out.print("NAO ");
            }
            if (x2) {
                System.out.print("SIM ");
            } else {
                System.out.print("NAO ");
            }
            if (x3) {
                System.out.print("SIM ");
            } else {
                System.out.print("NAO ");
            }
            if (x4) {
                System.out.print("SIM ");
            } else {
                System.out.print("NAO ");
            }
            System.out.println();
            palavra = sc.nextLine();
        }
        sc.close();
    }
}
