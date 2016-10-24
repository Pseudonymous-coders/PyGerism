import plotly.offline.offline as plot
import plotly.graph_objs as graffer


class BarRer:
    def __init__(self, title):
        self.bars = []

        self.layout = graffer.Layout(
            barmode='stack',
            title=title
        )

    def add_bar(self, x, y, name, **kwargs):
        self.bars.append(graffer.Bar(
            x=x,
            y=y,
            text=name,
            **kwargs
        ))

    def show(self):
        print "Showing graphs"
        # plot.image.save_as(self.bars, filename='a-simple-plot.png')
        plot.plot(self.bars)
