from os import system
from time import sleep


def command(commands):
    return 1 if system(commands) == 0 else 0


if __name__ == "__main__":
    print "Starting the installation of Bailey Checker..."
    sleep(2)
    total = 5
    made = 0
    made += command("sudo easy_install pip")
    made += command("sudo port install git +svn +doc +bash_completion +gitweb")
    made += command("git clone git://git.gnome.org/gtk-mac-bundler")
    made += command("cd gtk-mac-bundler")
    made += command("make install")
    made += command("pip install six")
    made += command("pip install numpy")
    made += command("pip install matplotlib")
    print "%d out of %d commands were successfully ran" % (total, made)
