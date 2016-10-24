from threading import Thread
from GUI import MessageDialog, FileDialog
from pytexter import Docxer
from Configuration_Files.config import Configurator
from Tkinter import *
from os.path import sep as pathsep, basename
from processing import processor
import ttk


def exit_app():
    gui.destroy()
    Tk().withdraw()
    print "Exiting BaileyChecker..."
    exit(0)


Configurator.load_config()

# Set up main window
gui = Tk()
gui.geometry("%dx%d" % (Configurator.WIN_WIDTH, Configurator.WIN_HEIGHT))
gui.title("BaileyChecker plagiarism analyzer")
gui.protocol("WM_DELETE_WINDOW", exit_app)
gui.wm_resizable(Configurator.WIN_WIDTH, Configurator.WIN_HEIGHT)
gui.style = ttk.Style()

print "Available Tkinter themes: " + str(gui.style.theme_names())
print "Using theme: " + Configurator.WIN_THEME

#gui.style.theme_use(Configurator.WIN_THEME)

# Setup progress bar
progress = ttk.Progressbar(gui, orient="horizontal", length=Configurator.WIN_WIDTH, mode="determinate")
progress.pack()
progress["maximum"] = 100  # Obviously 100%
progress["value"] = 0  # Start off at 0

# Setup below text
update_text = StringVar()
text_label = ttk.Label(gui, textvariable=update_text)
text_label.pack()
update_text.set("0% : Starting...")

# Cancel button
cancel_text = StringVar()
cancel_button = ttk.Button(gui, textvariable=cancel_text, command=exit_app)
cancel_text.set("Cancel")
cancel_button.pack()

dialog = FileDialog()
message = MessageDialog("ERROR", "Couldn't load the requested folder")

doc = Docxer()
path = None


def set_progress(current_progress, text):
    global gui, progress, update_text
    progress["value"] = current_progress
    update_text.set("%d%% : %s" % (current_progress, text))


def graph_all(new_data):
    from BarRer import BarRer
    bargraph = BarRer("Bailey Checker Report")

    Xdata = []
    Ydata = []
    Names = []

    for name, copyname, counts in new_data:
        Xdata.append(str(name))
        Ydata.append(str(counts))
        Names.append(str("UGABUGA"))

    bargraph.add_bar(Xdata, Ydata, Names)
    bargraph.show()

    cancel_text.set("Done")
    set_progress(100, "Finished! Click 'Done' to Exit")


def on_start_app(run_type):
    global path

    if not run_type[0]:
        message.show()
        exit_app()

    raw_folder = run_type[1]
    folder_use = raw_folder + (pathsep if raw_folder[-1] != pathsep else "")
    set_progress(0, "Folder selected: " + folder_use)
    files = doc.get_folder(folder_use)

    print "Using folder: " + str(folder_use)

    new_folder = folder_use + "Examined" + pathsep
    documents_sizes = doc.run_files(folder_use, files, new_folder, set_progress)

    set_progress(10, "Done converting all essays into %s" % basename(new_folder))

    processor.load_folder(new_folder)
    processor.process_essays()
    new_data = processor.main_scan(set_progress)

    set_progress(100, "Loading graphs, Please wait...")
    graph_all(new_data)

    '''
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
    '''


def main():
    print "Welcome to pyscape a python program to detect plagiarism in Essays" \
          "\nDeveloped by: David Smerkous and Eli Smith" \
          "\nFor Bailey and his naughty students"

    run_type = dialog.get_folder()

    main_app = Thread(target=on_start_app, args=(run_type,))
    main_app.setDaemon(True)
    main_app.start()

    gui.mainloop()

if __name__ == "__main__":
    main()
