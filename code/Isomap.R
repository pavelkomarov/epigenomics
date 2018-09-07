#https://www.bioconductor.org/packages/devel/bioc/vignettes/RDRToolbox/inst/doc/vignette.pdf
library(RDRToolbox)

residuals <- read.csv("../data/2_residuals_SZ_vs_control.csv", row.names=1);
residuals <- na.omit(residuals);#I'm choosing to handle NAs by excluding them here, which
#reduces the number of CpGs we can consider from ~420k to ~390k. This might be wrong. Note
#that in the comments of GetPrincipleComponents.R I describe how I center and scale the
#data (as recommended for PCA) and then set NAs to 0. This also might be wrong.
residuals <- t(residuals);#so 1st dimension is sample and 2nd is CpGs (features) (NxD)

#This version of Isomap doesn't use a distance cutoff to decide what should be an edge in
#the distances adjacency matrix (which describes a graph); it just uses the k nearest neighbors
#and makes edges to them, weighted by the distance between those points, so in the
#final graph every node has k neighbors (adjacency matrix is k x k).
#
#"The connectivity of each data point in the neighborhood graph is defined as its nearest k
#Euclidean neighbors in the high-dimensional space. This step is vulnerable to "short-circuit
#errors" if k is too large with respect to the manifold structure or if noise in the data moves
#the points slightly off the manifold.[3] Even a single short-circuit error can alter many entries
#in the geodesic distance matrix, which in turn can lead to a drastically different (and incorrect)
#low-dimensional embedding. Conversely, if k is too small, the neighborhood graph may become too
#sparse to approximate geodesic paths accurately." -Wikipedia
#
#I've chosen k=2 because it gives the best fit to our features, that is, achieves the lowest residual
#variance with the fewest isomapped features. (I checked this on the full dataset in PACE.)
#
#dims = d, where we are reducing an N x D thing to an N x d thing.
residiso <- Isomap(data=residuals, dims=1:20, k=2, plotResiduals=TRUE);#saves plot as Rplots.pdf with the code

#save a 2D thing as a png. For some reason saving 3D plots isn't working, so just do this in the cloud
metadata <- read.csv("../data/1_jaffe_metadata_SZ_vs_control.csv");
labels <- metadata$Dx;
png('isos2.png');
plotDR(data=residiso$dim2, labels=labels, axesLabels=c("", ""), legend=TRUE);
title(main="Disease State in a 2D Isomap-generated feature space");
dev.off();#close png

#show interactive 3D plot for 30 seconds. Note this isn't useful in the cluster, and for some reason I
#can't get it to save. (I think it's a bug in the library.) To make up for this, I've created first3isos.R,
#which you can tweak and run after this script finishes to view where Isomap places points in 3-space.
#labels <- metadata$Dx;
#plotDR(data=residiso$dim3, labels=labels);
#Sys.sleep(30);

write.csv(residiso, row.names=rownames(residuals), file="../data/3_residiso_k2_SZ_vs_control.csv", quote=F);

