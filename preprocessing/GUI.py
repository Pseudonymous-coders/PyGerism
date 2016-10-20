from threading import Thread
from gi import require_version

require_version('Gtk', '3.0')
from gi.repository import Gtk, GObject


class MessageDialog:
    def __init__(self, parent, title, text):
        self.message = Gtk.MessageDialog(parent=parent, title=title)
        self.message.set_border_width(3)
        self.label = Gtk.Label(text)
        self.button = Gtk.Button("Okay")

    def connect(self, function):
        self.button.connect("clicked", function)

    def show(self):
        box = self.message.get_content_area()
        box.add(self.label)
        box.add(self.button)
        self.label.show()
        self.button.show()
        self.message.run()

    def destroy(self):
        self.message.destroy()


class FileDialog:
    def __init__(self, parent):
        self.dialog = Gtk.FileChooserDialog("Please choose a folder", parent,
                                            Gtk.FileChooserAction.SELECT_FOLDER, buttons=
                                            (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL,
                                             Gtk.STOCK_OPEN, Gtk.ResponseType.OK))
        self.path = None

    def get_folder(self):
        self.dialog.show_all()
        response = self.dialog.run()
        if response == Gtk.ResponseType.CANCEL:
            print "User hit cancel"
            self.dialog.destroy()
            return [True, self.path]
        self.path = self.dialog.get_filename()
        print "User opened folder\nPath:", self.path
        self.dialog.destroy()
        return [False, self.path]


class ProgressBar:
    def __init__(self, width, height, on_quit, title="Scanning progress"):
        GObject.threads_init()
        self.win = Gtk.Window(default_height=height, default_width=width)
        self.win.connect("delete-event", on_quit)
        self.win.set_border_width(5)
        self.win.resize(width, height)
        self.win.set_resizable(False)
        self.progress = Gtk.ProgressBar(show_text=True)
        self.prog = Gtk.Label()
        self.prog.set_markup('<i>Progress: </i>')
        self.first_step = Gtk.Button("Cancel")
        self.first_step.connect("clicked", on_quit)
        self.box = Gtk.Box()
        self.box.add(self.prog)
        self.box.add(self.progress)
        self.box.add(self.first_step)
        self.win.add(self.box)
        self.win.set_title(title)
        self.win.set_icon_from_file("eye.ico")


    def set_cancel_text(self, text):
        self.first_step.set_label(text)

    def set_prog_text(self, files):
        if files is not None:
            self.prog.set_markup('<i>Progress: (' + str(files) + ') files</i>')
        else:
            self.prog.set_markup('<i>Progress: </i>')

    def set_text(self, toset):
        toset = "" if "%" in str("" if toset is None else toset) else toset
        self.progress.set_text(str(toset + ": {}%".format(100 * self.progress.get_fraction()))
                               if toset is not "" else str(100 * self.progress.get_fraction()) + " %")

    def set_percent(self, percent):
        self.progress.set_fraction(float(percent) / 100)
        current = "Running...:" if self.progress.get_text() is None else self.progress.get_text()
        self.set_text(str((current[current.index(":"):])) if ":" in current else "")

    def add_percent(self, toadd):
        self.set_percent((100 * self.progress.get_fraction()) + toadd)

    def remove_percent(self, torem):
        self.set_percent((100 * self.progress.get_fraction()) - torem)

    def start(self, target):
        thread = Thread(target=target)
        thread.daemon = True
        thread.start()
        self.win.show_all()
        Gtk.main()

    def get_parent(self):
        return self.win

    def destroy(self):
        self.win.destroy()
