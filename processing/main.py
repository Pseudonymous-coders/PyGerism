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
            chunker[i]['copies'][n] = {'name': essayz[n]['name'], 'counts':0, 'sentences': []}
            for chunkySentence in chunker[i]['sentences']:
                for essaySentence in essayz[n]['sentences']:
                    if chunkySentence != essaySentence:
                        if isSimilar(chunkySentence, essaySentence) > 0.6:
                            chunker[i]['copies'][n]['sentences'].append([chunkySentence,essaySentence])
                            #chunker[i]['copies'].append({'name': essay['name'], 'sentence': [chunkySentence, essaySentence]})
		    	    #essays[howMany-1]['counts'] = counter
                            #essays[howMany-1]['copies'] = {'name':essay['name'], 'sentence': [chunkySentence, essaySentence]}

        for x in range(len(chunker[i]['copies'])):
            oldSent = chunker[i]['copies'][x]['sentences']
            chunker[i]['copies'][x]['sentences'] = []
            for y in range(len(oldSent)):
                for z in range(len(oldSent[y])):
                    if oldSent[y][z][-1] != ".":
                        oldSent[y][z] += "."
                if oldSent[y] not in chunker[i]['copies'][x]['sentences']:
                    chunker[i]['copies'][x]['sentences'].append(oldSent[y])


        for x in range(len(chunker[i]['copies'])):
            chunker[i]['copies'][x]['counts'] = float(len(chunker[i]['copies'][x]['sentences']))

    with open(name+".pkl", "wb") as f:
        pickle.dump(chunker, f)
        f.close()


numOfEssays = 20
first = essays[0:][::2][:numOfEssays/2]
second = essays[1:][::2][:numOfEssays/2]

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
        if copy['counts'] > 0:
            count = float(count) + float(copy['counts'])
            ticker += 1
avg = float(count)/float(ticker)
breakpoint = avg + (avg/2)

print "Ticker", ticker
print "Avg", avg
print "Breakpoint", breakpoint
print "Count", count
print "Essay #", len(essays)

table = []

headers = ["Name", "Copyer name", "Counts", "Sentences"]

for essay in essays:
    for copy in essay['copies']:
        if copy['counts'] > breakpoint: 
            if copy['sentences'] != []:
                newPairs = ""
                for a, b in enumerate(copy['sentences'], 1):
                    newPairs += "{}. Original: {}         Copy: {}".format(a, b[0], b[1])
                table.append([essay['name'], copy['name'], copy['counts']])

table = sorted(table, key=lambda x: x[2])[::-1]

with open("Report.txt", "w") as rep:
    rep.write(tabulate.tabulate(table, headers, tablefmt="grid"))

