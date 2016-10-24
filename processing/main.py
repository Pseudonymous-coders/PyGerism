import glob
import os
import re
import time
import pickle 
import tabulate
from multiprocessing import Process, queues
from time import sleep
from Levenshtein import ratio

startTime = float(time.time())

def isSimilar(a, b):
    return ratio(a,b)


def median(lst):
    sortedLst = sorted(lst)
    lstLen = len(lst)
    index = (lstLen - 1) // 2

    if (lstLen % 2):
        return sortedLst[index]
    else:
        return (sortedLst[index] + sortedLst[index + 1])/2.0

def getFiles(path):
    files = []
    names = []
    output = []
    rawFiles = glob.glob(path+"*.txt")
    for thing in rawFiles:
        f = open(thing, "r").read()
        files.append(f)
        if thing.rfind('\\') != -1:
            name = thing[int(thing.rfind('\\'))+1:]
        elif thing.rfind('/'):
            name = thing[int(thing.rfind('/'))+1:]
        else:
            name = thing
        names.append(name)
    output.append(names)
    output.append(files)
    return output

#Gets the essay text into propper format 
raw = getFiles("../essays/Examined/")
rawNames = raw[0]
rawEssays = raw[1]
essays = []

#Splits the essays up into a usable dictionary array
for i in range(len(rawEssays)):
    essayInfo = {}
    essayInfo['name'] = rawNames[i]
    essayInfo['raw'] = rawEssays[i]
    essayInfo['sentences'] = []
    rawParagraphs  = rawEssays[i].split("\n")
    tempParagraphs = []
    sentences = []
    for paragraph in rawParagraphs:
        if len(paragraph.split(" ")) > 20:
            tempParagraphs.append(paragraph)
    for paragraph in tempParagraphs:
        rawSentences = re.split('\. |\? |! ',paragraph)
        tempsentences = []
        for sentence in rawSentences:
            sentence = sentence.strip()
            words = int(len(sentence.split(" ")))
            if words > 5:
                essayInfo['sentences'].append(sentence)
    essays.append(essayInfo)

howMany = 0
perc = 0.0

def scan(name, chunker, essayz):
    global howMany
    for i in range(len(chunker)):
        howMany += 1
        chunker[i]['copies'] = [{}] * len(essayz)
        perc = float(float(howMany) / float(len(chunker)))
        print "Thread {} {}%".format(name, perc*100)
        for n in range(len(essayz)):
            chunker[i]['copies'][n] = {'name': essayz[n]['name'], 'counts':0, 'sentences': ["", ""]}
            for chunkySentence in chunker[i]['sentences']:
                for essaySentence in essayz[n]['sentences']:
                    if chunkySentence != essaySentence:
                        if isSimilar(chunkySentence, essaySentence) > 0.1:
                            chunker[i]['copies'][n]['sentences'].append((chunkySentence,essaySentence))
                            #chunker[i]['copies'].append({'name': essay['name'], 'sentence': [chunkySentence, essaySentence]})
		    	    #essays[howMany-1]['counts'] = counter
                            #essays[howMany-1]['copies'] = {'name':essay['name'], 'sentence': [chunkySentence, essaySentence]}
        '''
        new = old
        chunker[i]['copies'] = []
        for x in range(len(old)):
            new[x]['sentences'] = []
            for sentence in old[x]['sentences']:
                if sentence not in new[x]['sentences']:
                    new[x]['sentences'].append(old[x]['sentences'])
            new[x]['counts'] = len(new[x]['sentences'])   
        '''
        for x in range(len(chunker[i]['copies'])):
            chunker[i]['copies'][x]['counts'] = float(len(chunker[i]['copies'][x]['sentences']))

    with open(name+".pkl", "wb") as f:
        pickle.dump(chunker, f)
        f.close()


first = essays[0:][::2][:3]
second = essays[1:][::2][:3]

firstName = "first"
secondName = "second"

jobs = []
fi = Process(target=scan, args=(firstName, first, essays))
se = Process(target=scan, args=(secondName, second, essays))
jobs.extend([fi,se])
fi.start()
se.start()
fi.join()
se.join()

essays = []

with open(firstName+".pkl", "rb") as f, open(secondName+".pkl", "rb") as s:
    firstEssays = pickle.load(f)
    secondEssays = pickle.load(s)
    essays.extend(firstEssays)
    essays.extend(secondEssays)
os.remove(firstName+".pkl")
os.remove(secondName+".pkl")

count = 0
ticker = 0

for essay in essays:
    for copy in essay['copies']:
        count = float(count) + float(copy['counts'])
        ticker += 1
avg = float(count)/float(ticker)

print "Ticker", ticker
print "Avg", avg
print "Count", count
print "Essay #", len(essays)
for essay in essays:
    for copy in essay['copies']:
        if copy['sentences'] != []:
            print "{} and {} have {} counts, {} greater than the average ({})".format(essay['name'], copy['name'], copy['counts'], copy['counts'] - avg, avg)
            print "Copied sentences:"
            for pair in copy['sentences']:
                print pair[0]

            print "\n"
