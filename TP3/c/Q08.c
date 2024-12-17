#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define MAX_LINE_LENGTH 1024 
#define MAX_FIELDS 20        
#define MAX_STRING_LENGTH 100 

typedef struct {
    int id;
    int generation;
    int captureRate;
    char name[MAX_STRING_LENGTH];
    char description[MAX_STRING_LENGTH];
    char **types;
    int numTypes;
    char **abilities;
    int numAbilities;
    double weight;
    double height;
    int isLegendary; 
    char captureDate[MAX_STRING_LENGTH]; 
} Pokemon;

typedef struct Node {
    Pokemon* data;
    struct Node *next;
    struct Node *prev;
} Node;

long comparisons = 0;
long movements = 0;

// Funções auxiliares
int isNumeric(const char *str) {
    int result = 1;
    int i = 0;
    if (str == NULL || *str == '\0') {
        result = 0;
    } else {
        if (str[0] == '-' || str[0] == '+') i++;
        while (str[i] != '\0' && result == 1) {
            if (!isdigit(str[i])) {
                result = 0;
            }
            i++;
        }
    }
    return result;
}

void trim(char *str) {
    int len = strlen(str);
    int start = 0;
    while (isspace((unsigned char)str[start])) start++;
    int end = len - 1;
    while (end >= start && isspace((unsigned char)str[end])) end--;
    int i = 0;
    while (start <= end) {
        str[i++] = str[start++];
    }
    str[i] = '\0';
}

int isDouble(const char *str) {
    char *endptr;
    strtod(str, &endptr);
    return (*endptr == '\0' && endptr != str);
}

void removeChars(char *str, const char *charsToRemove) {
    int src = 0, dst = 0;
    while (str[src] != '\0') {
        int shouldRemove = 0;
        int i = 0;
        while (charsToRemove[i] != '\0') {
            if (str[src] == charsToRemove[i]) {
                shouldRemove = 1;
                break;
            }
            i++;
        }
        if (!shouldRemove) {
            str[dst++] = str[src];
        }
        src++;
    }
    str[dst] = '\0';
}

int parseCSVLine(char *line, char **fields, int maxFields) {
    int numFields = 0;
    int inQuotes = 0;
    char *fieldStart = line;
    int i = 0;
    while (line[i] != '\0') {
        if (line[i] == '"') {
            inQuotes = !inQuotes;
        } else if (line[i] == ',' && !inQuotes) {
            line[i] = '\0';
            if (numFields < maxFields) {
                fields[numFields++] = fieldStart;
            }
            fieldStart = &line[i + 1];
        }
        i++;
    }
    if (numFields < maxFields) {
        fields[numFields++] = fieldStart;
    }
    return numFields;
}

// Funções para manipulação da lista
Node* createNode(Pokemon* p) {
    Node* newNode = (Node*)malloc(sizeof(Node));
    if (!newNode) {
        fprintf(stderr, "Erro de alocação de memória.\n");
        exit(EXIT_FAILURE);
    }
    newNode->data = p;
    newNode->next = NULL;
    newNode->prev = NULL;
    return newNode;
}

void appendNode(Node** headRef, Pokemon* p) {
    Node* newNode = createNode(p);
    if (*headRef == NULL) {
        *headRef = newNode;
    } else {
        Node* temp = *headRef;
        while (temp->next != NULL) {
            temp = temp->next;
        }
        temp->next = newNode;
        newNode->prev = temp;
    }
}

Pokemon* buscarPokemonPorId(int id, Node* head) {
    Node* current = head;
    while (current != NULL) {
        comparisons++; // Contabiliza a comparação
        if (current->data->id == id) {
            return current->data;
        }
        current = current->next;
    }
    return NULL;
}

void liberarLista(Node* head, int liberarDados) {
    Node* current = head;
    while (current != NULL) {
        Node* temp = current;
        current = current->next;
        if (liberarDados && temp->data != NULL) {
            // Liberar tipos
            for (int j = 0; j < temp->data->numTypes; j++) {
                free(temp->data->types[j]);
            }
            free(temp->data->types);
            // Liberar habilidades
            for (int j = 0; j < temp->data->numAbilities; j++) {
                free(temp->data->abilities[j]);
            }
            free(temp->data->abilities);
            // Liberar o Pokemon
            free(temp->data);
        }
        // Liberar o nó
        free(temp);
    }
}

