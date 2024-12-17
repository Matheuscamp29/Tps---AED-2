import java.util.Scanner;

public class aque {
    
    static int letrama(String pala) {
        int resp = 0;
        for (int i = 0; i < pala.length(); i++) {
            if (pala.charAt(i) >= 'A' && pala.charAt(i) <= 'Z') {
                resp++;
            }
        }
        return resp;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String pala = "";  
        int resp = 0;

        pala = sc.nextLine();
        while (!pala.equals("FIM")) {
            resp = letrama(pala);
            System.out.println(resp);
            pala = sc.nextLine();
        }

        sc.close(); 
    }
}
