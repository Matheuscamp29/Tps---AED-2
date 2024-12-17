#include <stdio.h>
#include <stdbool.h>
#include <string.h>

int tamanho(const char *palavra){
    int length = 0;
    
    while (palavra[length] != '\0') {
        length++;
    }

    return length;
}


bool fim(char *palavra){
    bool resp=false;
    if(tamanho(palavra) >= 3 && palavra[0] == 'F' && palavra[1] == 'I' && palavra[2] == 'M'){
        resp=true;
    }
    return(resp);
}

bool comp(char pala[]){
    
    int i=0;
    int tam=0;
    tam=tamanho(pala);
    int y=tam-1;
    bool resp=0;

    for(i=0;i<tam/2;i++){
        if(pala[i] == pala[y]){
            resp=1;
        }
        else{
            resp=0;
            i=tam;
        }
        y=y-1;
    }
    return(resp);
}

void main(){
    bool resp=0;
    int x=0;
    char pala[500];
    scanf("%[^\n]", pala);
    getchar();
    while(!fim(pala)){
        resp = comp(pala);
        if(resp==1){
        printf("SIM\n");
        }
        else{
            printf("NAO\n");
        }
        scanf(" %[^\n]", pala);
        getchar();
    }
}
