from sys import argv

argv.append("py2exe")

from distutils.core import setup
import py2exe

setup(windows=['Main.py'])
