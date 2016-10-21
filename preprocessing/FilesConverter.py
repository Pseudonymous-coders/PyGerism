from string import printable
from threading import Thread
from time import sleep
from splinter import Browser
from Configuration_Files import config

browser = Browser("phantomjs")
kill = False



def killer_thread(seconds):
    global kill
    kill = False
    print "killer thread started"
    for a in range(0, seconds):
        sleep(1)
    kill = True


def check_add():
    global browser
    if browser.is_element_present_by_text("X"):
        print "Found ad, deleting it"
        for item in browser.find_by_xpath('//div'):
            if item.text == "X":
                item.click()
                print "Deleted"
                break


def filters(text):
    printa = set(printable)
    return filter(lambda x: x in printa, text)


def odt_get_text(path):
    global browser, kill
    text = ""
    time = config.odt_kill_time
    tokill = Thread(target=killer_thread, args=(time,))
    tokill.setDaemon(True)
    tokill.setName("Killer_Thread")
    tokill.start()
    if kill:
        print "ODT took too long"
        return text
    browser.visit("http://www.convertfiles.com/convert/document/ODT-to-TXT.html")
    print "Uploading odt"
    if kill:
        print "ODT took too long"
        return text
    browser.attach_file('uploadedfile', path)
    print "Uploaded"
    if kill:
        print "ODT took too long"
        return text
    try:
        print "Selecting output"
        browser.find_by_xpath('//select[@name="output_format"]//option[@value=".txt"]').first.click()
        print "Output set to .txt\nConverting..."
        browser.find_by_xpath('//input[@type="submit" and @value="Convert"]').first.click()
        print "Converted page"
    except Exception:
        print "error"
        check_add()
        print "Selecting output"
        browser.find_by_xpath('//select[@name="output_format"]//option[@value=".txt"]').first.click()
        print "Output set to .txt\nConverting..."
        browser.find_by_xpath('//input[@type="submit" and @value="Convert"]').first.click()
        print "Converted page"
    if kill:
        print "ODT took too long"
        return text
    sleep(10)
    if kill:
        print "ODT took too long"
        return text
    try:
        print "Finding download link (First page)"
        element = browser.find_by_xpath('//a')
        for thing in element:
            if str("download page") in str(thing.text.lower()):
                print "found it"
                thing.click()
                break
            if kill:
                print "ODT took too long"
                return text
    except Exception:
        print "error"
        check_add()
        print "Finding download link (First page)"
        element = browser.find_by_xpath('//a')
        for thing in element:
            if "the download page" in thing.text:
                print "found it"
                thing.click()
                break
            if kill:
                print "ODT took too long"
                return text
    if kill:
        print "ODT took too long"
        return text
    sleep(6)
    if kill:
        print "ODT took too long"
        return text
    try:
        print "Finding link to grab text"
        element = browser.find_by_xpath('//a')
        for thing in element:
            if str("convertfiles.com") in str(thing.text.decode('ascii', 'ignore')):
                print "Found it\n %s" % filters(str(thing.text.replace(" ", "").strip()))
                bro = Browser("phantomjs")
                bro.visit(str(filters(thing.text)).decode('ascii', 'ignore').replace(" ", "").strip())
                text = bro.find_by_xpath("//pre").first.text
                bro.quit()
                break
            if kill:
                print "ODT took too long"
                return text
    except Exception:
        print "error"
        check_add()
        element = browser.find_by_xpath('//a')
        for thing in element:
            if str("convertfiles.com") in str(thing.text.decode('ascii', 'ignore')):
                print "Found it\n %s" % filters(str(thing.text.replace(" ", "").strip()))
                bro = Browser("phantomjs")
                bro.visit(str(filters(thing.text)).decode('ascii', 'ignore').replace(" ", "").strip())
                text = bro.find_by_xpath("//pre").first.text
                bro.quit()
                break
            if kill:
                print "ODT took too long"
                return text
    print "Done converting"
    return text
