//pokemon pesquisa binaria

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <ctype.h>

#define MAX_POKEMONS 1000
#define MAX_LINE_LENGTH 1024
#define MAX_NAME_LENGTH 100
#define MAX_TYPES_ABILITIES 10

typedef struct {
    int id;
    int generation;
    int captureRate;
    char name[MAX_NAME_LENGTH];
    char description[MAX_LINE_LENGTH];
    char types[MAX_TYPES_ABILITIES][MAX_NAME_LENGTH];
    int typesCount;
    char abilities[MAX_TYPES_ABILITIES][MAX_NAME_LENGTH];
    int abilitiesCount;
    double weight;
    double height;
    bool isLegendary;
    char captureDate[20];
} Pokemon;

typedef struct {
    int contador;
} Comparacao;

void incrementarComparacao(Comparacao *comp) {
    comp->contador++;
}

void replaceChar(char *str, char find, char replace) {
    char *currentPos = strchr(str, find);
    while (currentPos) {
        *currentPos = replace;
        currentPos = strchr(currentPos + 1, find);
    }
}

void removeChars(char *str, const char *charsToRemove) {
    char *src, *dst;
    for (src = dst = str; *src != '\0'; src++) {
        const char *c = charsToRemove;
        bool remove = false;
        while (*c != '\0') {
            if (*src == *c) {
                remove = true;
                break;
            }
            c++;
        }
        if (!remove) {
            *dst++ = *src;
        }
    }
    *dst = '\0';
}

bool parseCSVLine(char *line, Pokemon *pokemon) {
    char *token;
    int fieldCount = 0;
    bool inQuotes = false;
    for (int i = 0; line[i]; i++) {
        if (line[i] == '"') {
            inQuotes = !inQuotes;
        }
        if (line[i] == ',' && inQuotes) {
            line[i] = ';';
        }
    }

    char *saveptr1;
    token = strtok_r(line, ",", &saveptr1);
    while (token != NULL) {
        replaceChar(token, ';', ',');

        switch (fieldCount) {
            case 0:
                pokemon->id = atoi(token);
                break;
            case 1:
                pokemon->generation = atoi(token);
                break;
            case 2:
                strncpy(pokemon->name, token, MAX_NAME_LENGTH - 1);
                pokemon->name[MAX_NAME_LENGTH - 1] = '\0';
                break;
            case 3:
                strncpy(pokemon->description, token, MAX_LINE_LENGTH - 1);
                pokemon->description[MAX_LINE_LENGTH - 1] = '\0';
                break;
            case 4: {
                removeChars(token, "[]'\"");
                char *typeToken;
                char *saveptr2;
                typeToken = strtok_r(token, ",", &saveptr2);
                pokemon->typesCount = 0;
                while (typeToken != NULL && pokemon->typesCount < MAX_TYPES_ABILITIES) {
                    strncpy(pokemon->types[pokemon->typesCount], typeToken, MAX_NAME_LENGTH - 1);
                    pokemon->types[pokemon->typesCount][MAX_NAME_LENGTH - 1] = '\0';
                    pokemon->typesCount++;
                    typeToken = strtok_r(NULL, ",", &saveptr2);
                }
                break;
            }
            case 5:
                pokemon->isLegendary = (strcmp(token, "True") == 0 || strcmp(token, "true") == 0);
                break;
            case 6: {
                removeChars(token, "[]'\"");
                char *abilityToken;
                char *saveptr2;
                abilityToken = strtok_r(token, ",", &saveptr2);
                pokemon->abilitiesCount = 0;
                while (abilityToken != NULL && pokemon->abilitiesCount < MAX_TYPES_ABILITIES) {
                    strncpy(pokemon->abilities[pokemon->abilitiesCount], abilityToken, MAX_NAME_LENGTH - 1);
                    pokemon->abilities[pokemon->abilitiesCount][MAX_NAME_LENGTH - 1] = '\0';
                    pokemon->abilitiesCount++;
                    abilityToken = strtok_r(NULL, ",", &saveptr2);
                }
                break;
            }
            case 7:
                pokemon->weight = atof(token);
                break;
            case 8:
                pokemon->height = atof(token);
                break;
            case 9:
                pokemon->captureRate = atoi(token);
                break;
            case 10:
                strncpy(pokemon->captureDate, token, sizeof(pokemon->captureDate) - 1);
                pokemon->captureDate[sizeof(pokemon->captureDate) - 1] = '\0';
                break;
            default:
                break;
        }
        fieldCount++;
        token = strtok_r(NULL, ",", &saveptr1);
    }

    return fieldCount >= 11;
}

