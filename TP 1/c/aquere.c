#include <stdio.h>
#include <string.h>

int letrama(char pala[],int i){
    int tam=0;
    int resp=0;
    tam=strlen(pala);

    if (i < tam) {
        if (pala[i] >= 'A' && pala[i] <= 'Z') {
            resp = resp +1;
        }
        resp += letrama(pala, i + 1);
    }
    
    return resp;
}


void main(){
    char pala[500]="";
    int resp=0;
    int i=0;
    scanf("%[^\n]", pala);
    getchar();
    while(strcmp(pala, "FIM")!=0){
        resp=letrama(pala, i);
        printf("%d\n", resp);
        scanf(" %[^\n]", pala);
        getchar();

    }



}