"""Create same-source contact sheets and raster diagnostics without resizing audit images."""
import json
import math
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFont
from skimage.metrics import structural_similarity

directory = Path(sys.argv[1] if len(sys.argv) > 1 else "build/mixed-formula-audit")
official = json.loads((directory / "official.json").read_text())
native = json.loads((directory / "native.json").read_text())
font = ImageFont.load_default(size=18)
cases = []
rows = []
column = 600
for entry in official["cases"]:
    case_id = entry["id"]
    images = [
        Image.open(directory / f"{case_id}-{side}.png").convert("RGBA")
        for side in ("native", "official")
    ]
    width = max(image.width for image in images)
    height = max(image.height for image in images)
    normalized = []
    for image in images:
        canvas = Image.new("RGB", (width, height), "white")
        canvas.paste(image, (0, 0), image)
        normalized.append(np.asarray(canvas))
    score = float(structural_similarity(*normalized, channel_axis=2, data_range=255))
    cases.append({"id": case_id, "ssim": score,
                  "nativeSize": list(images[0].size), "officialSize": list(images[1].size)})
    row = Image.new("RGB", (column * 2, height + 70), "#f7f8fb")
    draw = ImageDraw.Draw(row)
    draw.text((14, 8), f'{case_id}   Native', fill="#202936", font=font)
    draw.text((column + 14, 8), f'Official   SSIM {score:.4f}', fill="#202936", font=font)
    for index, image in enumerate(images):
        # A wide diagnostic preview is uniformly reduced only in the contact
        # sheet. The SSIM and geometry inputs above retain their original pixels.
        preview = image.copy()
        if preview.width > column - 28:
            preview.thumbnail((column - 28, height))
        row.paste(preview, (index * column + 14, 40), preview)
    rows.append(row)

sheets = []
for first in range(0, len(rows), 7):
    group = rows[first:first + 7]
    sheet = Image.new("RGB", (column * 2, sum(row.height for row in group)), "white")
    top = 0
    for row in group:
        sheet.paste(row, (0, top))
        top += row.height
    filename = f"contact-sheet-{first // 7 + 1}.png"
    sheet.save(directory / filename)
    sheets.append(filename)

(directory / "raster.json").write_text(json.dumps({
    "officialGeneratedAt": official["generatedAt"],
    "nativeGeneratedAt": native["generatedAt"],
    "caseCount": len(cases), "screenshotCount": len(cases) * 2,
    "contactSheets": sheets, "cases": cases,
}, indent=2))
print(json.dumps({"caseCount": len(cases), "contactSheets": sheets,
                  "minimumSSIM": min(case["ssim"] for case in cases)}, indent=2))
