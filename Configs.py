from Tkinter import *
import ttk
from threading import Thread
from time import sleep
from preprocessing.Configuration_Files.config import Configurator

sense_res = 0.05
both_res = 10
set_pass = True
sleep_thread = 3


def exit_app():
    gui.destroy()
    Tk().withdraw()
    print "Exiting BaileyChecker..."
    exit(0)


def base_fix(widget, valuer, text):
    global set_pass
    value = widget.get()

    movement = round(float(value) % float(valuer))

    if movement != 0.0:
        try:
            toset = round(value + movement)
            if set_pass:
                set_pass = False
                widget.set(toset)
            text.set(str(toset))
        except Exception as err:
            print "Configuration error: " + str(err)
    else:
        set_pass = True


def resolution_sense_fix(e=None):
    global ss, sense_var
    base_fix(ss, sense_res, sense_var)


def resolution_res_horz_fix(e=None):
    global w, horz_var
    base_fix(w, both_res, horz_var)


def resolution_res_vert_fix(e=None):
    global w, vert_var
    base_fix(w, both_res, vert_var)


def fix_refreshing():
    while True:
        print "Refreshing gui"
        resolution_sense_fix()
        resolution_res_horz_fix()
        resolution_res_vert_fix()
        sleep(sleep_thread)

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

sense_var = StringVar()
horz_var = StringVar()
vert_var = StringVar()

sl = ttk.Label(gui, text="Select a sensitivity (0 - 100) aka (0% to 100%) exactness")
sl.pack()
slp = ttk.Label(gui, textvariable=sense_var)
slp.pack()
ss = ttk.Scale(gui, from_=1, to=100, orient=HORIZONTAL, command=resolution_sense_fix)
print "Current Height: %d" % Configurator.WIN_HEIGHT
ss.set(Configurator.SENSITIVITY * 100)
sense_var.set(str(Configurator.SENSITIVITY * 100))
ss.pack()

swl = ttk.Label(gui, text="Select widget screen width")
swl.pack()
swlp = ttk.Label(gui, textvariable=horz_var)
swlp.pack()
w = ttk.Scale(gui, from_=60, to=1920, orient=HORIZONTAL, command=resolution_res_horz_fix)
print "Current Width: %d" % Configurator.WIN_WIDTH
w.set(Configurator.WIN_WIDTH)
horz_var.set(str(Configurator.WIN_WIDTH) + " px")
w.pack()

shl = ttk.Label(gui, text="Select widget screen height")
shl.pack()
shlp = ttk.Label(gui, textvariable=vert_var)
shlp.pack()
h = ttk.Scale(gui, from_=60, to=1080, orient=HORIZONTAL, command=resolution_res_vert_fix)
print "Current Height: %d" % Configurator.WIN_HEIGHT
h.set(Configurator.WIN_HEIGHT)
vert_var.set(str(Configurator.WIN_HEIGHT) + " px")
h.pack()

firstv = None

if len(available_themes) > 0:
    print "Found some themes"
    tl = ttk.Label(gui, text="Select a theme")
    tl.pack()

    firstv = StringVar(gui)
    firstv.set(Configurator.WIN_THEME)

    to = apply(ttk.OptionMenu, (gui, firstv) + tuple(available_themes))
    to.pack()

else:
    print "No themes found"
    tl = ttk.Label(gui, text="No themes found!")
    tl.pack()


def on_save():
    Configurator.WIN_WIDTH = w.get()
    Configurator.WIN_HEIGHT = h.get()
    Configurator.SENSITIVITY = float(ss.get()) / 100

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

refresh = Thread(target=fix_refreshing)
refresh.setDaemon(True)
refresh.setName("Bailey refresh")
refresh.start()


if __name__ == "__main__":
    gui.mainloop()