// Função para ler Pokémons e criar a lista principal
Node* lerPokemons(const char *filename) {
    FILE *file = fopen(filename, "r");
    Node* head = NULL;
    if (file == NULL) {
        fprintf(stderr, "Erro ao abrir o arquivo %s\n", filename);
        return head;
    }

    char line[MAX_LINE_LENGTH];
    // Ler a primeira linha (cabeçalho) e ignorar
    if (fgets(line, sizeof(line), file) == NULL) {
        fclose(file);
        return head;
    }

    while (fgets(line, sizeof(line), file) != NULL) {
        line[strcspn(line, "\n")] = '\0';

        if (strcmp(line, "FIM") == 0) {
            break;
        }

        if (strlen(line) > 0) {
            char *fields[MAX_FIELDS];
            int numFields = parseCSVLine(line, fields, MAX_FIELDS);
            if (numFields >= 12) {
                // Alocar memória para um novo Pokemon
                Pokemon* p = (Pokemon*)malloc(sizeof(Pokemon));
                if (!p) {
                    fprintf(stderr, "Erro de alocação de memória para Pokemon.\n");
                    fclose(file);
                    liberarLista(head, 1);
                    exit(EXIT_FAILURE);
                }

                p->id = isNumeric(fields[0]) ? atoi(fields[0]) : 0;
                p->generation = isNumeric(fields[1]) ? atoi(fields[1]) : 0;
                strncpy(p->name, fields[2], MAX_STRING_LENGTH);
                p->name[MAX_STRING_LENGTH - 1] = '\0';
                strncpy(p->description, fields[3], MAX_STRING_LENGTH);
                p->description[MAX_STRING_LENGTH - 1] = '\0';

                // Processar tipos (fields[4] e fields[5])
                p->numTypes = 0;
                p->types = NULL;
                for (int t = 4; t <=5; t++) {
                    if (t >= numFields) continue;
                    if (strlen(fields[t]) > 0) {
                        trim(fields[t]);
                        removeChars(fields[t], "[]'\"");
                        if (strlen(fields[t]) > 0) {
                            char *typeToken = strtok(fields[t], ",");
                            while (typeToken != NULL) {
                                trim(typeToken);
                                p->types = realloc(p->types, (p->numTypes + 1) * sizeof(char *));
                                p->types[p->numTypes] = (char*)malloc(MAX_STRING_LENGTH);
                                if (!p->types[p->numTypes]) {
                                    fprintf(stderr, "Erro de alocação de memória para tipos.\n");
                                    fclose(file);
                                    liberarLista(head, 1);
                                    exit(EXIT_FAILURE);
                                }
                                strncpy(p->types[p->numTypes], typeToken, MAX_STRING_LENGTH);
                                p->types[p->numTypes][MAX_STRING_LENGTH - 1] = '\0';
                                p->numTypes++;
                                typeToken = strtok(NULL, ",");
                            }
                        }
                    }
                }

                // Processar habilidades (fields[6])
                removeChars(fields[6], "[]'\"");
                char *abilityToken = strtok(fields[6], ",");
                p->numAbilities = 0;
                p->abilities = NULL;
                while (abilityToken != NULL) {
                    trim(abilityToken);
                    p->abilities = realloc(p->abilities, (p->numAbilities + 1) * sizeof(char *));
                    p->abilities[p->numAbilities] = (char*)malloc(MAX_STRING_LENGTH);
                    if (!p->abilities[p->numAbilities]) {
                        fprintf(stderr, "Erro de alocação de memória para habilidades.\n");
                        fclose(file);
                        liberarLista(head, 1);
                        exit(EXIT_FAILURE);
                    }
                    strncpy(p->abilities[p->numAbilities], abilityToken, MAX_STRING_LENGTH);
                    p->abilities[p->numAbilities][MAX_STRING_LENGTH - 1] = '\0';
                    p->numAbilities++;
                    abilityToken = strtok(NULL, ",");
                }

                p->weight = isDouble(fields[7]) ? atof(fields[7]) : 0.0;
                p->height = isDouble(fields[8]) ? atof(fields[8]) : 0.0;
                p->captureRate = isNumeric(fields[9]) ? atoi(fields[9]) : 0;

                p->isLegendary = isNumeric(fields[10]) ? atoi(fields[10]) : 0;

                strncpy(p->captureDate, fields[11], MAX_STRING_LENGTH);
                p->captureDate[MAX_STRING_LENGTH - 1] = '\0';

                appendNode(&head, p);
            }
        }
    }

    fclose(file);
    return head;
}

