#  Copyright (c) 2020. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.

import io
from os.path import abspath
from typing import Union

from .. import _kbridge as kbr
from ..plot.core import PlotSpec
from ..plot.plot import GGBunch


def export_svg(plot: Union[PlotSpec, GGBunch], filename: str) -> str:
    """
    Exports plot or `bunch` of plots to file in SVG format.
    
    Parameters
    ----------
    plot: PlotSpec or GGBunch object
            Plot specification to export.
    filename: str
            Filename to save SVG under.
     Returns
    -------
        Absolute pathname of created SVG file.

    """
    if not (isinstance(plot, PlotSpec) or isinstance(plot, GGBunch)):
        raise ValueError("PlotSpec or GGBunch expected but was: {}".format(type(plot)))

    svg = kbr._generate_svg(plot.as_dict())
    with io.open(filename, mode="w", encoding="utf-8") as f:
        f.write(svg)

    return abspath(filename)