#include <stdio.h>
#include <string.h>
int letrama(char pala[]){
    int tam=0;
    int resp=0;
    tam=strlen(pala);
    for(int i=0;i<tam;i++){
        if(pala[i]>='A' && pala[i]<='Z'){
        resp=resp+1;
        }
    }
    return (resp);
}

void main(){
    char pala[500]="";
    int resp=0;

    scanf("%[^\n]", pala);
    getchar();
    while(strcmp(pala, "FIM")!=0){
        resp=letrama(pala);
        printf("%d\n", resp);
        scanf(" %[^\n]", pala);
        getchar();

    }



}