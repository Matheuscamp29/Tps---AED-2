import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Scanner;

public class arq {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        try {
            int n = sc.nextInt();
            
            RandomAccessFile a = new RandomAccessFile("a.txt", "rw");
            
            for (int i = 0; i < n; i++) {
                double x = sc.nextDouble();
                a.writeDouble(x);
            }
            
            long len = a.length();
            a.close();

            a = new RandomAccessFile("a.txt", "r");
            
            for (long i = 8; i <= len; i += 8) {
                a.seek(len - i);
                double aux = a.readDouble();
                
                if (aux % 1 == 0) {
                    System.out.println((int) aux);
                } else {
                    System.out.println(aux);
                }
            }
            
            a.close();
        } catch (IOException e) {
            System.err.println("Erro ao acessar o arquivo: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
