from Tkinter import *
import ttk
from preprocessing.Configuration_Files.config import Configurator


def exit_app():
    gui.destroy()
    Tk().withdraw()
    print "Exiting BaileyChecker..."
    exit(0)


Configurator.load_config()

# Set up main window
gui = Tk()
gui.geometry("%dx%d" % (Configurator.CONFIG_WIDTH, Configurator.CONFIG_HEIGHT))
gui.title("BaileyChecker Configurations")
gui.protocol("WM_DELETE_WINDOW", exit_app)
gui.wm_resizable(Configurator.CONFIG_WIDTH, Configurator.CONFIG_HEIGHT)
gui.style = ttk.Style()

available_themes = gui.style.theme_names()

print "Available Tkinter themes: " + str(available_themes)
print "Using theme: " + Configurator.WIN_THEME

gui.style.theme_use(Configurator.WIN_THEME)

sl = Label(gui, text="Select a sensitivity (0 - 1) aka (0% to 100%) exactness")
sl.pack()
ss = Scale(gui, from_=0, to=1, orient=HORIZONTAL, resolution=0.05)
print "Current Height: %d" % Configurator.WIN_HEIGHT
ss.set(Configurator.SENSITIVITY)
ss.pack()


swl = Label(gui, text="Select widget screen width")
swl.pack()
w = Scale(gui, from_=0, to=1920, orient=HORIZONTAL, resolution=10)
print "Current Width: %d" % Configurator.WIN_WIDTH
w.set(Configurator.WIN_WIDTH)
w.pack()

shl = Label(gui, text="Select widget screen height")
shl.pack()
h = Scale(gui, from_=0, to=1080, orient=HORIZONTAL, resolution=5)
print "Current Height: %d" % Configurator.WIN_HEIGHT
h.set(Configurator.WIN_HEIGHT)
h.pack()

firstv = None

if len(available_themes) > 0:
    print "Found some themes"
    tl = Label(gui, text="Select a theme")
    tl.pack()

    firstv = StringVar(gui)
    firstv.set(Configurator.WIN_THEME)

    to = apply(OptionMenu, (gui, firstv) + tuple(available_themes))
    to.pack()

else:
    print "No themes found"
    tl = Label(gui, text="No themes found!")
    tl.pack()


def on_save():
    Configurator.WIN_WIDTH = w.get()
    Configurator.WIN_HEIGHT = h.get()
    Configurator.SENSITIVITY = ss.get()

    if firstv is not None:
        toset = firstv.get()
        print "Setting theme: " + toset
        Configurator.WIN_THEME = toset

    print "Saving settings"
    Configurator.save_config()
    print "Saved"

    gui.destroy()
    Tk().withdraw()
    exit(0)

save = Button(gui, text="Save and Exit", command=on_save)
save.pack()

if __name__ == "__main__":
    gui.mainloop()
