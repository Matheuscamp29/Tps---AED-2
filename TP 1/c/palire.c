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
bool comp(char pala[], int i, int y) {
    bool resp = true;

    if (i < y) {
        if (pala[i] == pala[y]) {
            resp = comp(pala, i + 1, y - 1);  
        } else {
            resp = false;  
        }
    }
    return resp;
}

void main() {
    bool resp = 0;
    char pala[500];

    scanf("%[^\n]", pala);
    getchar();
    while (!fim(pala)) {
        int tam = tamanho(pala);
        resp = comp(pala, 0, tam - 1);

        if (resp) {
            printf("SIM\n");
        } else {
            printf("NAO\n");
        }
        
        scanf("%[^\n]", pala);
        getchar();
    }
}
