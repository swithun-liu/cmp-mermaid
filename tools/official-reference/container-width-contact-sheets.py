"""Create raster diagnostics and reviewed previews for the container-width capture."""
import json
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageOps
from skimage.metrics import structural_similarity

directory = Path(sys.argv[1] if len(sys.argv) > 1 else "build/container-width")
official = json.loads((directory / "official.json").read_text())
native = json.loads((directory / "native.json").read_text())
sheet = Image.new("RGB", (1600, len(official["cases"]) * 300), "#eeeeee")
draw = ImageDraw.Draw(sheet)
cases = []
for index, case in enumerate(official["cases"]):
    images = [
        Image.open(directory / f'{case["id"]}-{side}.png').convert("RGBA")
        for side in ["official", "native"]
    ]
    width = max(image.width for image in images)
    height = max(image.height for image in images)
    pixels = []
    for image in images:
        canvas = Image.new("RGB", (width, height), "white")
        canvas.paste(image, (0, 0), image)
        pixels.append(np.asarray(canvas))
    score = float(structural_similarity(*pixels, channel_axis=2, data_range=255))
    cases.append({
        "id": case["id"], "ssim": score,
        "officialSize": list(images[0].size), "nativeSize": list(images[1].size),
    })
    draw.text((10, index * 300 + 5), f'{case["id"]}  Official (left) / Native (right)', fill="black")
    for side, image in enumerate(images):
        # Only the preview shrinks; SSIM above retains original capture pixels.
        preview = ImageOps.contain(image.convert("RGB"), (760, 270))
        sheet.paste(preview, (20 + side * 800, index * 300 + 25))

sheets = []
for index, top in enumerate(range(0, sheet.height, 900)):
    name = f"contact-sheet-{index + 1}.png"
    sheet.crop((0, top, 1600, min(top + 900, sheet.height))).save(directory / name)
    sheets.append(name)
(directory / "raster.json").write_text(json.dumps({
    "officialGeneratedAt": official["generatedAt"],
    "nativeGeneratedAt": native["generatedAt"],
    "caseCount": len(cases), "screenshotCount": len(cases) * 2,
    "contactSheets": sheets, "cases": cases,
}, indent=2))
print(json.dumps({"caseCount": len(cases), "contactSheets": sheets}, indent=2))
