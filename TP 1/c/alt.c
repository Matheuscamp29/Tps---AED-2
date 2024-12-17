#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdbool.h>
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
void substituirLetras(char* str, char letra1, char letra2, int index) {
    if (str[index] == '\0') {
        return;
    }
    if (str[index] == letra1) {
        str[index] = letra2;
    }
    substituirLetras(str, letra1, letra2, index + 1);  // chamada recursiva
}

void imp(char* palavra) {
    if (fim(palavra)) {
        return; 
    }

    char letra1 = 'a' + (rand() % 26);
    char letra2 = 'a' + (rand() % 26);

    substituirLetras(palavra, letra1, letra2, 0);
    
    printf("%s\n", palavra);

    if (fgets(palavra, 100, stdin) != NULL) {
        palavra[strcspn(palavra, "\n")] = '\0';  
        imp(palavra);  
    }
}

int main() {
    char palavra[100];
    srand(4);  
    if (fgets(palavra, 100, stdin) != NULL) {
        palavra[strcspn(palavra, "\n")] = '\0';  
        imp(palavra);  
    }

    return 0;
}