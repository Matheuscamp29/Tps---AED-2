import java.util.*;
public class alea {
    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }
    
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        String palavra=sc.nextLine();
        while(!fim(palavra)){
            palavra=sc.nextLine();
        }
    sc.close();
    }
}