"""
A simple way to classify things
This implementation uses dataframes instead of numpy arrays
"""

import numpy as np
import pandas as pd

class KNNLearner(object):

    def __init__(self, k = 3, verbose = False):
        self.k = k
        self.Xtrain = None
        self.Ytrain = None

    def addEvidence(self,dataX,dataY):
        """
        @summary: Add training data to learner
        @param dataX: X values of data to add
        @param dataY: the Y training values
        """
        if self.Xtrain == None:
            self.Xtrain = dataX
        else:
            self.Xtrain = pd.concat([self.Xtrain, dataX])
        if self.Ytrain == None:
            self.Ytrain = dataY
        else:
            self.Ytrain = pd.concat([self.Ytrain, dataY])
        
    def query(self,points):
        """
        @summary: Estimate a set of test points given point-cloud distance
        @param points: should be a numpy array with each row corresponding to a specific query.
        @returns the estimated values according to the saved model.
        """
        Ytest = []
        for Xtest in points:
            dists = np.linalg.norm(self.Xtrain.values[:,:] - Xtest.values[:,:], axis=1)
            knindices = np.argsort(dists)[0:self.k].tolist()
            #Ytest += [self.Ytrain[knindices].sum()/self.k]#average for Regression
            Ytest += max(set(Ytrain.values[knindices]), \
                key=Ytrain.values[knindices].tolist().count)#vote for classification
        return pd.DataFrame(Ytest, index=points.index, columns=['Dx'])
