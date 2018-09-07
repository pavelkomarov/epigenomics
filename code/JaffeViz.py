
import numpy as np
import matplotlib.pyplot as plt

file = open('../data/jaffe_metadata.csv')
#data = np.array([map(float,s.strip().split(',')) for s in inf.readlines()])

line = file.readline().split(',')
for i in range(len(line)):
	entry = line[i]
	if "dropsample (whether to remove the sample for failing quality control)" in entry:
		dropsamplecol = i
	elif "bestqc (best sample to use when more than 1 array were run on the same subject/brnum)" in entry:
		bestqccol = i
	elif "age (in years)" in entry:
		agecol = i
	elif "plate (processing plate)" in entry:
		platecol = i
	elif "group" in entry:
		groupcol = i

controlages = []
schizoages = []

for line in file.readlines()[1:]:
	linelist = line.split(',')
	if "TRUE" not in linelist[dropsamplecol] and \
		"FALSE" not in linelist[bestqccol] and \
		float(linelist[agecol]) >= 16 and \
		"Lieber_244" not in linelist[platecol]:
		
		if "Control" in linelist[groupcol]:
			controlages.append(float(linelist[agecol]))
		elif "Schizo" in linelist[groupcol]:
			schizoages.append(float(linelist[agecol]))

print len(controlages)
print len(schizoages)


#bar chart nonsense. python is so shitty.
fig, ax = plt.subplots()
rect1 = plt.bar(np.arange(1), height=np.mean(controlages),
            width=0.35, color='MediumSlateBlue',
            yerr=np.std(controlages),                 
            error_kw={'ecolor':'Black',
                          'linewidth':2})
rect2 = plt.bar(np.arange(1)+0.35*1.5, height=np.mean(schizoages),
            width=0.35, color='Tomato',
            yerr=np.std(schizoages),
            error_kw={'ecolor':'Black',
                          'linewidth':2})
ax.set_ylabel('Age')
ax.set_title('Age comparison of groups')
ax.set_xticks(np.arange(2)*0.35*1.5 + 0.35/2)
ax.set_xticklabels(('Control', 'Schizo'))
plt.show()

