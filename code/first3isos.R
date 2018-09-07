library(RDRToolbox)

residiso <- read.csv("../data/3_residiso_k2_SZ_vs_control.csv", row.names=1);
dim3 <- residiso[grep("dim3", names(residiso), value=TRUE)];

#show interactive 3D plot for 30 seconds.
metadata <- read.csv("../data/1_jaffe_metadata_SZ_vs_control.csv");
labels <- metadata$Dx;
plotDR(data=dim3, labels=labels);
Sys.sleep(300);

