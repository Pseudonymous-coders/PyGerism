from argparse import ArgumentParser
from preprocessing.Configuration_Files.config import Configurator

if __name__ == "__main__":
    print "Running pre-processing unit"
    try:
        # noinspection PyBroadException
        try:
            Configurator.load_config()
        except Exception as err:
            print "Error loading config: " + str(err)

        parser = ArgumentParser(description='BaileyChecker a plagiarism checking software kit')
        parser.add_argument('--width', help='Set window width must be above 0', type=int, default=-1)
        parser.add_argument('--height', help='Set window height must be above 0', type=int, default=-1)
        parser.add_argument('--sensitivity', help='Set sentence sensitivity (0 - 100)', type=int, default=-1)
        parser.add_argument('--theme', help='Set the theme of BaileyChecker', type=str, default="")
        parser.add_argument('--listthemes', help='List available themes', action='store_true')

        args = parser.parse_args()

        twidth = args.width
        theight = args.height
        tsens = args.sensitivity
        ttheme = args.theme

        changed_something = False

        if twidth > 0:
            changed_something = True
            print "Setting new width: %d" % twidth
            Configurator.CONFIG_WIDTH = twidth

        if theight > 0:
            changed_something = True
            print "Setting new height: %d" % theight
            Configurator.CONFIG_HEIGHT = theight

        if tsens > 0:
            changed_something = True
            print "Setting new sensitivity: %d" % tsens
            Configurator.SENSITIVITY = tsens

        if len(ttheme) > 0:
            changed_something = True
            print "Setting new theme: %s" % ttheme
            Configurator.WIN_THEME = ttheme

        if changed_something:
            print "Saving new configuration settings"
            Configurator.save_config()

        if args.listthemes:
            from ttk import Style
            print "Available themes: " + str(', '.join(Style().theme_names()))
            exit(0)
    except Exception as err:
        print "Failed parsing arguments: " + str(err)

    try:
        from preprocessing import Main

        Main.main()
    except Exception as err:
        print "Exiting Bailey checker: ERROR: " + str(err)
        exit(1)
    print "Done"
