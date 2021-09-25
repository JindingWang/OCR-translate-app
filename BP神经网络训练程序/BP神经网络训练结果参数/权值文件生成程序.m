a = net.IW{1,1};
b = net.b{1};
c = net.LW{2,1};
d = net.b{2};
%d = d';
f = fopen('c.txt','w');
[row,column] = size(c);
fprintf(f,'{');
for i=1:row
    fprintf(f,'{');
    for j=1:column-1
        fprintf(f,'%f,',c(i,j));
    end
    fprintf(f,'%f},',c(i,column));
end
fprintf(f,'}');
fclose(f);