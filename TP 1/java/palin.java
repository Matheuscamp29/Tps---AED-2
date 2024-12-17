import java.util.Scanner;

class palin{
    
    public static boolean fim(String palavra) {
        return palavra.length() >= 3 && palavra.startsWith("FIM");
    }

    static boolean comp(String pala){
        int y=pala.length()-1;
        boolean resp=true;
        for(int i=0;i<pala.length()/2;i++){
            if(pala.charAt(i) == pala.charAt(y)){
               
            }
            else{
                resp=false;
                i=pala.length();
            }
            y=y-1;
        }
        return(resp);
    }
    
    
    public static void main(String[] args){
    
        Scanner sc= new Scanner(System.in);
        String pala = new String();
        pala = sc.nextLine();
            
        while(!fim(pala)){
            if(comp(pala)){
                System.out.println("SIM");
            }
            else{
                System.out.println("NAO");
            }
            pala = sc.nextLine();    
        }
    }
}
