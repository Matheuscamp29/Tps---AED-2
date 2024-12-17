#include <stdio.h>
#include <string.h>
#include <stdbool.h>


void comb(char pala1[500], char pala2[500]){
    char resp[1000];
    int tam = strlen(pala1) + strlen(pala2);
    int y = 0, x = 0;
    
    for (int i = 0; i < tam; i++){
        if (i % 2 == 0){//alternar entre as 2 palavras
            if (x < strlen(pala1)) {
                resp[i] = pala1[x];
                x++;
            } else {//se a palavra1 acabar completar com a palavra 2
                resp[i] = pala2[y];
                y++;
            }
        } else {
            if (y < strlen(pala2)) {
                resp[i] = pala2[y];
                y++;
            } else {
                resp[i] = pala1[x];
                x++;
            }
        }
    }
    
    resp[tam] = '\0';//incluir o \0 no final
    printf("%s\n", resp);//mostrar resp
}

void main(){
    char pala1[500];
    char pala2[500];
    
    scanf("%s", pala1); //ler as 2 primeiras palavras
    getchar();
    scanf("%s", pala2);
    getchar();
    while (pala1 !='null' && pala2 !='null') { //fazer ate null
        comb(pala1, pala2);//combinar as 2 palavrar
        scanf("%s", pala1);
        getchar();
        scanf("%s", pala2);
        getchar();
    }
}
