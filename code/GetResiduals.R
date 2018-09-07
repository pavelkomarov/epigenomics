
#read processed data and phenotype info, containing final number of individuals and CpGs
methylation <- read.csv("../data/1_processed_SZ_vs_control.csv", row.names=1);
metadata <- read.csv("../data/1_jaffe_metadata_SZ_vs_control.csv");

#Some CpGs have only M or F as sex or only AA or CAUC as ethnicity.
#This causes issues in lm() because it doesn't know how to fit to one "factor" only.
#So this function finds which things should be considered for the model on a particular CpG.
selectFn <- function(methRow) {
	naLocations <- which(is.na(methRow))#locations in the row
	oneSex <- length(unique(as.character(metadata$Sex)[-naLocations])) == 1
	oneEthnicity <- length(unique(as.character(metadata$Ethnicity)[-naLocations])) == 1
	if (oneSex && oneEthnicity) {#call lm() without both sex and ethnicity
		return("00")
	} else if (!oneSex && oneEthnicity) {#call lm() without ethnicity
		return("01")
	} else if (oneSex && !oneEthnicity) {#call lm() without sex
		return("10")
	} else {#call lm() normally
		return("00")
	}
}

#get back the residual methylation that, according to a linear fit, isn't
#due to sex, age, ethnicity, principle components, or cell composition
getResidLin <- function(beta, sex, age, ethnicity, pc1, pc2, pc3, pc4,
	es, npc, daneuron, neunpos, neunneg) {

	mode <- selectFn(beta)#first find out how to call lm()
	if (mode == "00") {
		linFit <- lm(beta ~ age + pc1 + pc2 + pc3 + pc4 +
			es + npc + daneuron + neunpos + neunneg, na.action=na.exclude)
	} else if (mode == "01") {#we have only one ethnicity for all non-NA values in this CpG
		linFit <- lm(beta ~ sex + age + pc1 + pc2 + pc3 + pc4 +
			es + npc + daneuron + neunpos + neunneg, na.action=na.exclude)
	} else if (mode == "10") {#we have only one sex for all non-NA values in this CpG
		linFit <- lm(beta ~ age + ethnicity + pc1 + pc2 + pc3 + pc4 +
			es + npc + daneuron + neunpos + neunneg, na.action=na.exclude)
	} else {#nothing missing
		linFit <- lm(beta ~ sex + age + ethnicity + pc1 + pc2 + pc3 + pc4 +
			es + npc + daneuron + neunpos + neunneg, na.action=na.exclude)
	} 
	return(resid(linFit))
}

#run the function for each CpG (each row in p matrix) using apply
residualMeth <- apply(methylation, 1, getResidLin,#1="rows" direction
	metadata$Sex, metadata$Age, metadata$Ethnicity, metadata$negControl_PC1,
	metadata$negControl_PC2, metadata$negControl_PC3, metadata$negControl_PC4,
	metadata$ES, metadata$NPC, metadata$DA_NEURON, metadata$NeuN_pos,
	metadata$NeuN_neg)
residualMeth <- t(residualMeth)#transpose the matrix

write.csv(residualMeth, file="../data/2_residuals_SZ_vs_control.csv", quote=F);