// Função para imprimir um Pokémon
void imprimirPokemon(Pokemon *p) {
    char formattedTypes[500] = "";
    for (int i = 0; i < p->numTypes; i++) {
        strncat(formattedTypes, "'", sizeof(formattedTypes) - strlen(formattedTypes) - 1);
        strncat(formattedTypes, p->types[i], sizeof(formattedTypes) - strlen(formattedTypes) - 1);
        strncat(formattedTypes, "'", sizeof(formattedTypes) - strlen(formattedTypes) - 1);
        if (i < p->numTypes - 1) {
            strncat(formattedTypes, ", ", sizeof(formattedTypes) - strlen(formattedTypes) - 1);
        }
    }

    char formattedAbilities[500] = "";
    for (int i = 0; i < p->numAbilities; i++) {
        strncat(formattedAbilities, "'", sizeof(formattedAbilities) - strlen(formattedAbilities) - 1);
        strncat(formattedAbilities, p->abilities[i], sizeof(formattedAbilities) - strlen(formattedAbilities) - 1);
        strncat(formattedAbilities, "'", sizeof(formattedAbilities) - strlen(formattedAbilities) - 1);
        if (i < p->numAbilities - 1) {
            strncat(formattedAbilities, ", ", sizeof(formattedAbilities) - strlen(formattedAbilities) - 1);
        }
    }

    printf("[#%d -> %s: %s - [%s] - [%s] - %.1lfkg - %.1lfm - %d%% - %s - %d gen] - %s\n",
           p->id, p->name, p->description, formattedTypes, formattedAbilities,
           p->weight, p->height, p->captureRate,
           p->isLegendary ? "true" : "false", p->generation, p->captureDate);
}

// Função para trocar os ponteiros de dados de dois nós
void swapNodesData(Node* a, Node* b) {
    if (a == NULL || b == NULL) return;
    Pokemon* temp = a->data;
    a->data = b->data;
    b->data = temp;
    movements += 3; // Considerando 3 movimentações (troca de ponteiros)
}

// Função de comparação
int comparePokemons(Pokemon *p1, Pokemon *p2) {
    comparisons++;
    if (p1->generation < p2->generation) {
        return -1;
    } else if (p1->generation > p2->generation) {
        return 1;
    } else {
        return strcmp(p1->name, p2->name);
    }
}

// Implementação do Quicksort para lista duplamente encadeada
Node* partition(Node* low, Node* high) {
    Pokemon* pivot = high->data;
    movements++; // Movimentação do pivot
    Node* i = low->prev;

    for (Node* j = low; j != high; j = j->next) {
        if (comparePokemons(j->data, pivot) <= 0) {
            i = (i == NULL) ? low : i->next;
            swapNodesData(i, j);
        }
    }
    i = (i == NULL) ? low : i->next;
    swapNodesData(i, high);
    return i;
}

void quicksortRec(Node* low, Node* high) {
    if (high != NULL && low != high && low != high->next) {
        Node* p = partition(low, high);
        quicksortRec(low, p->prev);
        quicksortRec(p->next, high);
    }
}

void quicksortList(Node* head) {
    // Encontrar o último nó
    Node* last = head;
    if (head == NULL)
        return;
    while (last->next != NULL)
        last = last->next;

    quicksortRec(head, last);
}

int main() {
    // Ler todos os Pokémons na lista principal
    Node* pokemons = lerPokemons("/tmp/pokemon.csv");

    if (pokemons == NULL) {
        fprintf(stderr, "Nenhum Pokémon foi carregado.\n");
        return EXIT_FAILURE;
    }

    // Lista para Pokémons selecionados
    Node* selectedPokemons = NULL;

    char input[MAX_STRING_LENGTH];
    while (scanf("%s", input) != EOF) {
        if (strcmp(input, "FIM") == 0) {
            break;
        } else {
            int id = atoi(input);
            Pokemon* p = buscarPokemonPorId(id, pokemons);
            if (p != NULL) {
                appendNode(&selectedPokemons, p);
            } else {
                printf("Pokémon com ID %d não existe.\n", id);
            }
        }
    }

    // Medir o tempo de execução
    clock_t start = clock();

    // Ordenar a lista de Pokémons selecionados
    quicksortList(selectedPokemons);

    clock_t end = clock();
    double executionTime = ((double)(end - start)) / CLOCKS_PER_SEC * 1000.0;

    // Imprimir os Pokémons ordenados
    Node* current = selectedPokemons;
    int count = 1;
    while (current != NULL) {
        imprimirPokemon(current->data);
        current = current->next;
    }

    // Preparar o arquivo de log
    char matricula[] = "819886"; 
    char logFileName[50];
    sprintf(logFileName, "%s_quicksort2.txt", matricula);
    FILE *logFile = fopen(logFileName, "w");
    if (logFile) {
        fprintf(logFile, "%s\t%ld\t%ld\t%.2lf\n", matricula, comparisons, movements, executionTime);
        fclose(logFile);
    } else {
        printf("Erro ao criar o arquivo de log.\n");
    }

    // Liberar a memória alocada
    liberarLista(pokemons, 1); // Liberar Pokémons e nós
    liberarLista(selectedPokemons, 0); // Liberar apenas nós

    return 0;
}
