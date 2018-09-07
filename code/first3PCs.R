library(RDRToolbox)

residiso <- read.csv("../data/3_residPCs_SZ_vs_control.csv", row.names=1);
dim3 <- residiso[,c("PC1","PC2","PC3")];
dim3

#show interactive 3D plot for 30 seconds.
metadata <- read.csv("../data/1_jaffe_metadata_SZ_vs_control.csv");
labels <- metadata$Dx;
plotDR(data=dim3, labels=labels);
Sys.sleep(300);

