from threading import Thread
from time import sleep
from GUI import ProgressBar, MessageDialog, FileDialog
from pytexter import Docxer
#from copyScape import makeReport
from math import factorial

def exit_app(na):
    gui.destroy()
    message.destroy()
    try:
        use = str(na)[20]
    except IndexError:
        use = str(na)
    print "Usable callback: %s" % use
    exit(0)


gui = ProgressBar(800, 100, exit_app)
dialog = FileDialog(gui.get_parent())
message = MessageDialog(gui.get_parent(), "ERROR", "Couldn't load the requested folder")

doc = Docxer()
path = None


def slow_move(move):
    for a in range(move):
        gui.add_percent(1)
        sleep(0.8)


def on_start_app():
    global path
    print "Analyzer Started\n\n"
    gui.set_percent(0)
    gui.set_text("Reading and Converting the files")
    print "Getting files..."
    files = doc.get_folder(path)
    gui.set_percent(2)
    gui.set_text("Reading and Converting the files")
    gui.set_prog_text(len(files))
    gui.set_percent(6)
    gui.set_text("Reading and Converting the files")
    print "Reading and converting..."
    doc.run_files(path, files, path + "/Examined/")
    print "Examined/Converted"
    gui.set_percent(30)
    gui.set_text("DONE reading and converting")
    sleep(2)
    gui.set_percent(35)
    gui.set_text("Analyzing essays")
    Thread(target=slow_move, args=(60,)).start()
    factorial(len(files))
    makeReport.getReport(path + "/Examined/")
    gui.set_percent(100)
    gui.set_text("Done!!!")
    gui.set_cancel_text("DONE")
    print "Finished all of the examinations"


if __name__ == "__main__":
    folder_use = "/root/SCAN/"
    files = doc.get_folder(folder_use)
    doc.run_files(folder_use, files, folder_use + "/Examined/")
    '''
    dialog_response, path = dialog.get_folder()
    if dialog_response:
        exit_app(0)
    if path is None:
        message.connect(exit_app)
        message.show()
        exit_app(0)
    path += "/"
    exit_app(0) if path is None else path
    gui.start(on_start_app)
    '''
