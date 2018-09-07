#
# Oh, gods.
#
# Note that there is some crazy nonsense here. PCA with missing values isn't a defined
# operation, and since I've omited betas with p > 0.01, we have lots of missing data.
# I've explored some options. The standard steps of PCA are:
#	1. center the data
#	2. calculate the covariance matrix (NAs get propagated)
#	3. find eigenvalues and vectors (undefined with NAs)
#	4. choose vectors based on |eigenvalues|
#	5. new data = centered data * [v1 v2 .. vd]
# OR, use an SVD-based approach that is said to be more reliable, which also breaks
# down when there are missing values. Some use the "pairwise" option as described in
# Matlab here http://www.mathworks.com/help/stats/pca.html#bttveib-1, but that's not
# really best practise as described here http://www.r-bloggers.com/pairwise-complete-correlation-considered-dangerous/
# and here http://stats.stackexchange.com/questions/35561/imputation-of-missing-values-for-pca.
# So what to do? There exist a few methods, the easiest of which is interpolation.
# More difficult methods are described and compared here http://menugget.blogspot.de/2014/09/pca-eof-for-data-with-missing-values.html
# but honestly I'm not going to try that first. For now, I've decided to simply set
# missing values to 0 after the data is centered and scaled. Intuitively that corresponds
# to "no signal". It does depresses the variance of the corresponding CpG slightly, but
# I take this to be fine, since we should want to consider CpGs with lots of missing
# data less important anyway.

residuals <- read.csv("../data/2_residuals_SZ_vs_control.csv", row.names=1)
residuals <- t(residuals)#so 1st dimension is sample and 2nd is CpGs (features)

residuals <- scale(residuals)#center around zero and let stddev = 1
residuals[is.na(residuals)] <- 0#lowers stddev of cols with lots of NAs, which makes them
								#less important, which is intuitively fine
pcs <- prcomp(residuals)#works now because no missing values
#print(names(pcs))
variances <- pcs[1]$sdev^2

png('pcs_all.png')
plot(variances)
title(main="All PCs")
png('pcs_top_20.png')
plot(variances[1:20])
title(main="20 Most Significant PCs")

pcdata <- pcs[5]$x
write.csv(pcdata, file="../data/residuals_PCs_421103CpGs.csv", quote=F);
