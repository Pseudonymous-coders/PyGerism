print "Setting graphing credentials"
import plotly
plotly.tools.set_credentials_file(username='', api_key='')
plotly.tools.set_config_file(world_readable=False, sharing='private')
from plotly import __version__
from plotly.offline import download_plotlyjs, init_notebook_mode, plot, iplot

print __version__ # requires version >= 1.9.0
print "They are set!"
