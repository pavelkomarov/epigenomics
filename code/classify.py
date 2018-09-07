import numpy as np
import pandas as pd
import KNNLearner as knn
import matplotlib.pyplot as plt

if __name__=="__main__":
	maxk = 180

	#PCA data
	data = pd.read_csv('../data/3_residPCs_SZ_vs_control.csv', index_col=0)
	data = data.ix[:,0:18]#first d PCs
	data = data.divide(data.std(axis=0).values, axis=1)#for standardizing all PCAs (optional)
	print data.std(axis=0)

	#Isomap data
	#data = pd.read_csv('../data/3_residiso_k2_SZ_vs_control.csv', index_col=0)
	#data = data.filter(regex='dim18\.')
	#data = data.divide(data.std(axis=0).values, axis=1)#for standardizing all dimensions (optional)
	#print data.std(axis=0)

	metadata = pd.read_csv('../data/1_jaffe_metadata_SZ_vs_control.csv')

	hyper_train_accs = []#accs = accuracies
	hyper_test_accs =[]
	hyper_random_accs = []
	for k in range(1,maxk):#iterate hyperparameter k
		print '===== k =',k,'====='

		correct = 0
		randcorrect = 0
		in_sample_accs = []
		for i in range(data.shape[0]):#leave-one-out testing because we have little data
			train_rows = [j for j in range(data.shape[0]) if j != i]
			test_rows = range(i,i+1)#it's only one item, but this way it's a list
			trainX = data.values[train_rows]#take first 10 PCs
			trainY = metadata.values[train_rows,6]#sixth column contains disease state
			testX = data.values[test_rows]#.values gets a np.ndarray
			testY = metadata.values[test_rows,6]

			kenneth = knn.KNNLearner(k=k, verbose=False)
			kenneth.addEvidence(trainX, trainY)#train a new model on all data but the ith thing

			# evaluate performance in sample
			predY = kenneth.query(trainX)
			in_sample_accs.append(np.sum(predY==trainY)/float(trainY.shape[0]))

			# evaluate performance out of sample (just the one we left out)
			predY = kenneth.query(testX)
			randpred = np.random.choice(['Schizo','Control'],p=[0.443,0.557])#p reflects dataset
			if predY[0]==testY[0]: correct += 1
			if randpred==testY[0]: randcorrect += 1

		#print stuff so I can sort of see what's going on while it runs
		print "In sample results"
		print "Average accuracy: ",np.mean(in_sample_accs)*100,"%"
		print "\nOut of sample results"
		print "Correctly classified",correct,"of",len(data),"(",float(correct)/len(data)*100,"%)"
		print "Random gets",randcorrect,"of",len(data),"(",float(randcorrect)/len(data)*100,"%)\n"

		#keep track of things so I can plot them
		hyper_train_accs.append(np.mean(in_sample_accs)*100)
		hyper_test_accs.append(float(correct)/len(data)*100)
		hyper_random_accs.append(float(randcorrect)/len(data)*100)
	#plot accuracies over the whole range of k
	plt.plot(range(1,maxk), hyper_train_accs, 'b', label='train')
	plt.plot(range(1,maxk), hyper_test_accs, 'r', label='test')
	plt.plot(range(1,maxk), hyper_random_accs, 'g', label='weighted random')
	plt.xlabel('k')
	plt.ylabel('accuracy as %')
	plt.legend()
	plt.show()
