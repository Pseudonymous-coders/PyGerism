import glob

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

raw = getFiles("../essays/Examined/")

rawNames = raw[0]
rawEssays = raw[1]

essays = []

for i in range(len(rawEssays)):
    essayInfo = {}
    essayInfo['name'] = rawNames[i]
    essayInfo['essay'] = rawEssays[i]
    essays.append(essayInfo)
print essays
