import java.util.Scanner;

class palire{


    static boolean comp(String pala,int y,int i){
        
        boolean resp=true;
        if(i<pala.length()/2){
            if(pala.charAt(i) == pala.charAt(y)){
               
            }
            else{
                resp=false;
                i=pala.length();
            }
            
            comp(pala,y-1,i+1);
        }
        return(resp);
    }
    
    
    public static void main(String[] args){
    
        Scanner sc= new Scanner(System.in);
        String pala = new String();
        pala = sc.nextLine();
            
        while(!pala.equals("FIM")){
            int y=pala.length()-1;
        int i=0;
            if(comp(pala,y,i)){
                System.out.println("SIM");
            }
            else{
                System.out.println("NAO");
            }
            pala = sc.nextLine();    
        }
        sc.close();
    }
}
