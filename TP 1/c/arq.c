#include <stdio.h>
#include <stdlib.h>

int main() {
    int n;
    scanf("%d", &n);

    FILE *file = fopen("a.bin", "wb");
    

    for (int i = 0; i < n; i++) {
        double x;
        scanf("%lf", &x);
        fwrite(&x, sizeof(double), 1, file);
    }

    fclose(file);

    file = fopen("a.bin", "rb");
    

    fseek(file, 0, SEEK_END);
    long len = ftell(file);

    for (long i = sizeof(double); i <= len; i += sizeof(double)) {
        fseek(file, -i, SEEK_END);
        double aux;
        fread(&aux, sizeof(double), 1, file);
        
        if (aux == (int)aux) {
            printf("%d\n", (int)aux);
        } else {
            printf("%g\n", aux);
        }
    }

    fclose(file);

    return 0;
}
