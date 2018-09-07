

%T = readtable('../data/1_processed_control_vs_youth.csv','TreatAsEmpty','NA');%contains all the main data before resid(linear())
T = readtable('../data/2_residuals_control_vs_youth.csv','TreatAsEmpty','NA');%contains all the main residual data
data = T{:,2:end};%ensure not cell array
probe_names = T.(1);
exp_names = T.Properties.VariableNames(2:end);

M = readtable('../data/1_jaffe_metadata_control_vs_youth.csv');
color_code_names = {'Plate','Sex','Ethnicity','Dx','Age','ES','NPC','DA_NEURON','NeuN_pos','NeuN_neg'}';
color_code_vectors = [];
for i = 1:4%through disease state (everything after is numeric)
	color_code_vectors = [color_code_vectors; findgroups(M.(color_code_names{i})')];
end
for i = 5:10
	[N, edges] = histcounts(M.(color_code_names{i}));%bucketize first
	color_code_vectors = [color_code_vectors; discretize(M.(color_code_names{i})', edges)];
end

clear i
clear N
clear edges
clear T
clear M

save('../data/control_vs_youth_resid.mat')