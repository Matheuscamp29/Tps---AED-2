import java.util.*;
public class is {
    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }
    public static boolean vogais(String palavra){
        boolean resp=false;
        for(int i=0;i<palavra.length();i++){
            if(palavra.charAt(i) == 'u' || palavra.charAt(i) == 'o'|| palavra.charAt(i) == 'i'|| palavra.charAt(i) == 'e'|| palavra.charAt(i) == 'a'|| palavra.charAt(i) == 'U'|| palavra.charAt(i) == 'O'|| palavra.charAt(i) == 'I'|| palavra.charAt(i) == 'E'|| palavra.charAt(i) == 'A'){
                resp=true;
            }
            else{
                resp=false;
                i=palavra.length();
            }
        }
        return(resp);
    }
    public static boolean numint(String palavra){
        boolean resp=false;
        for(int i=0;i<palavra.length();i++){
            if(palavra.charAt(i)>='0' && palavra.charAt(i)<='9'){
                resp=true;
            }
            else{
                resp=false;
                i=palavra.length();
            }

        }
        return(resp);
    }
    public static boolean numreal(String palavra){
        boolean resp=false;
        for(int i=0;i<palavra.length();i++){
            if(palavra.charAt(i)>='0' && palavra.charAt(i)<='9'){
                resp=true;
            }
            else{
                resp=false;
                i=palavra.length();
            }
        }
        return(resp);
    }

    public static boolean consoante(String palavra){
        boolean resp=false;
        for(int i=0;i<palavra.length();i++){
            if(palavra.charAt(i) == 'u' || palavra.charAt(i) == 'o'|| palavra.charAt(i) == 'i'|| palavra.charAt(i) == 'e'|| palavra.charAt(i) == 'a'|| palavra.charAt(i) == 'U'|| palavra.charAt(i) == 'O'|| palavra.charAt(i) == 'I'|| palavra.charAt(i) == 'E'|| palavra.charAt(i) == 'A' || palavra.charAt(i)>='0' && palavra.charAt(i)<='9'){
                resp=false;
                i=palavra.length();
            }
            else{
                resp=true;
            }
        }
        return(resp);
    }
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        String palavra=sc.nextLine();
        
        while(!fim(palavra)){
            boolean x1=false;
            boolean x2=false;
            boolean x3=false;
            boolean x4=false;


            x1=vogais(palavra);
            x2=consoante(palavra);
            x3=numint(palavra);
            x4=numreal(palavra);


            if(x1){
                System.out.print("SIM ");
            }
            else{
                System.out.print("NAO ");
            }
            if(x2){
                System.out.print("SIM ");
            }
            else{
                System.out.print("NAO ");
            }
            if(x3){
                System.out.print("SIM ");
            }
            else{
                System.out.print("NAO ");
            }
            if(x4){
                System.out.print("SIM ");
            }
            else{
                System.out.print("NAO ");
            }
            System.out.println();
            palavra=sc.nextLine();
        }
    }
}
