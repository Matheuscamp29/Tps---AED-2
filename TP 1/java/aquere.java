import java.util.Scanner;

public class aquere {
    
    static int letrama(String pala, int i) {
        int resp = 0;
        if(i < pala.length()){
            if (pala.charAt(i) >= 'A' && pala.charAt(i) <= 'Z') {
                    resp++;
            }
            resp=resp + letrama(pala, i+1);
        }
        return resp;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String pala = "";  
        int i=0;
        int resp = 0;

        pala = sc.nextLine();
        while (!pala.equals("FIM")) {
            resp = letrama(pala, i);
            System.out.println(resp);
            pala = sc.nextLine();
        }

        sc.close(); 
    }
}
