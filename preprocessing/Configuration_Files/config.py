import json

from os.path import dirname

'''
run_docx = True  # If you want to run/process docx files
run_pdf = True  # If you want to run/process PDF files
run_odt = False  # If you want to run/process odt files (if true, then it requires internet connection and takes about
# 60 seconds per file to turn into text
odt_kill_time = 60  # Seconds to kill each odt, if it takes too long to process
min_yellow = 60
min_red = 85

# WINDOW OPTIONS
win_width = 600
win_height = 70
win_theme = "clam"
'''


class Configurator:
    def __init__(self):
        pass

    parsed = None
    path = dirname(__file__)
    path += ("/" if path[-1] != "/" else "") + "bailey_config.json"

    RUN_DOCX = True
    RUN_PDF = True
    RUN_ODT = True

    ODT_KILL_TIME = 60
    MIN_YELLOW = 60
    MIN_RED = 85

    WIN_WIDTH = 600
    WIN_HEIGHT = 70
    WIN_THEME = "clam"

    CONFIG_WIDTH = 400
    CONFIG_HEIGHT = 300

    SENSITIVITY = 0.6

    @staticmethod
    def load_config():
        try:
            with open(Configurator.path, "r") as config_file:
                Configurator.parsed = json.loads(config_file.read())

            Configurator.RUN_DOCX = Configurator.parsed["proc_docs"]
            Configurator.RUN_PDF = Configurator.parsed["proc_pdf"]
            Configurator.RUN_ODT = Configurator.parsed["proc_odt"]
            Configurator.ODT_KILL_TIME = Configurator.parsed["odt_kill_time"]
            Configurator.WIN_WIDTH = Configurator.parsed["window"]["width"]
            Configurator.WIN_HEIGHT = Configurator.parsed["window"]["height"]
            Configurator.WIN_THEME = Configurator.parsed["window"]["theme"]
            Configurator.SENSITIVITY = Configurator.parsed["sensitivity"]
        except Exception as err:
            print "Failed loading config: " + str(err)

    @staticmethod
    def save_config():
        if Configurator.parsed is None:
            print "Failed saving config"
            return
        try:
            Configurator.parsed["proc_docs"] = Configurator.RUN_DOCX
            Configurator.parsed["proc_pdf"] = Configurator.RUN_PDF
            Configurator.parsed["proc_odt"] = Configurator.RUN_ODT
            Configurator.parsed["odt_kill_time"] = Configurator.ODT_KILL_TIME
            Configurator.parsed["window"]["width"] = Configurator.WIN_WIDTH
            Configurator.parsed["window"]["height"] = Configurator.WIN_HEIGHT
            Configurator.parsed["window"]["theme"] = Configurator.WIN_THEME
            Configurator.parsed["sensitivity"] = Configurator.SENSITIVITY

            with open(Configurator.path, "w") as config_file:
                tosave = json.dumps(Configurator.parsed)
                config_file.write(tosave)
        except Exception as err:
            print "Failed saving config: " + str(err)
