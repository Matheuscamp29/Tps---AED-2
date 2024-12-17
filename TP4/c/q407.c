#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define TAM_TAB 21         
#define MAX_POKEMONS 1000   
#define MAX_NAME_LEN 50     
#define LOG_FILE "819886_hashIndireta.txt" 
#define CSV_FILE "/tmp/pokemon.csv" 

typedef struct {
    int id;
    char name[MAX_NAME_LEN];
} Pokemon;

typedef struct Node {
    char name[MAX_NAME_LEN];
    struct Node *next;
} Node;

typedef struct {
    Node *buckets[TAM_TAB];
} HashTable;

typedef struct {
    int contador;
} Comparacao;

typedef struct {
    int posicao;
    int encontrado;
} ResultadoPesquisa;

int hash_function(char *name) {
    int sum = 0;
    for(int i = 0; name[i] != '\0'; i++) {
        sum += (int) name[i];
    }
    return sum % TAM_TAB;
}

void inicializar_hash(HashTable *ht) {
    for(int i = 0; i < TAM_TAB; i++) {
        ht->buckets[i] = NULL;
    }
}

void inserir(HashTable *ht, char *name) {
    int index = hash_function(name);
    Node *new_node = (Node *) malloc(sizeof(Node));
    if(new_node == NULL) {
        printf("Erro de alocação de memória.\n");
        exit(1);
    }
    strcpy(new_node->name, name);
    new_node->next = ht->buckets[index];
    ht->buckets[index] = new_node;
}

ResultadoPesquisa pesquisar(HashTable *ht, char *name, Comparacao *comp) {
    ResultadoPesquisa resultado;
    resultado.encontrado = 0;
    resultado.posicao = -1;

    int index = hash_function(name);
    Node *current = ht->buckets[index];
    int pos_in_list = 0;

    while(current != NULL) {
        comp->contador++;
        if(strcmp(current->name, name) == 0) {
            resultado.encontrado = 1;
            resultado.posicao = index;
        }
        current = current->next;
        pos_in_list++;
    }

    return resultado;
}

void trim(char *str) {
    int start = 0;
    while(str[start] == ' ' || str[start] == '\t') start++;
    if(start > 0) {
        memmove(str, str + start, strlen(str) - start + 1);
    }
    int end = strlen(str) - 1;
    while(end >= 0 && (str[end] == ' ' || str[end] == '\t' || str[end] == '\n' || str[end] == '\r')) {
        str[end] = '\0';
        end--;
    }
}

int ler_csv(char *filename, Pokemon pokemons[]) {
    FILE *file = fopen(filename, "r");
    if(file == NULL) {
        printf("Erro ao abrir o arquivo %s.\n", filename);
        exit(1);
    }

    char line[512];
    int count = 0;

    if(fgets(line, sizeof(line), file) == NULL) {
        printf("Arquivo CSV vazio.\n");
        fclose(file);
        exit(1);
    }

    while(fgets(line, sizeof(line), file) != NULL && count < MAX_POKEMONS) {
        trim(line);
        if(strcmp(line, "FIM") == 0) {
            break;
        }
        char *token = strtok(line, ",");
        if(token == NULL) continue;
        int id = atoi(token);

        for(int i = 0; i < 2; i++) {
            token = strtok(NULL, ",");
            if(token == NULL) break;
        }

        if(token == NULL) continue;
        char name[MAX_NAME_LEN];
        strcpy(name, token);
        trim(name);

        if(name[0] == '\"') {
            memmove(name, name + 1, strlen(name));
        }
        if(name[strlen(name)-1] == '\"') {
            name[strlen(name)-1] = '\0';
        }

        pokemons[count].id = id;
        strcpy(pokemons[count].name, name);
        count++;
    }

    fclose(file);
    return count;
}

Pokemon* buscar_pokemon_por_id(int id, Pokemon pokemons[], int total) {
    for(int i = 0; i < total; i++) {
        if(pokemons[i].id == id) {
            return &pokemons[i];
        }
    }
    return NULL;
}

void inicializar_comparacao(Comparacao *comp) {
    comp->contador = 0;
}

void imprimir_resultado(char *name, ResultadoPesquisa resultado) {
    if(resultado.encontrado) {
        printf("=> %s: (Posicao: %d) SIM\n", name, resultado.posicao);
    } else {
        printf("=> %s: NAO\n", name);
    }
}

int main() {
    HashTable ht;
    inicializar_hash(&ht);

    Comparacao comp;
    inicializar_comparacao(&comp);

    Pokemon pokemons[MAX_POKEMONS];
    int total_pokemons = ler_csv(CSV_FILE, pokemons);

    char input[100];
    clock_t start, end;
    double cpu_time_used;

    // Inserção
    while(fgets(input, sizeof(input), stdin)) {
        trim(input);
        if(strcmp(input, "FIM") == 0) {
            break;
        }
        if(strlen(input) == 0) {
            continue;
        }
        int id = atoi(input);
        if(id == 0 && strcmp(input, "0") != 0) {
            continue; 
        }
        Pokemon *poke = buscar_pokemon_por_id(id, pokemons, total_pokemons);
        if(poke != NULL) {
            inserir(&ht, poke->name);
        }
    }

    start = clock();

    // Pesquisas
    while(fgets(input, sizeof(input), stdin)) {
        trim(input);
        if(strcmp(input, "FIM") == 0) {
            break;
        }
        if(strlen(input) == 0) {
            continue;
        }
        ResultadoPesquisa resultado = pesquisar(&ht, input, &comp);
        imprimir_resultado(input, resultado);
    }

    end = clock();
    cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC * 1000; // em milissegundos

    FILE *log_file = fopen(LOG_FILE, "w"); 
    if(log_file == NULL) {
        printf("Erro ao criar o arquivo de log.\n");
        exit(1);
    }
    fprintf(log_file, "819886\t%.0f\t%d\n", cpu_time_used, comp.contador);
    fclose(log_file);

    return 0;
}
