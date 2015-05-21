mtx = csvread("benchmark4escravosremotoreal.csv");
nome = strsplit("benchmark4escravosremotoreal.csv", ",");
axis ('image');
plot (mtx(:,1), mtx(:,2), "r");
xlabel ('Tamanho do Vetor (u)');
ylabel ('Tempo de Execucao (ns)');
grid on;
title (nome{1});
print (nome{1}, "-dpng");