Pokemon *buscarPokemonPorId(int id, Pokemon *pokemons, int totalPokemons) {
    for (int i = 0; i < totalPokemons; i++) {
        if (pokemons[i].id == id) {
            return &pokemons[i];
        }
    }
    return NULL;
}

int compararPokemons(const void *a, const void *b) {
    Pokemon *p1 = (Pokemon *)a;
    Pokemon *p2 = (Pokemon *)b;
    return strcasecmp(p1->name, p2->name);
}

bool buscarPokemonPorNomeBinaria(char *nome, Pokemon *pokemons, int totalPokemons, Comparacao *comparacao) {
    int left = 0;
    int right = totalPokemons - 1;

    while (left <= right) {
        int mid = (left + right) / 2;
        incrementarComparacao(comparacao);
        int cmp = strcasecmp(nome, pokemons[mid].name);
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            right = mid - 1;
        } else {
            left = mid + 1;
        }
    }
    return false;
}

int main() {
    Pokemon pokemons[MAX_POKEMONS];
    int totalPokemons = 0;
    const char *filePath = "/tmp/pokemon.csv";

    FILE *file = fopen(filePath, "r");
    if (file == NULL) {
        printf("Erro ao abrir o arquivo %s\n", filePath);
        return 1;
    }

    char line[MAX_LINE_LENGTH];
    fgets(line, sizeof(line), file);

    while (fgets(line, sizeof(line), file)) {
        if (totalPokemons >= MAX_POKEMONS) {
            printf("Limite de Pokémons atingido.\n");
            break;
        }
        line[strcspn(line, "\r\n")] = '\0';
        if (parseCSVLine(line, &pokemons[totalPokemons])) {
            totalPokemons++;
        }
    }
    fclose(file);

    Pokemon selectedPokemons[MAX_POKEMONS];
    int totalSelected = 0;

    char inputLine[MAX_LINE_LENGTH];
    while (fgets(inputLine, sizeof(inputLine), stdin)) {
        inputLine[strcspn(inputLine, "\r\n")] = '\0';
        if (strcmp(inputLine, "FIM") == 0) {
            break;
        }
        int id = atoi(inputLine);
        Pokemon *p = buscarPokemonPorId(id, pokemons, totalPokemons);
        if (p != NULL && totalSelected < MAX_POKEMONS) {
            selectedPokemons[totalSelected++] = *p;
        }
    }

    qsort(selectedPokemons, totalSelected, sizeof(Pokemon), compararPokemons);

    char nomesPesquisados[MAX_POKEMONS][MAX_NAME_LENGTH];
    int totalNomes = 0;

    while (fgets(inputLine, sizeof(inputLine), stdin)) {
        inputLine[strcspn(inputLine, "\r\n")] = '\0';
        if (strcmp(inputLine, "FIM") == 0) {
            break;
        }
        strncpy(nomesPesquisados[totalNomes], inputLine, MAX_NAME_LENGTH - 1);
        nomesPesquisados[totalNomes][MAX_NAME_LENGTH - 1] = '\0';
        totalNomes++;
    }

    clock_t inicio = clock();
    Comparacao comparacao = {0};

    for (int i = 0; i < totalNomes; i++) {
        bool encontrado = buscarPokemonPorNomeBinaria(nomesPesquisados[i], selectedPokemons, totalSelected, &comparacao);
        if (encontrado) {
            printf("SIM\n");
        } else {
            printf("NAO\n");
        }
    }

    clock_t fim = clock();
    double tempoExecucao = ((double)(fim - inicio)) / CLOCKS_PER_SEC * 1000;

    char matricula[] = "819886";
    char nomeArquivo[50];
    sprintf(nomeArquivo, "%s_binaria.txt", matricula);

    FILE *logFile = fopen(nomeArquivo, "w");
    if (logFile != NULL) {
        fprintf(logFile, "%s\t%.2lf\t%d\n", matricula, tempoExecucao, comparacao.contador);
        fclose(logFile);
    } else {
        printf("Erro ao criar o arquivo de log.\n");
    }

    return 0;
}
