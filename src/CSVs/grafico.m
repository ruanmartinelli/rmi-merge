mtx = csvread("benchmark 2-escravos-remoto-real.csv");
nome = strsplit("benchmark 2-escravos-remoto-real.csv", ",");
axis ('image');
plot (mtx(:,1), mtx(:,2), "r");
xlabel ('Tamanho do Vetor (u)');
ylabel ('Tempo de Execucao (s)');
grid on;
title (nome{1});
print (nome{1}, "-dpng");
