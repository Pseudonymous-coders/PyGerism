import glob
import re
import time
from difflib import SequenceMatcher


startTime = time.time()
def isSimilar(a, b):
    return SequenceMatcher(None, a, b).ratio()


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

for i in range(len(essays)):
    for n in range(len(essays)):
        counter = 0
        copycats = []
        if n != i:
            for isentence in essays[i]['sentences']:
                for nsentence in essays[n]['sentences']:
                    if isSimilar(isentence, nsentence) > 0.6:
                        counter += 1
                        copycats.append([isentence,nsentence])
        if counter >= 1:
            print "Alert!! {} and {} are flagged {} times, here are the setences:".format(essays[i]['name'], essays[n]['name'], counter)
            for copycat in copycats:
                print copycat[0]
                print copycat[1]

            print "\n\n"
print "TIME:::::: "+(time.time() - startTime)
