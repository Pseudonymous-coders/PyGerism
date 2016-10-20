import numpy as np
import matplotlib.pyplot as plt


class BarRer:
    def __init__(self):
        self.number = 1
        self.nums = (0, 0)
        self.auth = ("", "")

    def set_nums(self, numss, auths):
        self.nums = numss
        self.auth = auths
        self.number = len(self.nums)

    def plot(self):
        plt.bar(self.auth, self.nums, (1 / 1.5), color="red")

    def show(self):
        self.show()

plot = BarRer()
plot.set_nums((1, 0, 1, 1), ("R", "G", "B", "D"))
plot.plot()
plot.show()

# Main
    # dictionary
        #author
            #cheating author
            #percent